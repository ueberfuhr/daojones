package de.ars.daojones.runtime;

import java.io.Serializable;
import java.util.Date;

import de.ars.daojones.FieldAccessor;

/**
 * An object that contains information where it was loaded. This is used to
 * reconnect a DaoJones bean to the database.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface DataObject extends Serializable {

	/**
	 * Returns an identificator for the object in the database.
	 * 
	 * @return the identificator
	 */
	public Identificator getIdentificator();

	/**
	 * Refreshes the information from the database. This method is not intended
	 * to be used by clients, they should use {@link Dao#refresh()} to clean
	 * cached fields.
	 * 
	 * @see Dao#refresh()
	 * @throws DataAccessException
	 *             if refreshing the field values fails
	 */
	public void refresh() throws DataAccessException;

	// TODO remove from the interface -> create Notes interface -> remove call
	// from the AbstractAccessor
	// /**
	// * Destroys the object from the database. This is called by the
	// * {@link Accessor} during the execution of the {@link
	// Accessor#delete(Dao)}
	// * method.
	// */
	// void destroy();

	/**
	 * Returns the last modified date of the object in the database.
	 * 
	 * @return the last modified date
	 */
	public Date getLastModified();

	/**
	 * Returns the field accessor for reading properties from the database.
	 * 
	 * @return the field accessor
	 */
	FieldAccessor getFieldAccessor();

}
