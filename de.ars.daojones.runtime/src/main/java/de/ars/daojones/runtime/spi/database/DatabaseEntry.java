package de.ars.daojones.runtime.spi.database;

import java.io.Closeable;

import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.DatabaseAccessor;

/**
 * An single search result that is read from the database by the driver. The
 * driver has to implement this interface
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface DatabaseEntry extends Closeable, DatabaseAccessor {

  /**
   * Returns the bean model that corresponds to this entry.
   * 
   * @return the bean model
   */
  BeanModel getBeanModel();

  /**
   * Stores the changes to the object.
   * 
   * @throws DataAccessException
   */
  void store() throws DataAccessException;

  /**
   * Deletes the object.
   * 
   * @throws DataAccessException
   */
  void delete() throws DataAccessException;

}