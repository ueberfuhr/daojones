package de.ars.daojones.runtime.configuration.provider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.runtime.configuration.connections.Connection;
import de.ars.daojones.runtime.configuration.connections.ConnectionConfiguration;
import de.ars.daojones.runtime.configuration.connections.Credential;
import de.ars.daojones.runtime.configuration.connections.CredentialReference;
import de.ars.daojones.runtime.configuration.connections.ImportDeclaration;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModelImpl;

/**
 * Reads the connection configuration from a connection configuration XML
 * resource.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public class XmlConnectionConfigurationSource extends XmlConfigurationSource<ConnectionModel, ConnectionConfiguration> {

  private static final Messages bundle = Messages.create( "configuration.provider.XmlConnectionConfigurationSource" );

  private static int idCounter = 1;

  private final Set<Object> handledURLs;
  private final Set<Object> handledURLsOnThisWay;

  /**
   * Creates an instance.
   * 
   * @param application
   *          the application
   * @param resourceURL
   *          the URL to the XML resource
   */
  public XmlConnectionConfigurationSource( final String application, final URL resourceURL ) {
    this( application, new URLIOProvider( resourceURL ), new HashSet<Object>(), new HashSet<Object>() );
  }

  public XmlConnectionConfigurationSource( final String application, final IOProvider ioProvider ) {
    this( application, ioProvider, new HashSet<Object>(), new HashSet<Object>() );
  }

  protected XmlConnectionConfigurationSource( final String application, final IOProvider ioProvider,
          final ApplicationConfigurationSourceAdapter<ConnectionConfiguration> cache ) {
    super( application, ioProvider, cache );
    handledURLs = new HashSet<Object>();
    handledURLsOnThisWay = new HashSet<Object>();
  }

  private XmlConnectionConfigurationSource( final String application, final IOProvider ioProvider,
          final Set<Object> handledURLs, final Set<Object> handledURLsOnThisWay ) {
    super( application, ioProvider );
    this.handledURLs = handledURLs;
    this.handledURLsOnThisWay = handledURLsOnThisWay;
  }

  @Override
  protected URL getSchema() {
    return XmlConnectionConfigurationSource.class.getResource( "/META-INF/schemas/daojones-2.0-connections.xsd" );
  }

  @Override
  protected Class<? extends ConnectionConfiguration> getRootElementType() {
    return ConnectionConfiguration.class;
  }

  @Override
  protected Class<? extends ConnectionModel> getModelType() {
    return ConnectionModel.class;
  }

  /**
   * Reads out all imports from the given configuration and eliminates them by
   * loading the referenced configurations
   * 
   * @param configuration
   *          the configuration that contains the imports
   * @throws ConfigurationException
   * @throws IOException
   */
  protected void loadImported( final ConnectionConfiguration configuration ) throws ConfigurationException, IOException {
    final List<ImportDeclaration> imports = configuration.getImports();
    // resolve imports
    try {
      for ( final Iterator<ImportDeclaration> it = imports.iterator(); it.hasNext(); it.remove() ) {
        final ImportDeclaration importDeclaration = it.next();
        final String file = importDeclaration.getFile();
        final IOProvider importIOProvider = getIOProvider().getRelativeResource( file );
        if ( !handledURLsOnThisWay.contains( importIOProvider ) ) {
          if ( handledURLs.add( importIOProvider ) ) {
            final Set<Object> handledURLsOnThisWayCopy = new HashSet<Object>( handledURLsOnThisWay );
            handledURLsOnThisWayCopy.add( importIOProvider );
            final XmlConnectionConfigurationSource p = new XmlConnectionConfigurationSource( getApplication(),
                    importIOProvider, handledURLs, new HashSet<Object>( handledURLsOnThisWay ) );
            final ConnectionConfiguration importedConfiguration = p.readRootElement();
            // recursive !!!! -> do not resolve here!
            loadImported( importedConfiguration );
            configuration.getCredentials().addAll( importedConfiguration.getCredentials() );
            configuration.getConnections().addAll( importedConfiguration.getConnections() );
            handledURLsOnThisWay.add( importIOProvider );
          } else {
            // do not load again, no cycle, just imported twice
          }
        } else {
          throw new ConfigurationException( XmlConnectionConfigurationSource.bundle.get( "error.cycle",
                  importIOProvider ) );
        }
      }
    } catch ( final MalformedURLException e ) {
      throw new ConfigurationException( e );
    }

  }

  @Override
  protected ConnectionConfiguration readRootElement() throws ConfigurationException, IOException {
    try {
      return super.readRootElement();
    } finally {
      handledURLs.clear();
      handledURLsOnThisWay.clear();
    }
  }

  /**
   * Resolves all references within the given configuration. Typically, the
   * following steps should be done:<br/>
   * <ol>
   * <li>Load the referenced (<i>imported</i>) configurations (see
   * {@link #loadImported(ConnectionConfiguration)})</li>
   * <li>Resolve credential references.</li>
   * </ol>
   * After executing this method, the configuration does not contain any imports
   * and credential references.
   * 
   * @param configuration
   *          the configuration
   * @throws ConfigurationException
   */
  protected void resolve( final ConnectionConfiguration configuration ) throws ConfigurationException {
    // Handle imports
    try {
      loadImported( configuration );
    } catch ( final IOException e ) {
      throw new ConfigurationException( e );
    }

    // Handle credentials
    final Map<String, Credential> credentials = new HashMap<String, Credential>();
    for ( final Credential credential : configuration.getCredentials() ) {
      credentials.put( credential.getId(), credential );
    }
    // Handle connections
    for ( final Connection connection : configuration.getConnections() ) {
      // set id
      if ( null == connection.getId() ) {
        synchronized ( XmlConnectionConfigurationSource.class ) {
          connection.setId( "context.connection." + ( XmlConnectionConfigurationSource.idCounter++ ) );
        }
      }
      // set credential
      final CredentialReference credentialReference = connection.getCredentialReference();
      if ( null != credentialReference && null == connection.getCredential() ) {
        final Credential credential = credentials.get( credentialReference.getId() );
        if ( null != credential ) {
          connection.setCredential( credential );
        } else {
          throw new ConfigurationException( XmlConnectionConfigurationSource.bundle.get( "error.credential.notfound",
                  credentialReference ) );
        }
      }
    }
  }

  @Override
  protected ConfigurationProvider<ConnectionModel> createConfigurationProviderForModel(
          final ConnectionConfiguration root ) throws ConfigurationException {
    return new ConfigurationProvider<ConnectionModel>() {

      @Override
      public Iterable<ConnectionModel> readConfiguration() throws ConfigurationException {
        XmlConnectionConfigurationSource.this.resolve( root );
        final Collection<ConnectionModel> result = new HashSet<ConnectionModel>();
        for ( final Connection con : root.getConnections() ) {
          result.add( new ConnectionModelImpl( XmlConnectionConfigurationSource.this.getApplication(), con ) );
        }
        return result;
      }
    };
  }

  @Override
  public ConfigurationSource adaptTo( final String application ) throws ConfigurationException {
    return new XmlConnectionConfigurationSource( getApplication(), getIOProvider(), this );
  }

}
