package de.ars.daojones.connections;

import de.ars.daojones.annotations.model.DataSourceInfo;
import de.ars.daojones.runtime.Dao;

/**
 * Returns some metadata about a connection.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T> The Dao class the connection is responsible for.
 */
public interface ConnectionMetaData<T extends Dao> {

	/**
	 * Returns the data source configuration.
	 * @param t the Dao that should be operated by the connection
	 * @return the data source configuration
	 */
	public DataSourceInfo getDataSource(T t);
	/**
	 * Returns whether updating objects is allowed or not.
	 * @param t the Dao that should be operated by the connection
	 * @return true, if updating objects is allowed
	 */
	public boolean isUpdateAllowed(T t);
	/**
	 * Returns whether deleting objects is allowed or not.
	 * @param t the Dao that should be operated by the connection
	 * @return true, if deleting objects is allowed
	 */
	public boolean isDeleteAllowed(T t);
	/**
	 * Returns whether creating objects is allowed or not.
	 * @return true, if creating objects is allowed
	 */
	public boolean isCreateAllowed();
}
