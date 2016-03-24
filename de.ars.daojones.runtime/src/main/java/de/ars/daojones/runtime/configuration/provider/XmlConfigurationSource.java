package de.ars.daojones.runtime.configuration.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;

import de.ars.daojones.internal.runtime.utilities.JAXBReader;
import de.ars.daojones.internal.runtime.utilities.JAXBWriter;
import de.ars.daojones.internal.runtime.utilities.Messages;

/**
 * Reads the configuration from a configuration XML resource.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 * @param <Model>
 *          the model type
 */
public abstract class XmlConfigurationSource<Model, XmlRoot> extends ApplicationConfigurationSourceAdapter<XmlRoot> {

  private static final Messages logger = Messages.create( "configuration.provider.XmlConfigurationSource" );

  /**
   * A common provider for I/O from and to the XML resource.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
   * @since 2.0
   */
  public static interface IOProvider {

    /**
     * Returns an input stream.
     * 
     * @return the input stream
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;

    /**
     * Returns an output stream.
     * 
     * @return the output stream
     * @throws IOException
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Returns a provider to a resource that is relative to this instance.
     * 
     * @param resource
     *          the resource
     * @return the provider
     * @throws IOException
     */
    IOProvider getRelativeResource( String resource ) throws IOException;

    @Override
    boolean equals( Object o );

    @Override
    int hashCode();

    @Override
    String toString();
  }

  /**
   * An I/O provider that reads the input stream from an url and writes the XML
   * to a file.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
   * @since 2.0
   */
  protected static class URLIOProvider implements IOProvider {

    private final URL url;

    public URLIOProvider( final URL url ) {
      super();
      if ( null == url ) {
        throw new IllegalArgumentException( "url must not be null!" );
      } else {
        this.url = url;
      }
    }

    @Override
    public InputStream getInputStream() throws IOException {
      return url.openStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
      try {
        final File target = new File( url.toURI() );
        if ( target.exists() ) {
          target.delete();
        }
        return new FileOutputStream( target );
      } catch ( final URISyntaxException e ) {
        throw new IOException( e );
      }
    }

