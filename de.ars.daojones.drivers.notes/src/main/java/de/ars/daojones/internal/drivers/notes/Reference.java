package de.ars.daojones.internal.drivers.notes;

/**
 * A reference to an object.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the type of the referenced object
 */
public interface Reference<T> {

  /**
   * Returns the referenced object within the current scope.
   * 
   * @return the referenced object
   * @throws Exception
   */
  T get() throws Exception;

}
