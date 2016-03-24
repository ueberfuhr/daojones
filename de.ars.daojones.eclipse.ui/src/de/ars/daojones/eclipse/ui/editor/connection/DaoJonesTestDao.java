package de.ars.daojones.eclipse.ui.editor.connection;

import de.ars.daojones.FieldAccessor;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.DataObject;
import de.ars.daojones.runtime.DataObjectContainer;

/**
 * Just a test dao for testing connections.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
final class DaoJonesTestDao extends TestDao {

	private static final long serialVersionUID = -1958171482217368048L;
	private final DataObjectContainer delegate = new DataObjectContainer();
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}
	public String getApplicationContextId() {
		return delegate.getApplicationContextId();
	}
	public DataObject getDataObject() {
		return delegate.getDataObject();
	}
	public FieldAccessor getFieldAccessor() {
		return delegate.getFieldAccessor();
	}
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return delegate.hashCode();
	}
	public boolean isNew() {
		return delegate.isNew();
	}
	public void onCreate() throws DataAccessException {
		delegate.onCreate();
	}
	public void onDelete() throws DataAccessException {
		delegate.onDelete();
	}
	public void onUpdate() throws DataAccessException {
		delegate.onUpdate();
	}
	public void refresh() throws DataAccessException {
		delegate.refresh();
	}
	public void setApplicationContextId(String applicationContextId) {
		delegate.setApplicationContextId(applicationContextId);
	}
	public void setDataObject(DataObject dataObject) throws DataAccessException {
		delegate.setDataObject(dataObject);
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return delegate.toString();
	}
	public void update() throws DataAccessException {
		delegate.update();
	}
	
	
	
}
