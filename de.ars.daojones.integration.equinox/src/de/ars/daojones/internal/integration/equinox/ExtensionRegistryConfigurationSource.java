package de.ars.daojones.internal.integration.equinox;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import de.ars.daojones.runtime.configuration.connections.Cache;
import de.ars.daojones.runtime.configuration.connections.Connection;
import de.ars.daojones.runtime.configuration.connections.Credential;
import de.ars.daojones.runtime.configuration.connections.Property;
import de.ars.daojones.runtime.configuration.context.ApplicationModel;
import de.ars.daojones.runtime.configuration.context.ApplicationModelImpl;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.CacheFactoryModel;
import de.ars.daojones.runtime.configuration.context.CallbackHandlerFactoryModel;
import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModelImpl;
import de.ars.daojones.runtime.configuration.context.GlobalConverterModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationProvider;
import de.ars.daojones.runtime.configuration.provider.ConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.XmlConfigurationSource.IOProvider;
import de.ars.daojones.runtime.configuration.provider.XmlConnectionConfigurationSource;
import de.ars.daojones.runtime.connections.events.ConnectionEventListener;
import de.ars.daojones.runtime.spi.cache.CacheFactory;
import de.ars.daojones.runtime.spi.database.ConnectionFactory;
import de.ars.daojones.runtime.spi.security.CallbackHandlerFactory;

/**
 * This class provides a configuration source that reads out the platform's
 * extension registry.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 */
public class ExtensionRegistryConfigurationSource implements ConfigurationSource {

  private static final String PLUGIN_ID = Activator.PLUGIN_ID;

  /**
   * The name of the extension to configure driver implementations.
   */
  private static final String EXTENSION_FACTORIES = ExtensionRegistryConfigurationSource.PLUGIN_ID + ".factories";
  /**
   * The name of the extension to configure connections.
   */
  private static final String EXTENSION_CONNECTIONS = ExtensionRegistryConfigurationSource.PLUGIN_ID + ".connections";

  private final IExtensionRegistry registry = Platform.getExtensionRegistry();

  private static class BundleFileProvider implements IOProvider {

    private final Bundle declaringBundle;
    private final IPath path;

    public BundleFileProvider( final Bundle declaringBundle, final IPath path ) {
      super();
      this.declaringBundle = declaringBundle;
      this.path = path;
    }

    @Override
    public InputStream getInputStream() throws IOException {
      return FileLocator.openStream( declaringBundle, path, true );
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
      throw new IOException( new UnsupportedOperationException() );
    }

    @Override
    public IOProvider getRelativeResource( final String resource ) throws IOException {
      return new BundleFileProvider( declaringBundle, path.append( resource ) );
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( declaringBundle == null ) ? 0 : declaringBundle.getSymbolicName().hashCode() );
      result = prime * result + ( ( path == null ) ? 0 : path.hashCode() );
      return result;
    }

