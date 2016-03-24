package de.ars.daojones.connections;

import java.io.Serializable;

import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;

/**
 * An object containing methods to find connections to the training-specific classes.
 * Do not implement this by storing the connections as fields. 
 * @see Serializable
 * @param <T>
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface ConnectionReference<T extends Dao> extends Serializable {

    /**
     * Returns a connection.
     * @return the connection
     * @throws DataAccessException
     */
    public Connection<T> getConnection() throws DataAccessException;

}
