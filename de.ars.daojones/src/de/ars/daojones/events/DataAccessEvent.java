package de.ars.daojones.events;

import java.util.Date;

import de.ars.daojones.connections.Connection;
import de.ars.daojones.runtime.Dao;

/**
 * The object providing information about an event that is noticed by a
 * {@link DataAccessListener}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 * @param <T>
 *            the type of the DaoJones bean
 */
public class DataAccessEvent<T extends Dao> {

	private final DataAccessType accessType;
	private final Connection<T> connection;
	private final Object accessData;

	/**
	 * Creates an instance.
	 * 
	 * @param accessType
	 *            the {@link DataAccessType}
	 * @param connection
	 *            the {@link Connection}
	 * @param accessData
	 *            an unspecified data object that is not used per default
	 */
	public DataAccessEvent(final DataAccessType accessType,
			final Connection<T> connection, final Object accessData) {
		super();
		this.accessType = accessType;
		this.connection = connection;
		this.accessData = accessData;
	}

	/**
	 * Creates an instance.
	 * 
	 * @param accessType
	 *            the {@link DataAccessType}
	 * @param connection
	 *            the {@link Connection}
	 */
	public DataAccessEvent(final DataAccessType accessType,
			final Connection<T> connection) {
		this(accessType, connection, accessType.toString() + "@"
				+ new Date().getTime());
	}

	/**
	 * Returns some data for this event. Per default, this object is not used.
	 * 
	 * @return the event data
	 */
	public Object getAccessData() {
		return accessData;
	}

	/**
	 * Returns the {@link DataAccessType}.
	 * 
	 * @return the {@link DataAccessType}
	 */
	public DataAccessType getAccessType() {
		return accessType;
	}

	/**
	 * Returns the {@link Connection}.
	 * 
	 * @return the {@link Connection}
	 */
	public Connection<T> getConnection() {
		return connection;
	}

}
