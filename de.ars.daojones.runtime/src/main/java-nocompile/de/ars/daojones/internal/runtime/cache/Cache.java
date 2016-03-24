package de.ars.daojones.runtime.spi.cache;

import java.util.concurrent.Callable;

/**
 * A common cache interface.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <K>
 *          the key object type
 * @param <V>
 *          the value object type
 */
public interface Cache<K, V> {

  /**
   * Reads a value. If the entry does not yet exist or is invalid, the
   * {@link Callable} object is executed. The result of the lookup is returned.
   * This method is thread-safe.
   * 
   * This method should call {@link #put(CacheKey, RepeatableCommand, boolean)}
   * with the last parameter having the value false.
   * 
   * @param key
   *          the key
   * @param callable
   *          the {@link RepeatableCommand} that is only executed if the entry
   *          does not yet exist
   * @return the cached value
   * @throws CacheException
   *           if an error occured during cache access
   */
  public CacheValue<V> execute( CacheKey<K> key,
      RepeatableCommand<CacheValue<V>> callable ) throws CacheException;

}
