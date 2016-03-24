package de.ars.daojones.drivers.notes;

import lotus.domino.Document;
import de.ars.daojones.annotations.DataSource;
import de.ars.daojones.annotations.model.DataSourceInfo;
import de.ars.daojones.connections.ApplicationContext;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.query.SearchCriterion;
import de.ars.daojones.runtime.query.VariableResolver;

/**
 * An interface providing methods that are called during query execution. The
 * behaviour is dependent from the kind of {@link DataSource}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
interface QueryAssistant {

	/**
	 * Creates a {@link VariableResolver}.
	 * 
	 * @param ds
	 *            the {@link DataSourceInfo}
	 * @param theGenericClass
	 *            the Dao class
	 * @return the {@link VariableResolver}
	 * @throws DataAccessException
	 */
	public VariableResolver createVariableResolver(DataSourceInfo ds,
			Class<? extends Dao> theGenericClass) throws DataAccessException;

	/**
	 * Creates a {@link SearchCriterion} to query for entries that are only part
	 * of the {@link DataSource}.
	 * 
	 * @param ds
	 *            the {@link DataSourceInfo}
	 * @param theGenericClass
	 *            the Dao class
	 * @return the {@link SearchCriterion} or null, if not necessary
	 * @throws DataAccessException
	 */
	public SearchCriterion createSearchCriterionForDataSource(
			DataSourceInfo ds, Class<? extends Dao> theGenericClass)
			throws DataAccessException;

	/**
	 * Creates a data object.
	 * 
	 * @param doc
	 *            the document that was found in the database or null, if a new
	 *            one should be created
	 * @param ds
	 *            the {@link DataSourceInfo}
	 * @param theGenericClass
	 *            the Dao class
	 * @param applicationContextId
	 *            the id of the {@link ApplicationContext}
	 * @return the {@link NotesDataObject}
	 * @throws DataAccessException
	 */
	public NotesDataObject createDataObject(Document doc, DataSourceInfo ds,
			Class<? extends Dao> theGenericClass, String applicationContextId)
			throws DataAccessException;

}
