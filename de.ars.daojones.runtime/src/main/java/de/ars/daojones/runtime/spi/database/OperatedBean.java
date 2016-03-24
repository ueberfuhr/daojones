package de.ars.daojones.runtime.spi.database;

import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;

/**
 * An object that encapsualtes information of and operations to a single bean
 * that is handled within an update or delete operation.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the bean type
 */
public interface OperatedBean<T> {

  /**
   * Returns the bean.
   * 
   * @return the bean
   */
  T getBean();

  /**
   * Returns the id of the bean.
   * 
   * @return the id of the bean
   * @throws ConfigurationException
   * @throws DataAccessException
   */
  Object getId() throws ConfigurationException, DataAccessException;

  /**
   * This method is invoked by the connection when a bean was updated.
   * 
   * @param entry
   *          the database entry
   * @throws DataAccessException
   */
  void operate( DatabaseEntry entry ) throws DataAccessException;

}
