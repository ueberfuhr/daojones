package de.ars.daojones.runtime.context;

import java.io.Closeable;

import de.ars.daojones.runtime.configuration.context.DaoJonesContextConfiguration;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.connections.events.ConnectionEventListener;

/**
 * An object providing context information of the DaoJones runtime to
 * <ul>
 * <li>get connections</li>
 * <li>read the configuration</li>
 * <li>configure the context</li>
 * </ul>
 * A singleton instance of this type is provided as an OSGi service.
 * 
 * @author ueberfuhr, ARS Computer und Consulting GmbH
 */
public interface DaoJonesContext extends Closeable {

  /**
   * Returns the configuration.
   * 
   * @return the configuration
   */
  public DaoJonesContextConfiguration getConfiguration();

  /**
   * Returns the context for a single application. If there isn't any
   * application context with this id, a connection provider is returned that
   * does not provide any connection.
   * 
   * @param applicationId
   *          the application id
   * @return the connection provider
   */
  public Application getApplication( final String applicationId );

  /**
   * Adds a connection event listener.
   * 
   * @param l
   *          the connection event listener
   */
  public void addConnectionEventListener( final ConnectionEventListener l );

  /**
   * Removes a connection event listener.
   * 
   * @param l
   *          the connection event listener
   */
  public void removeConnectionEventListener( final ConnectionEventListener l );

  /**
   * Closes a whole context.
   * 
   * @throws DataAccessException
   */
  @Override
  void close() throws DataAccessException;

}
