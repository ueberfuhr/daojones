package de.ars.daojones.eclipse.ui.editor.connection;

import java.util.Collection;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

import de.ars.daojones.connections.ConnectionBuildException;
import de.ars.daojones.connections.ConnectionFactory;
import de.ars.daojones.connections.model.IChangeableConnection;
import de.ars.daojones.connections.model.ICredential;
import de.ars.daojones.connections.model.IInterval;
import de.ars.daojones.connections.model.IUserPasswordCredential;
import de.ars.daojones.connections.model.Interval;
import de.ars.daojones.connections.model.UserPasswordCredential;
import de.ars.equinox.utilities.ui.formbean.FormBean;

/**
 * The form bean for the {@link IChangeableConnection}.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@SuppressWarnings("unchecked")
public final class ConnectionFormBean extends FormBean<IChangeableConnection> implements IChangeableConnection {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The name property.
	 */
	public static final ModifiableProperty<IChangeableConnection, String> PROPERTY_NAME 
		= (ModifiableProperty)createPropertyFromJavaBean(IChangeableConnection.class, "name", String.class);
	/**
	 * @see de.ars.daojones.connections.model.IConnection#getName()
	 */
	// TODO Java6-Migration
	// @Override
	public String getName() {
		return getValue(PROPERTY_NAME);
	}
	/**
	 * @see de.ars.daojones.connections.model.IChangeableConnection#setName(java.lang.String)
	 */
	// TODO Java6-Migration
	// @Override
	public void setName(String value) {
		setValue(PROPERTY_NAME, value);
	}
	
	/**
	 * The description property.
	 */
	public static final ModifiableProperty<IChangeableConnection, String> PROPERTY_DESCRIPTION 
		= (ModifiableProperty)createPropertyFromJavaBean(IChangeableConnection.class, "description", String.class);
	/**
	 * @see de.ars.daojones.connections.model.IConnection#getDescription()
	 */
	// TODO Java6-Migration
	// @Override
	public String getDescription() {
		return getValue(PROPERTY_DESCRIPTION);
	}
	/**
	 * @see de.ars.daojones.connections.model.IChangeableConnection#setDescription(java.lang.String)
	 */
	// TODO Java6-Migration
	// @Override
	public void setDescription(String value) {
		setValue(PROPERTY_DESCRIPTION, value);
	}

	/**
	 * The host property.
	 */
	public static final ModifiableProperty<IChangeableConnection, String> PROPERTY_HOST 
		= (ModifiableProperty)createPropertyFromJavaBean(IChangeableConnection.class, "host", String.class);
	/**
	 * @see de.ars.daojones.connections.model.IConnection#getHost()
	 */
	// TODO Java6-Migration
	// @Override
	public String getHost() {
		return getValue(PROPERTY_HOST);
	}
	/**
	 * @see de.ars.daojones.connections.model.IChangeableConnection#setHost(java.lang.String)
	 */
	// TODO Java6-Migration
	// @Override
	public void setHost(String value) {
		setValue(PROPERTY_HOST, value);
	}

	/**
	 * The database property.
	 */
	public static final ModifiableProperty<IChangeableConnection, String> PROPERTY_DATABASE 
		= (ModifiableProperty)createPropertyFromJavaBean(IChangeableConnection.class, "database", String.class);
	/**
	 * @see de.ars.daojones.connections.model.IConnection#getDatabase()
	 */
	// TODO Java6-Migration
	// @Override
	public String getDatabase() {
		return getValue(PROPERTY_DATABASE);
	}
	/**
	 * @see de.ars.daojones.connections.model.IChangeableConnection#setDatabase(java.lang.String)
	 */
	// TODO Java6-Migration
	// @Override
	public void setDatabase(String value) {
		setValue(PROPERTY_DATABASE, value);
	}

	/**
	 * The credential property.
	 */
	public static final ModifiableProperty<IChangeableConnection, ICredential> PROPERTY_CREDENTIAL 
		= (ModifiableProperty)createPropertyFromJavaBean(IChangeableConnection.class, "credential", ICredential.class);
	/**
	 * @see de.ars.daojones.connections.model.IConnection#getCredential()
	 */
	// TODO Java6-Migration
	// @Override
	public ICredential getCredential() {
		return getValue(PROPERTY_CREDENTIAL);
	}
	/**
	 * @see de.ars.daojones.connections.model.IChangeableConnection#setCredential(ICredential)
	 */
	// TODO Java6-Migration
	// @Override
	public void setCredential(ICredential value) {
		setValue(PROPERTY_CREDENTIAL, value);
	}

	/**
	 * The factory property.
	 */
	public static final ModifiableProperty<IChangeableConnection, String> PROPERTY_FACTORY 
		= (ModifiableProperty) createPropertyFromJavaBean(IChangeableConnection.class, "factory", String.class);
	/**
	 * @see de.ars.daojones.connections.model.IConnection#getFactory()
	 */
	// TODO Java6-Migration
	// @Override
	public String getFactory() {
		return getValue(PROPERTY_FACTORY);
	}
	/**
	 * @see de.ars.daojones.connections.model.IChangeableConnection#setFactory(String)
	 */
	// TODO Java6-Migration
	// @Override
	public void setFactory(String value) {
		setValue(PROPERTY_FACTORY, value);
	}

	/**
	 * The default property.
	 */
	public static final ModifiableProperty<IChangeableConnection, Boolean> PROPERTY_DEFAULT 
		= (ModifiableProperty) createPropertyFromJavaBean(IChangeableConnection.class, "default", Boolean.class);
	/**
	 * @see de.ars.daojones.connections.model.IConnection#isDefault()
	 */
	// TODO Java6-Migration
	// @Override
	public boolean isDefault() {
		return getValue(PROPERTY_DEFAULT);
	}
	/**
	 * @see de.ars.daojones.connections.model.IChangeableConnection#setDefault(boolean)
	 */
	// TODO Java6-Migration
	// @Override
	public void setDefault(boolean value) {
		setValue(PROPERTY_DEFAULT, value);
	}
	
	/**
	 * The cached property.
	 */
	public static final ModifiableProperty<IChangeableConnection, Boolean> PROPERTY_CACHED 
		= (ModifiableProperty) createPropertyFromJavaBean(IChangeableConnection.class, "cached", Boolean.class);
	/**
	 * @see de.ars.daojones.connections.model.IConnection#isCached()
	 */
	// TODO Java6-Migration
	// @Override
	public boolean isCached() {
		return getValue(PROPERTY_CACHED);
	}
	/**
	 * @see de.ars.daojones.connections.model.IChangeableConnection#setCached(boolean)
	 */
	// TODO Java6-Migration
	// @Override
	public void setCached(boolean value) {
		setValue(PROPERTY_CACHED, value);
	}

	/**
	 * The cacheExpiration property.
	 */
	public static final ModifiableProperty<IChangeableConnection, Long> PROPERTY_CACHEEXPIRATION 
		= (ModifiableProperty) createPropertyFromJavaBean(IChangeableConnection.class, "cacheExpiration", Long.class);
	/**
	 * @see de.ars.daojones.connections.model.IConnection#getCacheExpiration()
	 */
	// TODO Java6-Migration
	// @Override
	public long getCacheExpiration() {
		return getValue(PROPERTY_CACHEEXPIRATION);
	}
	/**
	 * @see de.ars.daojones.connections.model.IChangeableConnection#setCacheExpiration(long)
	 */
	// TODO Java6-Migration
	// @Override
	public void setCacheExpiration(long value) {
		setValue(PROPERTY_CACHEEXPIRATION, value);
	}
	
	/**
	 * The connectionPool property.
	 */
	public static final ModifiableProperty<IChangeableConnection, IInterval> PROPERTY_CONNECTION_POOL 
		= (ModifiableProperty) createPropertyFromJavaBean(IChangeableConnection.class, "connectionPool", IInterval.class);
	/**
	 * @see de.ars.daojones.connections.model.IConnection#getConnectionPool()
	 */
	// TODO Java6-Migration
	// @Override
	public IInterval getConnectionPool() {
		return getValue(PROPERTY_CONNECTION_POOL);
	}
	/**
	 * @see de.ars.daojones.connections.model.IChangeableConnection#setConnectionPool(IInterval)
	 */
	// TODO Java6-Migration
	// @Override
	public void setConnectionPool(IInterval value) {
		setValue(PROPERTY_CONNECTION_POOL, value);
	}
	
	/**
	 * The maxResults property.
	 */
	public static final ModifiableProperty<IChangeableConnection, Integer> PROPERTY_MAXRESULTS 
		= (ModifiableProperty) createPropertyFromJavaBean(IChangeableConnection.class, "maxResults", Integer.class);
	/**
	 * @see de.ars.daojones.connections.model.IConnection#getMaxResults()
	 */
	// TODO Java6-Migration
	// @Override
	public int getMaxResults() {
		return getValue(PROPERTY_MAXRESULTS);
	}
	/**
	 * @see de.ars.daojones.connections.model.IChangeableConnection#setMaxResults(int)
	 */
	// TODO Java6-Migration
	// @Override
	public void setMaxResults(int value) {
		setValue(PROPERTY_MAXRESULTS, value);
	}
	
	/**
	 * A strategy to read the username from the credential
	 */
	public final UpdateValueStrategy USERNAME_READ_FROM_CREDENTIAL = new UpdateValueStrategy() {
		@Override
		public Object convert(Object value) {
			return null == value || !(value instanceof IUserPasswordCredential) ? null : ((IUserPasswordCredential)value).getUsername();
		}
	};
	/**
	 * A strategy to write the username to the credential
	 */
	public final UpdateValueStrategy USERNAME_WRITE_TO_CREDENTIAL = new UpdateValueStrategy() {
		@Override
		public Object convert(Object value) {
			final ICredential currentCredential = getCredential();
			final UserPasswordCredential cred = new UserPasswordCredential();
			if(currentCredential instanceof IUserPasswordCredential) {
				cred.setUsername(((IUserPasswordCredential)currentCredential).getUsername());
				cred.setPassword(((IUserPasswordCredential)currentCredential).getPassword());
			}
			cred.setUsername(null != value ? value.toString() : null);
			return cred;
		}
	};
	
	/**
	 * A strategy to read the password from the credential
	 */
	public final UpdateValueStrategy PASSWORD_READ_FROM_CREDENTIAL = new UpdateValueStrategy() {
		@Override
		public Object convert(Object value) {
			return null == value || !(value instanceof IUserPasswordCredential) ? null : ((IUserPasswordCredential)value).getPassword();
		}
	};
	/**
	 * A strategy to write the password to the credential
	 */
	public final UpdateValueStrategy PASSWORD_WRITE_TO_CREDENTIAL = new UpdateValueStrategy() {
		@Override
		public Object convert(Object value) {
			final ICredential currentCredential = getCredential();
			final UserPasswordCredential cred = new UserPasswordCredential();
			if(currentCredential instanceof IUserPasswordCredential) {
				cred.setUsername(((IUserPasswordCredential)currentCredential).getUsername());
				cred.setPassword(((IUserPasswordCredential)currentCredential).getPassword());
			}
			cred.setPassword(null != value ? value.toString() : null);
			return cred;
		}
	};

	
	/**
	 * A strategy to read the minConnections from the connection pool
	 */
	public final UpdateValueStrategy MIN_CONNECTIONS_READ_FROM_CONNECTIONPOOL = new UpdateValueStrategy() {
		@Override
		public Object convert(Object value) {
			return null == value || !(value instanceof IInterval) ? null : ((IInterval)value).getMinimum();
		}
	};
	/**
	 * A strategy to write the minConnections to the connection pool.
	 */
	public final UpdateValueStrategy MIN_CONNECTIONS_WRITE_TO_CONNECTIONPOOL = new UpdateValueStrategy() {
		@Override
		public Object convert(Object value) {
			final IInterval currentConnectionPool = getConnectionPool();
			final Interval pool = new Interval();
			pool.setMinimum(((Number)value).intValue());
			pool.setMaximum(currentConnectionPool.getMaximum());
			return pool;
		}
	};

	/**
	 * A strategy to read the maxConnections from the connection pool
	 */
	public final UpdateValueStrategy MAX_CONNECTIONS_READ_FROM_CONNECTIONPOOL = new UpdateValueStrategy() {
		@Override
		public Object convert(Object value) {
			return null == value || !(value instanceof IInterval) ? null : ((IInterval)value).getMaximum();
		}
	};
	/**
	 * A strategy to write the maxConnections to the connection pool.
	 */
	public final UpdateValueStrategy MAX_CONNECTIONS_WRITE_TO_CONNECTIONPOOL = new UpdateValueStrategy() {
		@Override
		public Object convert(Object value) {
			final IInterval currentConnectionPool = getConnectionPool();
			final Interval pool = new Interval();
			pool.setMinimum(currentConnectionPool.getMinimum());
			pool.setMaximum(((Number)value).intValue());
			return pool;
		}
	};

	/**
	 * A strategy to read the maxConnections from the connection pool
	 */
	public final UpdateValueStrategy RESULTSLIMITED_READ_FROM_MAXRESULTS = new UpdateValueStrategy() {
		@Override
		public Object convert(Object value) {
			if(null == value) return false;
			final int maxResults = getMaxResults();
			return maxResults >0 && maxResults < Integer.MAX_VALUE;
		}
	};
	/**
	 * A strategy to write the maxConnections to the connection pool.
	 */
	public final UpdateValueStrategy RESULTSLIMITED_WRITE_TO_MAXRESULTS = new UpdateValueStrategy() {
		@Override
		public Object convert(Object value) {
			return ((Boolean)value ? Math.min(Math.max(getMaxResults(),1), Integer.MAX_VALUE-1) : Integer.MAX_VALUE);
		}
	};

	/**
	 * The forClasses property.
	 */
	public static final ModifiableProperty<IChangeableConnection, Collection<String>> PROPERTY_FORCLASSES 
		= (ModifiableProperty) createPropertyFromJavaBean(IChangeableConnection.class, "forClasses", Collection.class);
	/**
	 * @see de.ars.daojones.connections.model.IConnection#getForClasses()
	 */
	// TODO Java6-Migration
	// @Override
	public Collection<String> getForClasses() {
		return getValue(PROPERTY_FORCLASSES);
	}
	/**
	 * @see de.ars.daojones.connections.model.IChangeableConnection#setForClasses(java.util.Collection)
	 */
	// TODO Java6-Migration
	// @Override
	public void setForClasses(Collection<String> value) {
		setValue(PROPERTY_FORCLASSES, value);
	}
	/**
	 * @see de.ars.equinox.utilities.ui.formbean.FormBean#getListeners(org.eclipse.swt.widgets.Widget, int)
	 */
	@Override
	protected Listener[] getListeners(Widget widget, int eventType) {
		return widget.getListeners(eventType);
	}
	/**
	 * @see de.ars.daojones.connections.model.IConnection#createConnectionFactory()
	 */
	public ConnectionFactory createConnectionFactory()
			throws ConnectionBuildException {
		try {
			return (ConnectionFactory) Class.forName(getFactory()).newInstance();
		} catch (InstantiationException e) {
			throw new ConnectionBuildException(e);
		} catch (IllegalAccessException e) {
			throw new ConnectionBuildException(e);
		} catch (ClassNotFoundException e) {
			throw new ConnectionBuildException(e);
		}
	}
	
}
