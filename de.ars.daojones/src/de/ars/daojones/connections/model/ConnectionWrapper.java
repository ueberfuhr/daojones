package de.ars.daojones.connections.model;

import java.util.Collection;

import de.ars.daojones.connections.ConnectionBuildException;
import de.ars.daojones.connections.ConnectionFactory;

/**
 * A class wrapping an {@link IConnection} and delegating to it.
 * You can use this and overwrite single methods to change the
 * behaviour.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ConnectionWrapper implements IConnection {

	private static final long serialVersionUID = 4231519658961498068L;
	private final IConnection delegate;

	/**
	 * Creates an instance.
	 * @param delegate the delegate
	 */
	public ConnectionWrapper(IConnection delegate) {
		super();
		this.delegate = delegate;
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnection#getCacheExpiration()
	 */
	// TODO Java6-Migration
//	@Override
	public long getCacheExpiration() {
		return delegate.getCacheExpiration();
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnection#getConnectionPool()
	 */
	// TODO Java6-Migration
//	@Override
	public IInterval getConnectionPool() {
		return delegate.getConnectionPool();
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnection#getCredential()
	 */
	// TODO Java6-Migration
//	@Override
	public ICredential getCredential() {
		return delegate.getCredential();
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnection#getDatabase()
	 */
	// TODO Java6-Migration
//	@Override
	public String getDatabase() {
		return delegate.getDatabase();
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnection#getDescription()
	 */
	// TODO Java6-Migration
//	@Override
	public String getDescription() {
		return delegate.getDescription();
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnection#getFactory()
	 */
	// TODO Java6-Migration
//	@Override
	public String getFactory() {
		return delegate.getFactory();
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnection#getForClasses()
	 */
	// TODO Java6-Migration
//	@Override
	public Collection<String> getForClasses() {
		return delegate.getForClasses();
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnection#getHost()
	 */
	// TODO Java6-Migration
//	@Override
	public String getHost() {
		return delegate.getHost();
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnection#getMaxResults()
	 */
	// TODO Java6-Migration
//	@Override
	public int getMaxResults() {
		return delegate.getMaxResults();
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnection#getName()
	 */
	// TODO Java6-Migration
//	@Override
	public String getName() {
		return delegate.getName();
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnection#isCached()
	 */
	// TODO Java6-Migration
//	@Override
	public boolean isCached() {
		return delegate.isCached();
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnection#isDefault()
	 */
	// TODO Java6-Migration
//	@Override
	public boolean isDefault() {
		return delegate.isDefault();
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnection#createConnectionFactory()
	 */
	// TODO Java6-Migration
//	@Override
	public ConnectionFactory createConnectionFactory()
			throws ConnectionBuildException {
		return delegate.createConnectionFactory();
	}
	
}
