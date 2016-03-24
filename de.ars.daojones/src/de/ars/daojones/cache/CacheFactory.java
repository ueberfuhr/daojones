package de.ars.daojones.cache;


/**
 * A common interface for a factory that creates caches.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface CacheFactory {

	/**
	 * Creates a cache instance.
	 * 
	 * @param <K>
	 *            the key type
	 * @param <V>
	 *            the value type
	 * @param k
	 *            the key class
	 * @param v
	 *            the value class
	 * @return the {@link Cache}
	 * @throws CacheException
	 */
	public <K, V> Cache<K, V> createCache(Class<K> k, Class<V> v)
			throws CacheException;

}
