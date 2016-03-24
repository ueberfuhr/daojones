package de.ars.daojones.runtime.connections;

import de.ars.daojones.runtime.configuration.context.ConnectionModel;

/**
 * A connection to a database that is responsible to read and write objects of a
 * special type.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @param <T>
 *          the bean type that is handled by this connection
 */
public interface Connection<T> extends Accessor<T> {

  /**
   * Returns the connection model.
   * 
   * @return the connection model
   */
  ConnectionModel getModel();

  /**
   * Returns some meta information about this connection.
   * 
   * @return the metadata
   */
  ConnectionMetaData<T> getMetaData();

  /**
   * Returns the {@link ConnectionProvider}.
   * 
   * @return the {@link ConnectionProvider}
   */
  ConnectionProvider getConnectionProvider();

}
