package de.ars.daojones.runtime.connections.events;

/**
 * The types of events of a connection model.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public enum ConnectionEventType {

  /**
   * A connection was registered at the configuration model.
   */
  CONNECTION_MODEL_REGISTERED,
  /**
   * A connection was created.
   */
  CONNECTION_CREATED,
  /**
   * A connection gets closed.
   */
  CONNECTION_CLOSED,
  /**
   * A connection gets removed from the configuration model.
   */
  CONNECTION_MODEL_DEREGISTERED;

}
