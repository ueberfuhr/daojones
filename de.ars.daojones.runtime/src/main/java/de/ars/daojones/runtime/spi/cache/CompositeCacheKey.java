package de.ars.daojones.runtime.spi.cache;

import java.io.Serializable;
import java.util.Arrays;

/**
 * An implementation of {@link CacheKey} combining multiple key objects.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class CompositeCacheKey implements CacheKey<Serializable[]> {

  private static final long serialVersionUID = 6283711359695164637L;
  private Serializable[] objects;

  /**
   * Creates an instance
   * 
   * @param objects
   *          the key objects
   */
  public CompositeCacheKey( final Serializable... objects ) {
    super();
    this.objects = objects;
  }

  /**
   * @see de.ars.daojones.runtime.spi.cache.CacheKey#getData()
   */
  @Override
  public Serializable[] getData() {
    return objects;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode( objects );
    return result;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
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
    final CompositeCacheKey other = ( CompositeCacheKey ) obj;
    if ( !Arrays.equals( objects, other.objects ) ) {
      return false;
    }
    return true;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Composite Cache Key [objects=" + Arrays.toString( objects ) + "]";
  }

}