    @Override
    public boolean equals( final Object obj ) {
      if ( this == obj ) {
        return true;
      }
      if ( obj == null ) {
        return false;
      }
      if ( getClass() != obj.getClass() ) {
        return false;
      }
      final BundleFileProvider other = ( BundleFileProvider ) obj;
      if ( declaringBundle == null ) {
        if ( other.declaringBundle != null ) {
          return false;
        }
      } else if ( !declaringBundle.getSymbolicName().equals( other.declaringBundle.getSymbolicName() ) ) {
        return false;
      }
      if ( path == null ) {
        if ( other.path != null ) {
          return false;
        }
      } else if ( !path.equals( other.path ) ) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "bundle://" + declaringBundle.getSymbolicName() + "/" + path.toString();
    }

  }

  /* *****************************************************************************************
   *                                                                                         *
   *                  CREATE FACTORY MODELS FROM FACTORIES EXTENSION POINTS                  *
   *                                                                                         *
   ***************************************************************************************** */

  @Override
  public ConfigurationProvider<CacheFactoryModel> getCacheFactoryModelConfigurationProvider()
          throws ConfigurationException {
    return new ConfigurationProvider<CacheFactoryModel>() {

      @Override
      public Iterable<CacheFactoryModel> readConfiguration() throws ConfigurationException {
        final List<CacheFactoryModel> result = new LinkedList<CacheFactoryModel>();

        final IConfigurationElement[] factoryElements = registry
                .getConfigurationElementsFor( ExtensionRegistryConfigurationSource.EXTENSION_FACTORIES );
        for ( final IConfigurationElement factoryElement : factoryElements ) {
          if ( "cache-factory".equals( factoryElement.getName() ) ) {
            final String id = factoryElement.getAttribute( "id" );
            final String name = factoryElement.getAttribute( "name" );
            final String description = factoryElement.getAttribute( "description" );
            try {
              // build instance
              final CacheFactory cf = ( CacheFactory ) factoryElement.createExecutableExtension( "class" );
              final CacheFactoryModel cfm = new CacheFactoryModel() {

                private static final long serialVersionUID = 1L;

                @Override
                public String getId() {
                  return id;
                }

                @Override
                public String getName() {
                  return name;
                }

                @Override
                public String getDescription() {
                  return description;
                }

                @Override
                public CacheFactory getInstance() {
                  return cf;
                }
              };
              result.add( cfm );
            } catch ( final CoreException e ) {
              Activator.logError( "Cache Factory \"" + name + "\" could not be instantiated!", e );
            }
          }
        }
        return result;

      }

    };
  }

  @Override
  public ConfigurationProvider<CallbackHandlerFactoryModel> getCallbackHandlerFactoryModelConfigurationProvider()
          throws ConfigurationException {
    return new ConfigurationProvider<CallbackHandlerFactoryModel>() {

      @Override
      public Iterable<CallbackHandlerFactoryModel> readConfiguration() throws ConfigurationException {
        final List<CallbackHandlerFactoryModel> result = new LinkedList<CallbackHandlerFactoryModel>();

        final IConfigurationElement[] factoryElements = registry
                .getConfigurationElementsFor( ExtensionRegistryConfigurationSource.EXTENSION_FACTORIES );
        for ( final IConfigurationElement factoryElement : factoryElements ) {
          if ( "callback-handler-factory".equals( factoryElement.getName() ) ) {
            final String id = factoryElement.getAttribute( "id" );
            final String name = factoryElement.getAttribute( "name" );
            final String description = factoryElement.getAttribute( "description" );
            try {
              // build instance
              final CallbackHandlerFactory cf = ( CallbackHandlerFactory ) factoryElement
                      .createExecutableExtension( "class" );
              final CallbackHandlerFactoryModel cfm = new CallbackHandlerFactoryModel() {

                private static final long serialVersionUID = 1L;

                @Override
                public String getId() {
                  return id;
                }

                @Override
                public String getName() {
                  return name;
                }

                @Override
                public String getDescription() {
                  return description;
                }

                @Override
                public CallbackHandlerFactory getInstance() {
                  return cf;
                }
              };
              result.add( cfm );
            } catch ( final CoreException e ) {
              Activator.logError( "Callback Handler Factory \"" + name + "\" could not be instantiated!", e );
            }
          }
        }
        return result;

      }

    };
  }

  @Override
  public ConfigurationProvider<ConnectionFactoryModel> getConnectionFactoryModelConfigurationProvider()
          throws ConfigurationException {
    return new ConfigurationProvider<ConnectionFactoryModel>() {

      @Override
      public Iterable<ConnectionFactoryModel> readConfiguration() throws ConfigurationException {
        final List<ConnectionFactoryModel> result = new LinkedList<ConnectionFactoryModel>();

        final IConfigurationElement[] factoryElements = registry
                .getConfigurationElementsFor( ExtensionRegistryConfigurationSource.EXTENSION_FACTORIES );
        for ( final IConfigurationElement factoryElement : factoryElements ) {
          if ( "connection-factory".equals( factoryElement.getName() ) ) {
            final String id = factoryElement.getAttribute( "id" );
            final String name = factoryElement.getAttribute( "name" );
            final String description = factoryElement.getAttribute( "description" );
            try {
              // build instance
              final ConnectionFactory cf = ( ConnectionFactory ) factoryElement.createExecutableExtension( "class" );
              final ConnectionFactoryModel cfm = new ConnectionFactoryModel() {

                private static final long serialVersionUID = 1L;

                @Override
                public String getId() {
                  return id;
                }

                @Override
                public String getName() {
                  return name;
                }

                @Override
                public String getDescription() {
                  return description;
                }

                @Override
                public ConnectionFactory getInstance() {
                  return cf;
                }
              };
              result.add( cfm );
            } catch ( final CoreException e ) {
              Activator.logError( "Connection Factory \"" + name + "\" could not be instantiated!", e );
            }
          }
        }
        return result;

      }

    };
  }

  /* *****************************************************************************************
   *                                                                                         *
   *                CREATE CONNECTION MODELS FROM CONNECTIONS EXTENSION POINTS               *
   *                                                                                         *
   ***************************************************************************************** */

  @Override
  public ConfigurationProvider<ApplicationModel> getApplicationModelConfigurationProvider()
          throws ConfigurationException {
    return new ConfigurationProvider<ApplicationModel>() {

      @Override
      public Iterable<ApplicationModel> readConfiguration() throws ConfigurationException {

        final List<ApplicationModel> result = new LinkedList<ApplicationModel>();
        final IConfigurationElement[] connectionElements = registry
                .getConfigurationElementsFor( ExtensionRegistryConfigurationSource.EXTENSION_CONNECTIONS );
        // create applications
        for ( final IConfigurationElement el : connectionElements ) {
          if ( "application".equals( el.getName() ) ) {
            final String id = el.getAttribute( "id" );
            final ApplicationModelImpl model = new ApplicationModelImpl( id );
            model.setName( el.getAttribute( "name" ) );
            model.setDescription( el.getAttribute( "description" ) );
            result.add( model );
          }
        }

        return result;
      }
    };
  }

  @Override
  public ConfigurationProvider<ConnectionModel> getConnectionModelConfigurationProvider() throws ConfigurationException {

    return new ConfigurationProvider<ConnectionModel>() {

      @Override
      public Iterable<ConnectionModel> readConfiguration() throws ConfigurationException {

        final List<ConnectionModel> result = new LinkedList<ConnectionModel>();

        final IConfigurationElement[] connectionElements = registry
                .getConfigurationElementsFor( ExtensionRegistryConfigurationSource.EXTENSION_CONNECTIONS );

        // read credentials
        final Map<String, Credential> credentials = new HashMap<String, Credential>();
        for ( final IConfigurationElement el : connectionElements ) {
          if ( "credential".equals( el.getName() ) ) {
            final Credential c = new Credential();
            c.setId( el.getAttribute( "id" ) );
            c.setType( el.getAttribute( "type" ) );
            for ( final IConfigurationElement property : el.getChildren() ) {
              if ( "property".equals( property.getName() ) ) {
                final Property p = new Property();
                p.setName( property.getAttribute( "name" ) );
                p.setValue( property.getAttribute( "value" ) );
                c.getProperties().add( p );
              }
            }
            credentials.put( c.getId(), c );
          }
        }

        // read connections
        for ( final IConfigurationElement el : connectionElements ) {
          if ( "connection".equals( el.getName() ) ) {
            // application
            final String application = el.getAttribute( "application" );
            // common attributes
            final Connection con = new Connection();
            con.setId( el.getAttribute( "id" ) );
            con.setName( el.getAttribute( "name" ) );
            con.setDescription( el.getAttribute( "description" ) );
            con.setType( el.getAttribute( "type" ) );
            con.setDatabase( el.getAttribute( "database" ) );
            con.setDefault( "true".equals( el.getAttribute( "default" ) ) );
            // credential
            final String credentialId = el.getAttribute( "credential" );
            if ( null != credentialId && credentialId.length() > 0 ) {
              final Credential credential = credentials.get( credentialId );
              if ( null != credential ) {
                con.setCredential( credential );
              } else {
                throw new ConfigurationException( "Cannot find credential with id \"" + credentialId + "\"!" );
              }
            }
            // caching, limitation, for-class
            for ( final IConfigurationElement child : el.getChildren() ) {
              if ( "cache".equals( child.getName() ) ) {
                final Cache cache = new Cache();
                cache.setType( child.getAttribute( "type" ) );
                for ( final IConfigurationElement property : child.getChildren() ) {
                  if ( "property".equals( property.getName() ) ) {
                    final Property p = new Property();
                    p.setName( property.getAttribute( "name" ) );
                    p.setValue( property.getAttribute( "value" ) );
                    cache.getProperties().add( p );
                  }
                }
                con.setCache( cache );
              } else if ( "limitation".equals( child.getName() ) ) {
                try {
                  con.setMaximumResults( Integer.valueOf( child.getAttribute( "maximum-results" ) ) );
                } catch ( final NumberFormatException e ) {
                  throw new ConfigurationException( "Invalid number format: \""
                          + child.getAttribute( "maximum-results" ) + "\"!" );
                }
              } else if ( "for-class".equals( child.getName() ) ) {
                con.getAssignedBeanTypes().add( child.getAttribute( "class" ) );
              }
            }
            result.add( new ConnectionModelImpl( application, con ) );
          } else if ( "connection-file".equals( el.getName() ) ) {
            // application
            final String application = el.getAttribute( "application" );
            // read file
            final String file = el.getAttribute( "file" );
            // create IOProvider
            final Bundle declaringBundle = Platform.getBundle( el.getContributor().getName() );
            final IOProvider ioProvider = new BundleFileProvider( declaringBundle, new Path( file ) );
            // create connection configuration source
            final XmlConnectionConfigurationSource xml = new XmlConnectionConfigurationSource( application, ioProvider );
            final ConfigurationProvider<ConnectionModel> cmcp = xml.getConnectionModelConfigurationProvider();
            // access file
            for ( final ConnectionModel cm : cmcp.readConfiguration() ) {
              result.add( cm );
            }
            Activator.logInfo( "Connections successfully read from file \"" + el.getAttribute( "filename" ) + "\"!" );
          }
        }

        return result;
      }
    };

  }

  @Override
  public ConfigurationProvider<ConnectionEventListener> getConnectionEventListenerConfigurationProvider()
          throws ConfigurationException {
    return new ConfigurationProvider<ConnectionEventListener>() {

      @Override
      public Iterable<ConnectionEventListener> readConfiguration() throws ConfigurationException {

        final List<ConnectionEventListener> result = new LinkedList<ConnectionEventListener>();
        final IConfigurationElement[] connectionElements = registry
                .getConfigurationElementsFor( ExtensionRegistryConfigurationSource.EXTENSION_CONNECTIONS );
        // create applications
        for ( final IConfigurationElement el : connectionElements ) {
          if ( "connection-event-listener".equals( el.getName() ) ) {
            try {
              // build instance
              final ConnectionEventListener listener = ( ConnectionEventListener ) el
                      .createExecutableExtension( "class" );
              result.add( listener );
            } catch ( final CoreException e ) {
              Activator.logError( "Connection Event Listener \"" + el.getAttribute( "class" )
                      + "\" could not be instantiated!", e );
            }
          }
        }

        return result;
      }
    };
  }

  /* *****************************************************************************************
   *                                                                                         *
   *                      CREATE BEAN MODELS FROM BEANS EXTENSION POINTS                     *
   *                                                                                         *
   ***************************************************************************************** */

  @Override
  public ConfigurationProvider<BeanModel> getBeanModelConfigurationProvider() throws ConfigurationException {
    // TODO Not yet supported
    return null;
  }

  @Override
  public ConfigurationProvider<GlobalConverterModel> getGlobalConverterModelConfigurationProvider()
          throws ConfigurationException {
    // TODO Not yet supported
    return null;
  }

}