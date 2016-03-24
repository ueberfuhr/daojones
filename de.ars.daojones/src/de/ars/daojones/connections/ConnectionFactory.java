package de.ars.daojones.connections;

import de.ars.daojones.runtime.Dao;

/**
 * A factory that creates connection instances.
 * This is the central interface that must be implemented by a driver.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface ConnectionFactory {

	/**
	 * Creates a connection instance based on the given parameters.
	 * @param <T> the type of the objects that are handled by the connection.
	 * @param ctx the {@link ApplicationContext}
	 * @param c the class object of the objects that are handled by the connection
	 * @param data the connection information
	 * @return the connection instance
	 */
	public <T extends Dao>Connection<T> createConnection(ApplicationContext ctx, Class<T> c, ConnectionData data);
	
}
