package de.ars.daojones.runtime.query;

import java.util.Collection;

/**
 * A kind of comparison comparing collections and arrays.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public enum CollectionComparison implements Comparison<Collection<?>> {

  /**
   * The equals operator.
   */
  EQUALS {

    @Override
    public boolean matches( final Collection<?> left, final Collection<?> right ) {
      return null == left && null == right || null != left && null != right && left.containsAll( right )
              && right.containsAll( left );
    }

  },
  /**
   * The contains all operator.
   */
  CONTAINS_ALL {

    @Override
    public boolean matches( final Collection<?> left, final Collection<?> right ) {
      return null != left && null != right && left.containsAll( right );
    }

  },
  /**
   * The contains one operator.
   */
  CONTAINS_ONE {

    @Override
    public boolean matches( final Collection<?> left, final Collection<?> right ) {
      if ( null != left && null != right ) {
        for ( final Object o : right ) {
          if ( left.contains( o ) ) {
            return true;
          }
        }
      }
      return false;
    }

  };

  @SuppressWarnings( { "unchecked", "rawtypes" } )
  @Override
  public Class<Collection<?>> getType() {
    return ( Class ) Collection.class;
  }
}
