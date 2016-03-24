package de.ars.daojones.runtime.spi.cache;

/**
 * An exception that occurs when accessing the cache.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class CacheException extends Exception {

	private static final long serialVersionUID = 8832426336206889042L;

	/**
	 * @see Exception#Exception()
	 */
	public CacheException() {
		super();
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public CacheException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public CacheException(Throwable cause) {
		super(cause);
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public CacheException(String message, Throwable cause) {
		super(message, cause);
	}

}
