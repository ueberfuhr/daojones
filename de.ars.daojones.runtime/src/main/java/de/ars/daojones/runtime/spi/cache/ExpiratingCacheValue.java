package de.ars.daojones.runtime.spi.cache;

/**
 * A cache value that expires after a couple of time.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the value type
 */
public class ExpiratingCacheValue<T> implements CacheValue<T> {

  private static final long serialVersionUID = 2703840116424903853L;
  private final CacheValue<T> delegate;
  private final long expirationDate;

  /**
   * Creates an instance.
   * 
   * @param delegate
   *          the original {@link CacheValue}
   * @param expirationDate
   *          the date of expiration
   */
  public ExpiratingCacheValue( final CacheValue<T> delegate, final long expirationDate ) {
    super();
    this.delegate = delegate;
    this.expirationDate = expirationDate;
  }

  @Override
  public boolean isValid() {
    return delegate.isValid() && expirationDate >= System.currentTimeMillis();
  }

  @Override
  public T getData() {
    return delegate.getData();
  }

}
