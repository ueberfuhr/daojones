package de.ars.daojones.internal.drivers.notes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import de.ars.daojones.drivers.notes.NotesDriverConfiguration;

class NotesConnectorManager {

  private final NotesSessionManager sessionManager = NotesSessionManager.getInstance();
  private final Reference<Map<NotesConnectionModel, NotesConnector>> connectors;

  private static NotesConnectorManager theInstance;

  private NotesConnectorManager() {
    final Callable<Map<NotesConnectionModel, NotesConnector>> creator = new Callable<Map<NotesConnectionModel, NotesConnector>>() {

      @Override
      public Map<NotesConnectionModel, NotesConnector> call() throws Exception {
        return new HashMap<NotesConnectionModel, NotesConnector>();
      }
    };
    switch ( NotesDriverConfiguration.SESSION_SCOPE ) {
    case THREAD:
      // if one session per thread, then one db connection per thread too
      connectors = new ThreadReference<Map<NotesConnectionModel, NotesConnector>>( creator );
      break;
    case APPLICATION:
      // if one session for the whole application, then one db connection for the whole application too
      connectors = new SimpleReference<Map<NotesConnectionModel, NotesConnector>>( creator );
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

  public static synchronized NotesConnectorManager getInstance() {
    if ( null == NotesConnectorManager.theInstance ) {
      NotesConnectorManager.theInstance = new NotesConnectorManager();
    }
    return NotesConnectorManager.theInstance;
  }

  public NotesConnector get( final NotesConnectionModel model ) {
    final NotesConnectionModel key = model;
    try {
      final Map<NotesConnectionModel, NotesConnector> map = connectors.get();
      synchronized ( map ) {
        NotesConnector result = map.get( key );
        if ( null == result ) {
          result = new NotesConnector( sessionManager, key );
          map.put( key, result );
        }
        return result;
      }
    } catch ( final Exception e ) {
      // should not occur
      throw new RuntimeException( e );
    }
  }
}
