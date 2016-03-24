package de.ars.daojones.runtime.context;

import java.io.Closeable;

import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.connections.events.ConnectionEventListener;

/**
 * The context for a single application. This was introduced for isolation
 * issues.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public interface Application extends ConnectionProvider, Closeable {

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
   * Closes a whole application.
   * 
   * @throws DataAccessException
   */
  @Override
  void close() throws DataAccessException;

  /**
   * Returns the injection engine.
   * 
   * @return the injection engine
   */
  public InjectionEngine getInjectionEngine();

}
