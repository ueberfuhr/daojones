package de.ars.daojones.internal.runtime.connections;

import de.ars.daojones.runtime.spi.cache.Cache;
import de.ars.daojones.runtime.spi.cache.CacheException;

public interface CacheBuilder {

  public <K, V> Cache<K, V> createCache( Class<K> keyType, Class<V> valueType ) throws CacheException;

}
