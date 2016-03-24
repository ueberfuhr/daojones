package de.ars.daojones.drivers.notes;

import lotus.domino.NotesException;
import de.ars.daojones.annotations.model.DataSourceInfo;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.DataObject;

/**
 * An extension of {@link DataObject} providing driver-specific methods.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
interface NotesDataObject extends DataObject {

	/**
	 * Returns the {@link DataSourceInfo}.
	 * 
	 * @return the {@link DataSourceInfo}
	 */
	public DataSourceInfo getDataSourceInfo();

	/**
	 * Destroys the object from the database. This is called by the
	 * {@link NotesAccessor} during the execution of the
	 * {@link NotesAccessor#doEventDelete(de.ars.daojones.runtime.Dao)} method.
	 * 
	 * @throws NotesException
	 */
	public void destroy() throws NotesException;
	
	/**
	 * Recycles the underlying references to Notes objects.
	 * @throws NotesException
	 */
	public void recycle() throws NotesException;
	
	/**
	 * Updates the underlying object.
	 * @throws NotesException
	 * @throws DataAccessException 
	 */
	public void update() throws NotesException, DataAccessException;

}
