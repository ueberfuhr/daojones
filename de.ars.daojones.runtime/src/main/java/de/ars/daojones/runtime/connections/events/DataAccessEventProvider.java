package de.ars.daojones.runtime.connections.events;

import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.DataAccessException;

/**
 * An abstract class providing events for {@link DataAccessListener}. Create
 * subclasses to inherit this functionality.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 * @param <T>
 *          the type of the DaoJones bean
 */
public abstract class DataAccessEventProvider<T> {

  /* ***********************************
   *  L I S T E N E R   M E T H O D S  *
   *********************************** */

  /**
   * Returns the {@link Connection}.
   * 
   * @return the {@link Connection}
   */
  protected abstract Connection<T> getConnection();

  /**
   * Adds a {@link DataAccessListener}.
   * 
   * @param listener
   *          the {@link DataAccessListener}
   * @return a flag indicating whether the {@link DataAccessListener} was added
   *         or not
   */
  public boolean addDataAccessListener( final DataAccessListener<T> listener ) {
    return DataAccessListenerRegistry.getInstance().addDataAccessListener( this.getConnection(), listener );
  }

  /**
   * Removes a {@link DataAccessListener}.
   * 
   * @param listener
   *          the {@link DataAccessListener}
   * @return a flag indicating whether the {@link DataAccessListener} was
   *         removed or not
   */
  public boolean removeDataAccessListener( final DataAccessListener<T> listener ) {
    return DataAccessListenerRegistry.getInstance().removeDataAccessListener( this.getConnection(), listener );
  }

  /**
   * Fires the event that the database access begins.
   * 
   * @param event
   *          the event
   */
  protected void fireAccessBegins( final DataAccessEvent<T> event ) {
    DataAccessListenerRegistry.getInstance().fireAccessBegins( event );
  }

  /**
   * Fires the event that the database access finished successfully.
   * 
   * @param event
   *          the event
   */
  protected void fireAccessFinishedSuccessfully( final DataAccessEvent<T> event ) {
    DataAccessListenerRegistry.getInstance().fireAccessFinishedSuccessfully( event );
  }

  /**
   * Fires the event that the database access finished with errors.
   * 
   * @param event
   *          the event
   * @param t
   *          the exception that occured
   */
  protected void fireAccessFinishedWithError( final DataAccessEvent<T> event, final Throwable t ) {
    DataAccessListenerRegistry.getInstance().fireAccessFinishedWithError( event, t );
  }

  /* *************************************
   *  E X E C U T I N G   M E T H O D S  *
   ************************************* */

  /**
   * Closes this provider including event firing.
   * 
   * @throws DataAccessException
   */
  protected void close() throws DataAccessException {
    final DataAccessEvent<T> event = new DataAccessEvent<T>( DataAccessType.DISCONNECT, this.getConnection(), null );
    fireAccessBegins( event );
    try {
      doEventCloseConnection();
      fireAccessFinishedSuccessfully( event );
    } catch ( DataAccessException e ) {
      fireAccessFinishedWithError( event, e );
      throw e;
    }
  }

  /**
   * Executes the close event between event firing.
   * 
   * @throws DataAccessException
   */
  protected abstract void doEventCloseConnection() throws DataAccessException;

}
