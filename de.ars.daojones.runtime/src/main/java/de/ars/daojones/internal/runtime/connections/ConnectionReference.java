package de.ars.daojones.internal.runtime.connections;

import java.io.Serializable;

import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.DataAccessException;

/**
 * An object containing methods to find connections to the training-specific
 * classes. Do not implement this by storing the connections as fields.
 * 
 * @see Serializable
 * @param <T>
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface ConnectionReference<T> extends Serializable {

  /**
   * Returns a connection.
   * 
   * @return the connection
   * @throws DataAccessException
   */
  public Connection<T> getConnection() throws DataAccessException;

}
