package de.ars.daojones.drivers.notes;

/**
 * A common interface for an object that is used in the context of a class.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the key type
 */
public interface Aware<T> {

  /**
   * Returns the key type.
   * 
   * @return the key type
   */
  Class<? extends T> getKeyType();

}
