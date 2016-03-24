package de.ars.daojones.drivers.notes;

import de.ars.daojones.connections.ApplicationContext;
import de.ars.daojones.connections.Connection;
import de.ars.daojones.connections.ConnectionData;
import de.ars.daojones.connections.ConnectionFactory;
import de.ars.daojones.runtime.Dao;

/**
 * The driver implementation for the {@link ConnectionFactory} interface.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class NotesConnectionFactory implements ConnectionFactory {

	/**
	 * @see ConnectionFactory#createConnection(ApplicationContext, Class, ConnectionData)
	 */
	public <T extends Dao>Connection<T> createConnection(ApplicationContext ctx, Class<T> c, ConnectionData data) {
		return new NotesConnection<T>(ctx, data, c);
	}

}
