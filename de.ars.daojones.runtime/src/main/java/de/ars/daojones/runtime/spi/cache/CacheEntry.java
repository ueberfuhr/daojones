package de.ars.daojones.runtime.spi.cache;

import java.io.Serializable;

/**
 * A transfer object for a cache entry.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <K>
 *          the key object type
 * @param <V>
 *          the value object type
 */
public final class CacheEntry<K extends Serializable, V extends Serializable> {

  private final CacheKey<K> key;
  private final CacheValue<V> value;

  /**
   * Creates an instance.
   * 
   * @param key
   *          the key
   * @param value
   *          the value
   */
  public CacheEntry( CacheKey<K> key, CacheValue<V> value ) {
    super();
    this.key = key;
    this.value = value;
  }

  /**
   * Returns the key.
   * 
   * @return the key
   */
  public CacheKey<K> getKey() {
    return key;
  }

  /**
   * Returns the value.
   * 
   * @return the value
   */
  public CacheValue<V> getValue() {
    return value;
  }

}
