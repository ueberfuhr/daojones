package de.ars.daojones.connections.model;

import java.util.Collection;

import de.ars.daojones.connections.ConnectionFactory;

/**
 * This interfaces provides information about one
 * connection. The connection's attributes can be edited.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IChangeableConnection extends IConnection {

	/**
	 * Sets the name of the connection.
	 * @param name the name
	 */
	public void setName(String name);
	/**
	 * Sets a description.
	 * @param value the description
	 */
	public void setDescription(String value);
	/**
	 * Sets the host where to find the database.
	 * May be null for local database access.
	 * @param value the host
	 */
	public void setHost(String value);
	/**
	 * Sets the path to the database.
	 * @param value the path to the database
	 */
	public void setDatabase(String value);
	/**
	 * Sets the credential.
	 * @param credential the credential
	 */
	public void setCredential(ICredential credential);
	/**
	 * Returns the class name of the connection factory that can create a connection instance.
	 * The class must implement the interface {@link ConnectionFactory}
	 * and provide the default constructor.
	 * @param value the class name of the connection factory
	 */
	public void setFactory(String value);
	/**
	 * Returns true, if this connection should be used whenever a connection is not 
	 * explicitely responsible for a special object class.
	 * @param value true, if the connection is the default one
	 */
	public void setDefault(boolean value);
	/**
	 * Returns true, if the objects that this connection returns should be cached.
	 * @param value true, if the objects that this connection returns should be cached
	 */
	public void setCached(boolean value);
	/**
	 * Returns the count of milliseconds that an object in the cache is valid.
	 * @param value the count of milliseconds that an object in the cache is valid
	 */
	public void setCacheExpiration(long value);
	/**
	 * Returns the maximum count of results that this connection returns.
	 * You can use this for testing purposes.
	 * @param value the maximum count of results that this connection returns. You can use this for testing purposes.
	 */
	public void setMaxResults(int value);
	/**
	 * Returns a connection pool to limit the count of connection instances.
	 * May be null.
	 * @param value the connection pool
	 */
	public void setConnectionPool(IInterval value);
	/**
	 * Sets the for classes.
	 * @param value the new for classes
	 */
	public void setForClasses(Collection<String> value);
	
}
