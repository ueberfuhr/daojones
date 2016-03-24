package de.ars.daojones.runtime.spi.cache;

import java.io.Serializable;

/**
 * Default implementation for {@link CacheKey}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 *          the key type
 */
public class DefaultCacheKey<T extends Serializable> implements CacheKey<T> {

  private static final long serialVersionUID = 1L;

  private final T t;

  /**
   * Creates an instance.
   * 
   * @param t
   *          the key object
   */
  public DefaultCacheKey( final T t ) {
    super();
    this.t = t;
  }

  /**
   * Returns the key object.
   * 
   * @return the key object
   */
  public T getData() {
    return this.t;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( t == null ) ? 0 : t.hashCode() );
    return result;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @SuppressWarnings( "rawtypes" )
  @Override
  public boolean equals( final Object obj ) {
    if ( this == obj ) {
      return true;
    }
    if ( obj == null ) {
      return false;
    }
    if ( getClass() != obj.getClass() ) {
      return false;
    }
    DefaultCacheKey other = ( DefaultCacheKey ) obj;
    if ( t == null ) {
      if ( other.t != null ) {
        return false;
      }
    } else if ( !t.equals( other.t ) ) {
      return false;
    }
    return true;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "DefaultCacheKey [t=" + t + "]";
  }

}
