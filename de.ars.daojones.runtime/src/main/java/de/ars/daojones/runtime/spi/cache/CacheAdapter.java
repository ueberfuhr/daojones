package de.ars.daojones.runtime.spi.cache;

import java.util.concurrent.Callable;

/**
 * A default cache implementation where all methods are empty and return a
 * default value. This can be used as subclass for cache implementations to stay
 * compatible with DaoJones in case of interface additions.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <K>
 *          the key object type
 * @param <V>
 *          the value object type
 */
public abstract class CacheAdapter<K, V> implements Cache<K, V> {

  @Override
  public CacheValue<V> execute( final CacheKey<K> key, final Callable<V> callable ) throws CacheException {
    try {
      return new DefaultCacheValue<V>( callable.call() );
    } catch ( final CacheException e ) {
      throw e;
    } catch ( final Exception e ) {
      throw new CacheException( e );
    }
  }

}
