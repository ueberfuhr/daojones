package de.ars.daojones.runtime.spi.cache;


/**
 * A default implementation of {@link CacheValue}.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T> the value type
 */
public class DefaultCacheValue<T> implements CacheValue<T> {

	private static final long serialVersionUID = 878881751190664063L;
	private T data;
	
	
	/**
	 * Creates an instance.
	 * @param data the value object
	 */
	public DefaultCacheValue(T data) {
		super();
		this.data = data;
	}

	/**
	 * @see de.ars.daojones.runtime.spi.cache.CacheValue#getData()
	 */
	public T getData() {
		return data;
	}

	/**
	 * @see de.ars.daojones.runtime.spi.cache.CacheValue#isValid()
	 */
	public boolean isValid() {
		return true;
	}

}
