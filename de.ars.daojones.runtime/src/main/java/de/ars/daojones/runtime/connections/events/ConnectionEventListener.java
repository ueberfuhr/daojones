package de.ars.daojones.runtime.connections.events;

/**
 * A listener for {@link ConnectionEvent}s.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public interface ConnectionEventListener {

  /**
   * Handles an event of the connection model.
   * 
   * @param event
   *          the {@link ConnectionEvent}
   */
  public void handle( ConnectionEvent event );

}
