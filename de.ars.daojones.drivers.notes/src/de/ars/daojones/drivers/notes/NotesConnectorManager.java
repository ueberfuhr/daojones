package de.ars.daojones.drivers.notes;


import java.util.HashMap;
import java.util.Map;

import de.ars.daojones.connections.ConnectionData;

class NotesConnectorManager {

	private final Map<ConnectionData, NotesConnector> connectors = new HashMap<ConnectionData, NotesConnector>();
	
	private static NotesConnectorManager theInstance;
	private NotesConnectorManager() {}
	
	public static synchronized NotesConnectorManager getInstance() {
		if(null == theInstance) theInstance = new NotesConnectorManager();
		return theInstance;
	}
	
	public synchronized NotesConnector get(ConnectionData data) {
		NotesConnector result = connectors.get(data);
		if(null == result) {
			result = new NotesConnector(data);
			connectors.put(data, result);
		}
		return result;
	}
	
}
