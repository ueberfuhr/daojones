package de.ars.daojones.internal.runtime.utilities;

import java.util.HashMap;
import java.util.Map;

/**
 * A hash map that is synchronized per-key and has a putIfAbsent method that
 * allows to create a value only if it does not already exist
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 * @param <K>
 *          the key type
 * @param <V>
 *          the value type
 * @param <E>
 *          the type of exception that is thrown when a value is created
 */
public class ConcurrentLazyHashMap<K, V, E extends Throwable> {

  private final Map<K, Object> locks = new HashMap<K, Object>();
  private final Map<K, V> values = new HashMap<K, V>();

  /**
   * The factory that creates the object.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
   * @since 2.0
   * @param <V>
   *          the value type
   * @param <E>
   *          the type of exception that is thrown when a value is created
   */
  public static interface ObjectFactory<V, E extends Throwable> {
    V create() throws E;
  }

  /**
   * Puts a value into the map, if not present.
   * 
   * @param key
   *          the key
   * @param value
   *          the object factory that creates the value, if not present
   * @return the value
   * @throws E
   *           if creating the value fails
   */
  public V putIfAbsent( final K key, final ObjectFactory<V, E> value ) throws E {
    // find lock object
    Object lock;
    synchronized ( locks ) {
      lock = locks.get( key );
      if ( null == lock ) {
        lock = new Object();
        locks.put( key, lock );
      }
    }
    V result;
    synchronized ( lock ) {
      result = values.get( key );
      if ( null == result ) {
        result = value.create();
        values.put( key, result );
      }
    }
    return result;
  }

  public void clear() {
    synchronized ( locks ) {
      values.clear();
      locks.clear();
    }
  }

}
