package de.ars.daojones.cache;

import java.util.concurrent.Callable;

/**
 * A common cache interface.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <K>
 *            the key object type
 * @param <V>
 *            the value object type
 */
public interface Cache<K, V> {

	/**
	 * Puts a value into the cache using a callback mechanism. If the entry does
	 * not yet exist, the {@link Callable} object is executed. The result of the
	 * lookup is return. This method is thread-safe.
	 * 
	 * This method should call
	 * {@link #put(CacheKey, RepeatableCommand, boolean)} with the last
	 * parameter having the value false.
	 * 
	 * @param key
	 *            the key
	 * @param callable
	 *            the {@link RepeatableCommand} that is only executed if the
	 *            entry does not yet exist
	 * @return the cached value
	 * @throws CacheException
	 *             if an error occured during cache access
	 */
	public CacheValue<V> put(CacheKey<K> key,
			RepeatableCommand<CacheValue<V>> callable) throws CacheException;

	/**
	 * Puts a value into the cache using a callback mechanism. If the entry does
	 * not yet exist, the {@link Callable} object is executed. The result of the
	 * lookup is return. This method is thread-safe.
	 * 
	 * @param key
	 *            the key
	 * @param callable
	 *            the {@link RepeatableCommand} that is only executed if the
	 *            entry does not yet exist
	 * @param overwriteExisting
	 *            a flag indicating whether the value should be read from the
	 *            database, if it already exists in the cache.
	 * @return the cached value
	 * @throws CacheException
	 *             if an error occured during cache access
	 */
	public CacheValue<V> put(CacheKey<K> key,
			RepeatableCommand<CacheValue<V>> callable, boolean overwriteExisting)
			throws CacheException;

	/**
	 * Removes all cached entries from the cache.
	 * 
	 * @throws CacheException
	 *             if an error occured during cache access
	 */
	public void clear() throws CacheException;

}
