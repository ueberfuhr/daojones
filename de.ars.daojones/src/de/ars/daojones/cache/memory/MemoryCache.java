package de.ars.daojones.cache.memory;

import java.util.HashMap;
import java.util.Map;

import de.ars.daojones.cache.Cache;
import de.ars.daojones.cache.CacheEntry;
import de.ars.daojones.cache.CacheException;
import de.ars.daojones.cache.CacheKey;
import de.ars.daojones.cache.CacheValue;
import de.ars.daojones.cache.RepeatableCommand;

/**
 * Default implementation of {@link Cache} that stores {@link CacheEntry}
 * objects within a {@link HashMap}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 */
public class MemoryCache<K, V> implements Cache<K, V> {

	private final Map<CacheKey<K>, CacheValue<V>> map = new HashMap<CacheKey<K>, CacheValue<V>>();
	private final Map<Integer, Object> locks = new HashMap<Integer, Object>();

	private Object getLock(Object key) {
		final int hashCode = null != key ? key.hashCode() : 0;
		synchronized (locks) {
			if (!locks.containsKey(hashCode))
				locks.put(hashCode, new Object());
		}
		return locks.get(hashCode);
	}

	/**
	 * @see de.ars.daojones.cache.Cache#put(CacheKey, RepeatableCommand,
	 *      boolean)
	 */
	public CacheValue<V> put(CacheKey<K> key,
			RepeatableCommand<CacheValue<V>> callable, boolean overwriteExisting)
			throws CacheException {
		try {
			synchronized (this) {
				synchronized (getLock(key)) {
					CacheValue<V> value = null;
					if (overwriteExisting || !map.containsKey(key)
							|| null != (value = map.get(key))
							&& !value.isValid()) {
						value = callable.call();
						map.put(key, value);
					}
					return value;
				}
			}
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}

	/**
	 * @see de.ars.daojones.cache.Cache#put(de.ars.daojones.cache.CacheKey,
	 *      de.ars.daojones.cache.RepeatableCommand)
	 */
	public CacheValue<V> put(CacheKey<K> key,
			RepeatableCommand<CacheValue<V>> callable) throws CacheException {
		return put(key, callable, false);
	}

	/**
	 * @see de.ars.daojones.cache.Cache#clear()
	 */
	public void clear() throws CacheException {
		synchronized (this) {
			this.locks.clear();
			this.map.clear();
		}
	}

}
