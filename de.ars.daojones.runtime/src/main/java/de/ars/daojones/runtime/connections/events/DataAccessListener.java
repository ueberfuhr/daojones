package de.ars.daojones.runtime.connections.events;

/**
 * A listener that is used to listen for database access events.
 * DataAccessListeners are not invoked when a cached query result is read.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @param <T>
 *          the type this access listener should handle
 */
public interface DataAccessListener<T> {

  /**
   * This method is called when accessing the database begins.
   * 
   * @param event
   */
  public void accessBegins( DataAccessEvent<T> event );

  /**
   * This method is called when accessing the database finished successfully.
   * 
   * @param event
   */
  public void accessFinishedSuccessfully( DataAccessEvent<T> event );

  /**
   * This method is called when an error occured while accessing the database.
   * 
   * @param event
   * @param t
   *          the error
   */
  public void accessFinishedWithError( DataAccessEvent<T> event, Throwable t );

}
