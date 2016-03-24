package de.ars.daojones.runtime.test.driver;

import java.util.HashMap;
import java.util.Map;

import de.ars.daojones.connections.ApplicationContext;
import de.ars.daojones.connections.Connection;
import de.ars.daojones.connections.ConnectionData;
import de.ars.daojones.connections.ConnectionFactory;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.test.internal.driver.TestConnection;

/**
 * A {@link ConnectionFactory} creating connections to a dummy database.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class TestConnectionFactory implements ConnectionFactory {

    private final Map<String, Connection<?>> connectionInstances = new HashMap<String, Connection<?>>();

    /**
     * @see ConnectionFactory#createConnection(ApplicationContext, Class,
     *      ConnectionData)
     */
    @SuppressWarnings("unchecked")
    public <T extends Dao> Connection<T> createConnection(
            ApplicationContext ctx, Class<T> c, ConnectionData data) {
        synchronized (connectionInstances) {
            if (!connectionInstances.containsKey(c.getName())) {
                connectionInstances.put(c.getName(), new TestConnection<T>(ctx,
                        c, data));
            }
        }
        return (Connection<T>) connectionInstances.get(c.getName());
    }

}
