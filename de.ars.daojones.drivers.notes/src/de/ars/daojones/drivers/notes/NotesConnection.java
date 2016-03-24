package de.ars.daojones.drivers.notes;

import lotus.domino.NotesException;
import lotus.domino.Session;
import de.ars.daojones.annotations.DataSourceType;
import de.ars.daojones.annotations.model.DataSourceInfo;
import de.ars.daojones.connections.Accessor;
import de.ars.daojones.connections.AccessorHelper;
import de.ars.daojones.connections.ApplicationContext;
import de.ars.daojones.connections.Connection;
import de.ars.daojones.connections.ConnectionData;
import de.ars.daojones.connections.ConnectionMetaData;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.Identificator;
import de.ars.daojones.runtime.query.TemplateManager;

/**
 * A class managing a connection to a Notes database. To create an instance, use
 * <a href="ConnectionManagerFactory.html">ConnectionManagerFactory</a>.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class NotesConnection<T extends Dao> extends Connection<T> {

//	private final NotesConnector connector;
	private final Class<T> theGenericClass;
	private final ConnectionMetaData<T> metaData;
	private final ConnectionData connectionData;

	/**
	 * Creates an instance ready for committing to a Notes database.
	 * 
	 * @param ctx
	 *            the {@link ApplicationContext}
	 * @param connectionData
	 *            the {@link ConnectionData}
	 * @param theGenericClass
	 *            the class that this connection is responsible for
	 */
	public NotesConnection(ApplicationContext ctx,
			final ConnectionData connectionData, final Class<T> theGenericClass) {
		super(ctx);
		this.connectionData = connectionData;
//		this.connector = NotesConnectorManager.getInstance()
//				.get(connectionData);
		this.theGenericClass = theGenericClass;
		this.metaData = new ConnectionMetaData<T>() {
			// TODO Java6-Migration
			// @Override
			public DataSourceInfo getDataSource(T t) {
				return AccessorHelper.getDataSource(t.getClass());
			}

			// TODO Java6-Migration
			// @Override
			public boolean isCreateAllowed() {
				return NotesConnection.this.getDataSource().getType() == DataSourceType.TABLE;
			}

			// TODO Java6-Migration
			// @Override
			public boolean isDeleteAllowed(T t) {
				return true;
			}

			// TODO Java6-Migration
			// @Override
			public boolean isUpdateAllowed(T t) {
				return true;
			}
		};
	}

	protected NotesConnector getConnector() {
//		return connector;
	    return NotesConnectorManager.getInstance().get(connectionData);
	}

	Session getSession() throws NotesException {
		return getConnector().getSession();
	}

	@Override
	public ConnectionData getConnectionData() {
		return this.getConnector().getConnectionData();
	}
	
	/*
	 * I N T E R F A C E M E T H O D S
	 */

	@Override
	public int hashCode() {
		return getConnector().hashCode();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NotesConnection))
			return false;
		return getConnector().equals(((NotesConnection) obj).getConnector());
	}

	@Override
	public Accessor<T> getAccessor() {
		return new NotesAccessor<T>(theGenericClass, this);
	}

	protected DataSourceInfo getDataSource() {
		return AccessorHelper.getDataSource(theGenericClass);
	}

	@Override
	public Identificator getIdentificator(String id) throws DataAccessException {
		return new NotesDocumentIdentificator(id);
	}

	protected TemplateManager getTemplateManager() {
		return NotesTemplateManager.getInstance();
	}

	@Override
	public ConnectionMetaData<T> getMetaData() {
		return metaData;
	}

}
