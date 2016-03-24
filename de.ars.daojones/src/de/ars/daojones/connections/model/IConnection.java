package de.ars.daojones.connections.model;

import java.io.Serializable;
import java.util.Collection;

import de.ars.daojones.connections.ConnectionBuildException;
import de.ars.daojones.connections.ConnectionFactory;

/**
 * This interfaces provides information about one
 * connection.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IConnection extends Serializable {

	/**
	 * Returns the name of the connection.
	 * @return the name
	 */
	public String getName();
	/**
	 * Returns a description.
	 * @return the description
	 */
	public String getDescription();
	/**
	 * Returns the host where to find the database.
	 * May be null for local database access.
	 * @return the host
	 */
	public String getHost();
	/**
	 * Returns the path to the database.
	 * @return the path to the database
	 */
	public String getDatabase();
	/**
	 * Returns a credential that has to be used for database access.
	 * May be null for unsecured databases.
	 * @return the credential
	 */
	public ICredential getCredential();
	/**
	 * Returns the class name of the connection factory that can create a connection instance.
	 * The class must implement the interface {@link ConnectionFactory}
	 * and provide the default constructor.
	 * @return the class name of the connection factory
	 */
	public String getFactory();
	/**
	 * Creates a {@link ConnectionFactory} instance.
	 * @return the {@link ConnectionFactory} instance
	 * @throws ConnectionBuildException 
	 */
	public ConnectionFactory createConnectionFactory() throws ConnectionBuildException;
	/**
	 * Returns a collection of class names of the objects that this connection should handle.
	 * @return the class names
	 */
	public Collection<String> getForClasses();
	/**
	 * Returns true, if this connection should be used whenever a connection is not 
	 * explicitely responsible for a special object class.
	 * @return true, if the connection is the default one
	 */
	public boolean isDefault();
	/**
	 * Returns true, if the objects that this connection returns should be cached.
	 * @return true, if the objects that this connection returns should be cached
	 */
	public boolean isCached();
	/**
	 * Returns the count of milliseconds that an object in the cache is valid.
	 * @return the count of milliseconds that an object in the cache is valid
	 */
	public long getCacheExpiration();
	/**
	 * Returns the maximum count of results that this connection returns.
	 * You can use this for testing purposes.
	 * @return the maximum count of results that this connection returns. You can use this for testing purposes.
	 */
	public int getMaxResults();
	/**
	 * Returns a connection pool to limit the count of connection instances.
	 * May be null.
	 * @return the connection pool
	 */
	public IInterval getConnectionPool();
	
}
