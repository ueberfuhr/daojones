package de.ars.daojones.connections;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.ars.daojones.cache.CacheException;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.Identificator;

class CachedConnection<T extends Dao> extends Connection<T> implements
		ConnectionWrapper<T> {

	private static final Logger logger = Logger.getLogger(CachedConnection.class.getName());
	
	private final CacheConfiguration cacheConfiguration;
	private final Connection<T> connection;
	private final long cacheInterval;
	private final Class<T> theGenericClass;

	@Override
	public Connection<T> getConnection() {
		return connection;
	}

	/**
	 * @param ctx
	 * @param connection
	 * @param theGenericClass 
	 * @param cacheInterval
	 * @param cacheConfiguration
	 */
	public CachedConnection(final ApplicationContext ctx,
			final Connection<T> connection, final Class<T> theGenericClass,
			final long cacheInterval,
			final CacheConfiguration cacheConfiguration) {
		super(ctx);
		this.connection = connection;
		this.cacheInterval = cacheInterval;
		this.cacheConfiguration = cacheConfiguration;
		this.theGenericClass = theGenericClass;
	}

	@Override
	public ConnectionData getConnectionData() {
		return this.connection.getConnectionData();
	}

	@Override
	public ConnectionMetaData<T> getMetaData() {
		return connection.getMetaData();
	}

	/*
	 * ******************************************************* C O N N E C T I O
	 * N I M P L E M E N T A T I O N *
	 * ******************************************************
	 */

	private Accessor<T> accessor;

	@Override
	public Accessor<T> getAccessor() {
		if (null == accessor) {
			try {
				accessor = new CachedAccessor<T>(connection.getAccessor(),
						this.theGenericClass, this.cacheInterval,
						this.cacheConfiguration);
			} catch (CacheException e) {
				if(logger.isLoggable(Level.SEVERE)) logger.log(Level.SEVERE, "Error during creation of cached accessor.", e);
				accessor = connection.getAccessor();
			}
		}
		return accessor;
	}

	@Override
	public Identificator getIdentificator(String id) throws DataAccessException {
		return this.connection.getIdentificator(id);
	}

}
