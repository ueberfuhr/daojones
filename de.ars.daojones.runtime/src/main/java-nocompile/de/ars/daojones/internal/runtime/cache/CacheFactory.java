package de.ars.daojones.runtime.spi.cache;

import java.util.Properties;

/**
 * A common interface for a factory that creates caches.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface CacheFactory {

  /**
   * A transfer object that encapsulates all information that is available for
   * creating a cache instance.
   * 
   * @param <K>
   *          the key type
   * @param <V>
   *          the value type
   * @author ueberfuhr, ARS Computer und Consulting GmbH
   * @since 1.2.0
   */
  public interface CacheProperties<K, V> {

    /**
     * Returns the key class.
     * 
     * @return the key class
     */
    Class<K> getKeyClass();

    /**
     * Returns the value class.
     * 
     * @return the value class
     */
    Class<V> getValueClass();

    /**
     * Returns the application context id.
     * 
     * @return the application context id
     */
    String getApplicationContext();

    /**
     * Returns the connection id.
     * 
     * @return the connection id
     */
    String getConnectionId();

    /**
     * Returns a custom property.
     * 
     * @param key
     *          the custum property key
     * @return the custom property
     */
    String getProperty( String key );

  }

  /**
   * Default implementation of {@link CacheProperties}.
   * 
   * @param <K>
   *          the key type
   * @param <V>
   *          the value type
   * @author ueberfuhr, ARS Computer und Consulting GmbH
   * @since 1.2.0
   */
  public static class CachePropertiesImpl<K, V> implements
      CacheProperties<K, V> {

    private final String applicationContext;
    private final String connectionId;
    private final Class<K> keyClass;
    private final Class<V> valueClass;
    private final Properties properties;

    /**
     * Creates an instance.
     * 
     * @param applicationContext
     *          the application context
     * @param connectionId
     *          the connection od
     * @param keyClass
     *          the key class
     * @param valueClass
     *          the value class
     * @param properties
     *          the properties
     */
    public CachePropertiesImpl( final String applicationContext,
        final String connectionId, final Class<K> keyClass,
        final Class<V> valueClass, final Properties properties ) {
      super();
      this.applicationContext = applicationContext;
      this.connectionId = connectionId;
      this.keyClass = keyClass;
      this.valueClass = valueClass;
      this.properties = properties;
    }

    /**
     * @see de.ars.daojones.runtime.spi.cache.CacheFactory.CacheProperties#getKeyClass()
     */
    public Class<K> getKeyClass() {
      return keyClass;
    }

    /*
     * @see de.ars.daojones.runtime.cache.CacheFactory.CacheProperties#getValueClass()
     */
    public Class<V> getValueClass() {
      return valueClass;
    }

    /**
     * @see de.ars.daojones.runtime.spi.cache.CacheFactory.CacheProperties#getApplicationContext()
     */
    public String getApplicationContext() {
      return applicationContext;
    }

    /**
     * @see de.ars.daojones.runtime.spi.cache.CacheFactory.CacheProperties#getConnectionId()
     */
    public String getConnectionId() {
      return connectionId;
    }

    /**
     * @see de.ars.daojones.runtime.spi.cache.CacheFactory.CacheProperties#getProperty(java.lang.String)
     */
    public String getProperty( final String key ) {
      return null != properties ? properties.getProperty( key ) : null;
    }

  }

  /**
   * Creates a cache instance.
   * 
   * @param <K>
   *          the key type
   * @param <V>
   *          the value type
   * @param properties
   *          the cache properties
   * @return the {@link Cache}
   * @throws CacheException
   */
  public <K, V> Cache<K, V> createCache( CacheProperties<K, V> properties )
      throws CacheException;

}
