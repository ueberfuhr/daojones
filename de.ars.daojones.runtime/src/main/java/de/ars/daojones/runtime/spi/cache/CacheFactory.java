package de.ars.daojones.runtime.spi.cache;

/**
 * A common interface for a factory that creates caches.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface CacheFactory {

  /**
   * Creates a cache instance.
   * 
   * @param <K>
   *          the key type
   * @param <V>
   *          the value type
   * @param context
   *          the cache context
   * @return the {@link Cache}
   * @throws CacheException
   */
  public <K, V> Cache<K, V> createCache( CacheContext<K, V> context ) throws CacheException;

}
