package de.ars.daojones.runtime.connections.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.ars.daojones.runtime.connections.Accessor;
import de.ars.daojones.runtime.connections.Connection;

class DataAccessListenerRegistry {

  private static DataAccessListenerRegistry theInstance;

  private DataAccessListenerRegistry() {
  }

  public static DataAccessListenerRegistry getInstance() {
    if ( null == DataAccessListenerRegistry.theInstance ) {
      DataAccessListenerRegistry.theInstance = new DataAccessListenerRegistry();
    }
    return DataAccessListenerRegistry.theInstance;
  }

  private final Map<Accessor<?>, Set<DataAccessListener<?>>> listeners = new HashMap<Accessor<?>, Set<DataAccessListener<?>>>();

  public <T> boolean addDataAccessListener( final Connection<T> connection, final DataAccessListener<T> l ) {
    Set<DataAccessListener<?>> listenerSet = listeners.get( connection );
    if ( null == listenerSet ) {
      listenerSet = new HashSet<DataAccessListener<?>>();
      listeners.put( connection, listenerSet );
    }
    return listenerSet.add( l );
  }

  public <T> boolean removeDataAccessListener( final Connection<T> connection, final DataAccessListener<T> l ) {
    final Set<DataAccessListener<?>> listenerSet = listeners.get( connection );
    if ( null == listenerSet ) {
      return false;
    }
    return listenerSet.remove( l );
  }

  public <T> boolean removeAllDataAccessListeners( final Connection<T> connection ) {
    return null != listeners.remove( connection );
  }

  private interface DataAccessListenerOperation {
    public void run( DataAccessListener<?> l );
  }

  private <T> void fireEvent( final DataAccessEvent<T> event, final DataAccessListenerOperation runnable ) {
    final Set<DataAccessListener<?>> listenerSet = listeners.get( event.getAccessor() );
    if ( null == listenerSet ) {
      return;
    }
    for ( final DataAccessListener<?> l : listenerSet ) {
      runnable.run( l );
    }
  }

  public <T> void fireAccessBegins( final DataAccessEvent<T> event ) {
    fireEvent( event, new DataAccessListenerOperation() {
      @Override
      @SuppressWarnings( { "unchecked", "rawtypes" } )
      public void run( final DataAccessListener<?> l ) {
        l.accessBegins( ( DataAccessEvent ) event );
      }
    } );
  }

  public <T> void fireAccessFinishedSuccessfully( final DataAccessEvent<T> event ) {
    fireEvent( event, new DataAccessListenerOperation() {
      @Override
      @SuppressWarnings( { "unchecked", "rawtypes" } )
      public void run( final DataAccessListener<?> l ) {
        l.accessFinishedSuccessfully( ( DataAccessEvent ) event );
      }
    } );
  }

  public <T> void fireAccessFinishedWithError( final DataAccessEvent<T> event, final Throwable t ) {
    fireEvent( event, new DataAccessListenerOperation() {
      @Override
      @SuppressWarnings( { "unchecked", "rawtypes" } )
      public void run( final DataAccessListener<?> l ) {
        l.accessFinishedWithError( ( DataAccessEvent ) event, t );
      }
    } );
  }

}
