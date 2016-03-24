package de.ars.daojones.internal.runtime.configuration.context;

import java.util.HashMap;

/**
 * A map that creates a value if it does not exist during first request. The map
 * is synchronized.
 * 
 * @author ueberfuhr, ARS Computer und Consulting GmbH
 * @since 1.2.0
 */
abstract class FactoryMap<K, V> extends HashMap<K, V> {

  private static final long serialVersionUID = 1L;

  protected abstract V create( final K key );

  @SuppressWarnings( "unchecked" )
  @Override
  public V get( final Object key ) {
    synchronized ( key ) {
      V result = super.get( key );
      if ( null == result ) {
        final K kkey = ( K ) key;
        result = create( kkey );
        this.put( kkey, result );
      }
      return result;
    }
  }

}
