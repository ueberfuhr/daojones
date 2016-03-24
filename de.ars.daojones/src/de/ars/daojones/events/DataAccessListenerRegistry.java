package de.ars.daojones.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.ars.daojones.connections.Connection;
import de.ars.daojones.runtime.Dao;

class DataAccessListenerRegistry {

	private static DataAccessListenerRegistry theInstance;
	
	private DataAccessListenerRegistry() {}
	
	public static DataAccessListenerRegistry getInstance() {
		if(null == theInstance) {
			theInstance = new DataAccessListenerRegistry();
		}
		return theInstance;
	}
	
	private Map<Connection<Dao>, Set<DataAccessListener<Dao>>> listeners = new HashMap<Connection<Dao>, Set<DataAccessListener<Dao>>>();
	
	@SuppressWarnings("unchecked")
	public <T extends Dao>boolean addDataAccessListener(Connection<T> connection, DataAccessListener<T> l) {
		Set<DataAccessListener<Dao>> listenerSet = listeners.get(connection);
		if(null == listenerSet) {
			listenerSet = new HashSet<DataAccessListener<Dao>>();
			listeners.put((Connection<Dao>) connection, listenerSet);
		}
		return listenerSet.add((DataAccessListener<Dao>) l);
	}

	@SuppressWarnings("unchecked")
	public <T extends Dao>boolean removeDataAccessListener(Connection<T> connection, DataAccessListener<T> l) {
		final Set<DataAccessListener<Dao>> listenerSet = listeners.get(connection);
		if(null == listenerSet) return false;
		return listenerSet.remove((DataAccessListener<Dao>) l);
	}

	public <T extends Dao>boolean removeAllDataAccessListeners(Connection<T> connection) {
		return null != listeners.remove(connection);
	}

	private interface DataAccessListenerOperation {
		public void run(DataAccessListener<Dao> l);
	}
	
	private <T extends Dao>void fireEvent(DataAccessEvent<T> event, DataAccessListenerOperation runnable) {
		final Set<DataAccessListener<Dao>> listenerSet = listeners.get(event.getConnection());
		if(null == listenerSet) return;
		for(DataAccessListener<Dao> l : listenerSet) {
			runnable.run(l);
		}
	}

	public <T extends Dao>void fireAccessBegins(final DataAccessEvent<T> event) {
		fireEvent(event, new DataAccessListenerOperation() {
			@SuppressWarnings("unchecked")
			public void run(DataAccessListener<Dao> l) {
				l.accessBegins((DataAccessEvent<Dao>) event);
			}
		});
	}

	public <T extends Dao>void fireAccessFinishedSuccessfully(final DataAccessEvent<T> event) {
		fireEvent(event, new DataAccessListenerOperation() {
			@SuppressWarnings("unchecked")
			public void run(DataAccessListener<Dao> l) {
				l.accessFinishedSuccessfully((DataAccessEvent<Dao>) event);
			}
		});
	}

	public <T extends Dao>void fireAccessFinishedWithError(final DataAccessEvent<T> event, final Throwable t) {
		fireEvent(event, new DataAccessListenerOperation() {
			@SuppressWarnings("unchecked")
			public void run(DataAccessListener<Dao> l) {
				l.accessFinishedWithError((DataAccessEvent<Dao>) event, t);
			}
		});
	}

}
