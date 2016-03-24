package de.ars.daojones.cache.memory;

import de.ars.daojones.cache.Cache;
import de.ars.daojones.cache.CacheException;
import de.ars.daojones.cache.CacheFactory;

/**
 * Default implementation for {@link CacheFactory} caching entries using the
 * memory.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class MemoryCacheFactory implements CacheFactory {

	/**
	 * @see de.ars.daojones.cache.CacheFactory#createCache(java.lang.Class,
	 *      java.lang.Class)
	 */
	public <K, V> Cache<K, V> createCache(Class<K> k, Class<V> v)
			throws CacheException {
		return new MemoryCache<K, V>();
	}

}
