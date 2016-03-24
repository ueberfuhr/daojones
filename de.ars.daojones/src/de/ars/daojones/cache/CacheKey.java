package de.ars.daojones.cache;

import java.io.Serializable;

/**
 * An interface for a cache key. The CacheKey has to implement the
 * {@link #equals(Object)} and {@link #hashCode()} methods.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 *            the cache key object type
 */
public interface CacheKey<T> extends Serializable {

	/**
	 * Returns the key object.
	 * 
	 * @return the key object
	 */
	public T getData();

}
