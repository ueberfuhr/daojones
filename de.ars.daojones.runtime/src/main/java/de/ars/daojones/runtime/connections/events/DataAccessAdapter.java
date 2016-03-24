package de.ars.daojones.runtime.connections.events;

/**
 * An adapter class for {@link DataAccessListener}
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 *          the type of the DaoJones bean
 */
public class DataAccessAdapter<T> implements DataAccessListener<T> {

  /**
   * @see DataAccessListener#accessBegins(DataAccessEvent)
   */
  public void accessBegins( final DataAccessEvent<T> event ) {
  }

  /**
   * @see DataAccessListener#accessFinishedSuccessfully(DataAccessEvent)
   */
  public void accessFinishedSuccessfully( final DataAccessEvent<T> event ) {
  }

  /**
   * @see DataAccessListener#accessFinishedWithError(DataAccessEvent, Throwable)
   */
  public void accessFinishedWithError( final DataAccessEvent<T> event, final Throwable t ) {
  }

}
