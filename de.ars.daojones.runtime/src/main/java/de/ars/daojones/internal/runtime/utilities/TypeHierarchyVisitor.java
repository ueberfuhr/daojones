package de.ars.daojones.internal.runtime.utilities;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A visitor of the hierarchy of a given type.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public abstract class TypeHierarchyVisitor {

  /**
   * This method is invoked for each type within the hierarchy. It is first
   * invoked for the type itself.
   * 
   * @param c
   *          the type within the type hierarchy
   * @return <tt>true</tt> if visiting the type hierarchy has to continue,
   *         <tt>false</tt> if the process has to get stopped
   */
  protected abstract boolean visit( Class<?> c );

  /**
   * Executes visiting the hierarchy.
   * 
   * @param c
   *          the type
   */
  public void accept( final Class<?> c ) {
    acceptInternal( c );
  }

  private boolean acceptInternal( final Class<?>... classes ) {
    for ( final Class<?> c : classes ) {
      if ( !visit( c ) ) {
        return false;
      }
      final List<Class<?>> candidates = new LinkedList<Class<?>>();
      candidates.add( c.getSuperclass() );
      candidates.addAll( Arrays.asList( c.getInterfaces() ) );
      candidates.remove( null ); // no super class
      if ( !acceptInternal( candidates.toArray( new Class[candidates.size()] ) ) ) {
        return false;
      }
    }
    return true;
  }

}