    @Override
    public IOProvider getRelativeResource( final String resource ) throws IOException {
      return new URLIOProvider( new URL( url, resource ) );
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( url == null ) ? 0 : url.toExternalForm().hashCode() );
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
      final URLIOProvider other = ( URLIOProvider ) obj;
      if ( url == null ) {
        if ( other.url != null ) {
          return false;
        }
      } else if ( !url.toExternalForm().equals( other.url.toExternalForm() ) ) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return url.toString();
    }

  }

  private final IOProvider ioProvider;

  /**
   * Creates an instance.
   * 
   * @param application
   *          the application
   * @param resourceURL
   *          the URL to the XML resource
   */
  public XmlConfigurationSource( final String application, final URL resourceURL ) {
    this( application, new URLIOProvider( resourceURL ), null );
  }

  /**
   * Creates an instance.
   * 
   * @param application
   *          the application
   * @param ioProvider
   *          the I/O provider
   */
  public XmlConfigurationSource( final String application, final IOProvider ioProvider ) {
    this( application, ioProvider, null );
  }

  /**
   * Creates an instance.<br/>
   * <br/>
   * <b>Please note:</b><br/>
   * Subclasses should re-define this constructor with the protected scope. Do
   * not declare it as public. Invoke this constructor when implementing the
   * {@link #adaptTo(String)} method.
   * 
   * @param application
   *          the application
   * @param ioProvider
   *          the I/O provider
   * @param cache
   *          the cache
   */
  protected XmlConfigurationSource( final String application, final IOProvider ioProvider,
          final ApplicationConfigurationSourceAdapter<XmlRoot> cache ) {
    super( application, cache );
    this.ioProvider = ioProvider;
  }

  /**
   * Returns the URL to the XML schema.
   * 
   * @return the URL to the XML schema
   */
  protected abstract URL getSchema();

  /**
   * Returns the type of the XML root element.
   * 
   * @return the type of the XML root element
   */
  protected abstract Class<? extends XmlRoot> getRootElementType();

  /**
   * Returns the type of the model element.
   * 
   * @return the type of the model element
   */
  protected abstract Class<? extends Model> getModelType();

  /**
   * Returns the I/O provider.
   * 
   * @return the I/O provider
   */
  protected IOProvider getIOProvider() {
    return ioProvider;
  }

  protected JAXBReader.Callback getReaderCallback() {
    return null;
  }

  protected JAXBWriter.Callback getWriterCallback() {
    return null;
  }

  /**
   * Reads out the plain root element from the resource url. <br/>
   * <br/>
   * <b>Please note:</b><br/>
   * This method accesses the URL in each invocation. If you want to read the
   * cached instance, use {@link #getRootElement()} instead.
   * 
   * @return the plain root element
   * @throws ConfigurationException
   * @throws IOException
   */
  protected XmlRoot readRootElement() throws ConfigurationException, IOException {
    final Class<? extends XmlRoot> rootElementType = getRootElementType();
    final IOProvider iop = getIOProvider();
    if ( null == iop ) {
      throw new ConfigurationException( XmlConfigurationSource.logger.get( "error.noresource",
              rootElementType.getSimpleName() ) );
    }
    XmlConfigurationSource.logger.log( Level.INFO, "scan.start", rootElementType.getSimpleName(), iop.toString() );
    final InputStream in = iop.getInputStream();
    try {
      final URL schema = getSchema();
      final JAXBReader<XmlRoot> reader = new JAXBReader<XmlRoot>( rootElementType, schema );
      final XmlRoot root = reader.read( in, getReaderCallback() );
      return root;
    } finally {
      in.close();
    }
  }

  @Override
  protected XmlRoot readCache() throws ConfigurationException {
    try {
      return readRootElement();
    } catch ( final IOException e ) {
      throw new ConfigurationException( e );
    }
  }

  /**
   * Writes to root element to the resource url.
   * 
   * @param root
   *          the root element
   * @throws ConfigurationException
   * @throws IOException
   */
  public void writeRootElement( final XmlRoot root ) throws ConfigurationException, IOException {
    final Class<? extends XmlRoot> rootElementType = getRootElementType();
    final IOProvider iop = getIOProvider();
    if ( null == iop ) {
      throw new ConfigurationException( XmlConfigurationSource.logger.get( "error.noresource",
              rootElementType.getSimpleName() ) );
    }
    final OutputStream out = iop.getOutputStream();
    try {
      final URL schema = getSchema();
      final JAXBWriter<XmlRoot> writer = new JAXBWriter<XmlRoot>( rootElementType, schema );
      writer.write( root, out, getWriterCallback() );
      setCache( root );
    } finally {
      out.close();
    }
  }

  /**
   * Returns the root element. The root element is cached, so the resource url
   * is accessed in the first invocation. If you want to access the resource url
   * again, invoke {@link #clear()} before.
   * 
   * @return the root element
   * @throws ConfigurationException
   * @throws IOException
   */
  public XmlRoot getRootElement() throws ConfigurationException, IOException {
    return getCache( true );
  }

  /**
   * Creates the configuration provider for the model.
   * 
   * @param root
   *          the root element
   * @return the configuration provider for the model
   * @throws ConfigurationException
   */
  protected abstract ConfigurationProvider<Model> createConfigurationProviderForModel( XmlRoot root )
          throws ConfigurationException;

  /**
   * Returns the configuration provider for the model. This will lead to
   * accessing the XML source.
   * 
   * @return the configuration provider for the model
   * @throws ConfigurationException
   */
  protected ConfigurationProvider<Model> getConfigurationProviderForModel() throws ConfigurationException {
    try {
      final XmlRoot root = getRootElement();
      return createConfigurationProviderForModel( root );
    } catch ( final IOException e ) {
      throw new ConfigurationException( e );
    }
  }

  @SuppressWarnings( "unchecked" )
  @Override
  protected <T> ConfigurationProvider<T> getConfigurationProviderFor( final Class<T> modelClass )
          throws ConfigurationException {
    if ( modelClass.getName().equals( getModelType().getName() ) ) {
      return ( ConfigurationProvider<T> ) getConfigurationProviderForModel();
    } else {
      return super.getConfigurationProviderFor( modelClass );
    }
  }

}
