package de.ars.daojones.drivers.notes;

import de.ars.daojones.connections.ConnectionData;

/**
 * A default implementation for {@link QueryAssistant} containing some general
 * information about the query context.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 */
abstract class AbstractQueryAssistant implements QueryAssistant {

    private final ConnectionData connectionData;

    public AbstractQueryAssistant(ConnectionData connectionData) {
        super();
        this.connectionData = connectionData;
    }

    protected ConnectionData getConnectionData() {
        return this.connectionData;
    }

    protected NotesConnector getNotesConnector() {
        return NotesConnectorManager.getInstance().get(getConnectionData());
    }

}
