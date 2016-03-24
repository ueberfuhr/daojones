package de.ars.daojones.runtime.configuration.provider;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

/**
 * Application-dependent Adapter class for {@link ConfigurationSource}.<br/>
 * <br/>
 * <b>Please note:</b><br/>
 * The connection providers that are returned by the methods of this class
 * provide model elements that are partially application-dependent.
 * <ul>
 * <li>Connection Models</li>
 * <li>Global Converter Models</li>
 * <li>Bean Models</li>
 * </ul>
 * 
 * @param <Cache>
 *          the type of the cached model
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public abstract class ApplicationConfigurationSourceAdapter<Cache> extends ConfigurationSourceAdapter implements
        Closeable {

  /**
   * A simple reference to the cached object.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
   * @since 2.0
   * @param <Cache>
   *          the type of the cached model
   */
  private static class CacheProvider<Cache> implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient Cache cache;

    /**
     * Returns the cached object.
     * 
     * @return the cached object
     */
    public Cache getCache() {
      return cache;
    }

    /**
     * Sets the cached object.
     * 
     * @param cache
     *          the cached object
     */
    public void setCache( final Cache cache ) {
      this.cache = cache;
    }

  }

  private final String application;
  private final CacheProvider<Cache> cacheProvider;

  /**
   * Constructor.
   * 
   * @param application
   *          the application id
   */
  protected ApplicationConfigurationSourceAdapter( final String application ) {
    this( application, new CacheProvider<Cache>() );
  }

  private ApplicationConfigurationSourceAdapter( final String application, final CacheProvider<Cache> cacheProvider ) {
    super();
    this.application = application;
    this.cacheProvider = cacheProvider;
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
   * @param cacheProvider
   *          the application configuration source adapter that provides the
   *          configuration
   */
  protected ApplicationConfigurationSourceAdapter( final String application,
          final ApplicationConfigurationSourceAdapter<Cache> cacheProvider ) {
    this( application, null != cacheProvider ? cacheProvider.getCacheProvider() : new CacheProvider<Cache>() );
  }

  /**
   * Returns the application.
   * 
   * @return the application
   */
  public String getApplication() {
    return application;
  }

  protected abstract Cache readCache() throws ConfigurationException;

  /**
   * Returns the cached model instance.
   * 
   * @param readIfEmpty
   *          <tt>true</tt>, if the cached instance should be read, otherwise
   *          <tt>false</tt>
   * @return the cached model instance
   * @throws ConfigurationException
   */
  protected Cache getCache( final boolean readIfEmpty ) throws ConfigurationException {
    synchronized ( this.cacheProvider ) {
      if ( null == this.cacheProvider.getCache() && readIfEmpty ) {
        this.cacheProvider.setCache( readCache() );
      }
    }
    return this.cacheProvider.getCache();
  }

  /**
   * Returns the cache provider.
   * 
   * @return the cache provider
   */
  private CacheProvider<Cache> getCacheProvider() {
    return this.cacheProvider;
  }

  /**
   * Sets the cached model instance.
   * 
   * @param cache
   *          the cached model instance
   */
  public void setCache( final Cache cache ) {
    this.cacheProvider.setCache( cache );
  }

  /**
   * Removes the cached instance, which will lead to reading the XML source
   * again when requesting any of the supported configuration providers.
   */
  public void clear() {
    setCache( null );
  }

  @Override
  public void close() throws IOException {
    clear();
  }

  /**
   * Returns a connection source that provides the same model elements for the
   * given application.
   * 
   * @param application
   *          the application
   * @return the connection source
   * @throws ConfigurationException
   */
  public abstract ConfigurationSource adaptTo( String application ) throws ConfigurationException;

}
