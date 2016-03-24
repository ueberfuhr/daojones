package de.ars.daojones.events;

import de.ars.daojones.runtime.Dao;

/**
 * A listener that is used to listen for database access events.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 *            the type this access listener should handle
 */
public interface DataAccessListener<T extends Dao> {

	/**
	 * This method is called when accessing the database begins.
	 * 
	 * @param event
	 */
	public void accessBegins(DataAccessEvent<T> event);

	/**
	 * This method is called when accessing the database finished successfully.
	 * 
	 * @param event
	 */
	public void accessFinishedSuccessfully(DataAccessEvent<T> event);

	/**
	 * This method is called when an error occured while accessing the database.
	 * 
	 * @param event
	 * @param t
	 *            the error
	 */
	public void accessFinishedWithError(DataAccessEvent<T> event, Throwable t);

}
