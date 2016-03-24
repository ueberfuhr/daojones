package de.ars.daojones.runtime.spi.cache;

import java.io.Serializable;

/**
 * An interface for a cache value.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 *            the cache value object type
 */
public interface CacheValue<T> extends Serializable {

	/**
	 * Returns the cached object.
	 * 
	 * @return the cached object
	 */
	public T getData();

	/**
	 * Returns a flag indicating whether this value is valid or not. If the
	 * value is not valid, the {@link Cache} does not return it when reading the
	 * cache.
	 * 
	 * @return true, if the value is valid
	 */
	public boolean isValid();

}
