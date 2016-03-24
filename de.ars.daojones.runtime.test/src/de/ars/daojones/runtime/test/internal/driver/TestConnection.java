package de.ars.daojones.runtime.test.internal.driver;

import de.ars.daojones.connections.Accessor;
import de.ars.daojones.connections.ApplicationContext;
import de.ars.daojones.connections.Connection;
import de.ars.daojones.connections.ConnectionData;
import de.ars.daojones.connections.ConnectionMetaData;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.Identificator;

/**
 * A {@link Connection} accessing a local test file.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class TestConnection<T extends Dao> extends Connection<T> {

    private final Class<T> theGenericClass;
    private final ConnectionData connectionData;
    
    /**
     * Creates an instance.
     * @param ctx the {@link ApplicationContext}
     * @param theGenericClass the bean class
     * @param connectionData the {@link ConnectionData}
     */
    public TestConnection(ApplicationContext ctx, Class<T> theGenericClass, ConnectionData connectionData) {
        super(ctx);
        this.theGenericClass = theGenericClass;
        this.connectionData = connectionData;
    }

    @Override
    public Accessor<T> getAccessor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConnectionData getConnectionData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Identificator getIdentificator(String id) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConnectionMetaData<T> getMetaData() {
        // TODO Auto-generated method stub
        return null;
    }

}
