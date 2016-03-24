package de.ars.daojones;

import de.ars.daojones.connections.ApplicationContext;
import de.ars.daojones.connections.Connection;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;

/**
 * This is an abstract class that holds an {@link ApplicationContext}
 * and provides utility methods for accessing connections.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class ConnectionFactory {

	private ApplicationContext context;

	/**
	 * Returns the {@link ApplicationContext}.
	 * @return the {@link ApplicationContext}
	 */
	public ApplicationContext getContext() {
		return context;
	}
	/**
	 * Sets the {@link ApplicationContext}.
	 * @param context the {@link ApplicationContext}
	 */
	public void setContext(ApplicationContext context) {
		this.context = context;
	}
	
	/**
	 * Returns the connection in the current {@link ApplicationContext}.
	 * @param <T> the type that the connection should handle
	 * @param c the class that the connection should handle
	 * @return the connection
	 * @throws DataAccessException if fetching the connection failed
	 */
	public <T extends Dao> Connection<T> getConnection(Class<T> c) throws DataAccessException {
		return Connection.get(getContext(), c);
	}
	
}
