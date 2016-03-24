package de.ars.daojones.internal.runtime.configuration.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.connections.events.ConnectionEvent;
import de.ars.daojones.runtime.connections.events.ConnectionEventListener;

/**
 * A helper class providing support for firing connection events.
 * 
 * @author ueberfuhr, ARS Computer und Consulting GmbH
 * @since 1.2.0
 */
public class EventManager {

  private static final Messages logger = Messages.create( "configuration.context.EventManager" );

  private final Map<String, Collection<ConnectionEventListener>> listeners = new HashMap<String, Collection<ConnectionEventListener>>();

  /**
   * Adds a connection event listener.
   * 
   * @param l
   *          the connection event listener
   * @param applicationContextId
   *          the application context id that this listener is assigned to
   */
  public void addConnectionModelListener( final ConnectionEventListener l, final String applicationContextId ) {
    Collection<ConnectionEventListener> lset = listeners.get( applicationContextId );
    if ( null == lset ) {
      lset = new HashSet<ConnectionEventListener>();
      listeners.put( applicationContextId, lset );
    }
    lset.add( l );
  }

  /**
   * Removes a connection event listener.
   * 
   * @param l
   *          the connection event listener
   * @param applicationContextId
   *          the application context id that this listener is assigned to
   */
  public void removeConnectionModelListener( final ConnectionEventListener l, final String applicationContextId ) {
    final Collection<ConnectionEventListener> lset = listeners.get( applicationContextId );
    if ( null != lset ) {
      lset.remove( l );
    }
  }

  private static void handle( final ConnectionEventListener l, final ConnectionEvent event ) {
    try {
      l.handle( event );
    } catch ( final Throwable t ) {
      EventManager.logger.log( Level.SEVERE, t, "error.eventHandler" );
    }
  }

  /**
   * Fires a connection event.
   * 
   * @param event
   *          the connection event
   */
  public void fireEvent( final ConnectionEvent event ) {
    // fire common events
    final Collection<ConnectionEventListener> commonListeners = listeners.get( null );
    if ( null != commonListeners ) {
      for ( final ConnectionEventListener l : commonListeners ) {
        EventManager.handle( l, event );
      }
    }
    // fire application specific events
    for ( final Map.Entry<String, Collection<ConnectionEventListener>> entry : listeners.entrySet() ) {
      if ( null != entry.getKey() && !entry.getValue().isEmpty() ) {
        // search for connection models
        final List<ConnectionModel> models = new LinkedList<ConnectionModel>();
        for ( final ConnectionModel model : event.getConnections() ) {
          if ( entry.getKey().equals( model.getId().getApplicationId() ) ) {
            models.add( model );
          }
        }
        // fire event
        if ( !models.isEmpty() ) {
          final ConnectionEvent appEvent = new ConnectionEvent( event.getType(), event.getSource(),
                  models.toArray( new ConnectionModel[models.size()] ) );
          for ( final ConnectionEventListener l : entry.getValue() ) {
            EventManager.handle( l, appEvent );
          }
        }
      }
    }
  }

  /**
   * Removes all listeners.
   */
  public void clear() {
    listeners.clear();
  }

  /**
   * Removes all listeners to a single application context.
   * 
   * @param applicationContextId
   *          the application context id
   * @throws NullPointerException
   *           if applicationContextId is <tt>null</tt>
   */
  public void clear( final String applicationContextId ) {
    // avoid removing common listeners
    if ( null == applicationContextId ) {
      throw new NullPointerException();
    }
    listeners.remove( applicationContextId );
  }

}
