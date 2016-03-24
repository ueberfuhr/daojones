package de.ars.daojones.internal.integration.eclipse.viewers.providers;

import org.eclipse.core.runtime.IAdaptable;

/**
 * A utility class providing helper methods for adapting objects
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 1.1.0
 */
public abstract class AdapableUtil {

  private AdapableUtil() {
    super();
  }

  /**
   * Adapts an object to a given type.
   * 
   * @param <T>
   *          the object type
   * @param o
   *          the object
   * @param c
   *          the object class
   * @return the adapted object or <code>null</code>
   */
  @SuppressWarnings( "unchecked" )
  public static <T> T adapt( Object o, Class<T> c ) {
    if ( null != o ) {
      if ( c.isAssignableFrom( o.getClass() ) ) {
        return ( T ) o;
      } else if ( o instanceof IAdaptable ) {
        return ( T ) ( ( IAdaptable ) o ).getAdapter( c );
      }
    }
    return null;
  }

}
