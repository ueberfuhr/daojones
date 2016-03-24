package de.ars.daojones.events;

import de.ars.daojones.runtime.Dao;

/**
 * An adapter class for {@link DataAccessListener}
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 *            the type of the DaoJones bean
 */
public class DataAccessAdapter<T extends Dao> implements DataAccessListener<T> {

	/**
	 * @see DataAccessListener#accessBegins(DataAccessEvent)
	 */
	public void accessBegins(DataAccessEvent<T> event) {
	}

	/**
	 * @see DataAccessListener#accessFinishedSuccessfully(DataAccessEvent)
	 */
	public void accessFinishedSuccessfully(DataAccessEvent<T> event) {
	}

	/**
	 * @see DataAccessListener#accessFinishedWithError(DataAccessEvent,
	 *      Throwable)
	 */
	public void accessFinishedWithError(DataAccessEvent<T> event, Throwable t) {
	}

}
