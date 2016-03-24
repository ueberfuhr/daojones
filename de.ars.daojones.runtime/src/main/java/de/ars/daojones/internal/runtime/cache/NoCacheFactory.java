package de.ars.daojones.internal.runtime.cache;

import java.io.Serializable;

import de.ars.daojones.runtime.spi.cache.Cache;
import de.ars.daojones.runtime.spi.cache.CacheAdapter;
import de.ars.daojones.runtime.spi.cache.CacheContext;
import de.ars.daojones.runtime.spi.cache.CacheException;
import de.ars.daojones.runtime.spi.cache.CacheFactory;

public class NoCacheFactory implements CacheFactory, Serializable {

  private static final long serialVersionUID = 1L;

  @Override
  public <K, V> Cache<K, V> createCache( final CacheContext<K, V> context ) throws CacheException {
    return new CacheAdapter<K, V>() {
      // empty anonymous class
    };
  }

}
