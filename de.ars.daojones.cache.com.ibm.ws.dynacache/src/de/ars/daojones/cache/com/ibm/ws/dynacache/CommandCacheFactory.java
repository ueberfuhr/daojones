package de.ars.daojones.cache.com.ibm.ws.dynacache;

import de.ars.daojones.cache.Cache;
import de.ars.daojones.cache.CacheException;
import de.ars.daojones.cache.CacheFactory;

/**
 * An implementation of {@link CacheFactory} using Websphere Command Cache.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class CommandCacheFactory implements CacheFactory {

	/**
	 * @see de.ars.daojones.cache.CacheFactory#createCache(java.lang.Class,
	 *      java.lang.Class)
	 */
	public <K, V> Cache<K, V> createCache(Class<K> k, Class<V> v)
			throws CacheException {
		return new CommandCache<K, V>();
	}

}
