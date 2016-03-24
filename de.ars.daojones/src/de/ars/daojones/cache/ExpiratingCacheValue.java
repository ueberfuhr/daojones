package de.ars.daojones.cache;

/**
 * A {@link CacheValue} that expires after a couple of time.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 */
public class ExpiratingCacheValue<T> implements CacheValue<T> {

	private static final long serialVersionUID = 2703840116424903853L;
	private final CacheValue<T> delegate;
	private final long expirationDate;

	/**
	 * Creates an instance.
	 * 
	 * @param delegate
	 *            the original {@link CacheValue}
	 * @param expirationDate
	 *            the date of expiration
	 */
	public ExpiratingCacheValue(CacheValue<T> delegate, long expirationDate) {
		super();
		this.delegate = delegate;
		this.expirationDate = expirationDate;
	}

	/**
	 * @see de.ars.daojones.cache.CacheValue#isValid()
	 */
	public boolean isValid() {
		return delegate.isValid()
				&& expirationDate <= System.currentTimeMillis();
	}

	/**
	 * @see de.ars.daojones.cache.CacheValue#getData()
	 */
	public T getData() {
		return delegate.getData();
	}

}
