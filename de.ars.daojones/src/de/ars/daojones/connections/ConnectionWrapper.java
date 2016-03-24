package de.ars.daojones.connections;

import de.ars.daojones.runtime.Dao;

/**
 * A simple interface for a connection wrapper.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 *
 * @param <T> the DaoJones bean type
 */
interface ConnectionWrapper<T extends Dao> {

	/**
	 * Returns the {@link Connection}.
	 * @return the {@link Connection}
	 */
	public Connection<T> getConnection();
	
}
