package de.ars.daojones.runtime.beans.identification;

import java.io.Serializable;

import de.ars.daojones.runtime.connections.Accessor;

/**
 * A common interface for objects that identify a single entry within the
 * database. The implementation is typically dependent from the database driver.
 * 
 * A bean can have an instance field of this type and stays serializable,
 * because an identificator has to be serializable too.
 * 
 * It is possible to use a bean with an identificator within multiple
 * applications, e.g. one application for reading the bean, and one application
 * for storing the bean. Each application then has its own bean model and its
 * own bean identification. Therefor, the application id must be given as
 * parameter when reading information out of the identificator.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface Identificator extends Serializable {

  /**
   * Returns the id. The id can be used for comparing objects with each other or
   * for searching the object later. Objects with the same id refer to the same
   * database content.<br/>
   * <br/>
   * <b>Please note:</b><br/>
   * The id has a string representation that you can get by invoking the
   * {@link Object#toString()} method on the id object. This string
   * representation can also be used to search for the object within the
   * database later.
   * 
   * @param application
   *          the application id
   * @return the id
   * @see Accessor#findById(Object)
   */
  Serializable getId( String application );

}
