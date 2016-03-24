package de.ars.daojones.internal.drivers.notes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import lotus.domino.NotesException;
import lotus.domino.Session;
import de.ars.daojones.drivers.notes.NotesDriverConfiguration;
import de.ars.daojones.drivers.notes.NotesDriverConfiguration.SessionScope;
import de.ars.daojones.drivers.notes.NotesDatabasePath;
import de.ars.daojones.drivers.notes.security.AuthorityCredential;
import de.ars.daojones.drivers.notes.security.SessionFactoryCredential;
import de.ars.daojones.internal.drivers.notes.security.NotesAuthorityCredentialRequest;
import de.ars.daojones.internal.drivers.notes.security.NotesSessionCredentialRequest;
import de.ars.daojones.internal.drivers.notes.utilities.DriverSystem;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.database.CredentialVault;
import de.ars.daojones.runtime.spi.database.CredentialVault.Scope;
import de.ars.daojones.runtime.spi.database.CredentialVaultException;

/**
 * A manager that holds session objects by their connection information. This
 * allows to share sessions between multiple connections that have the same
 * connection information.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class NotesSessionManager {

  private static final Messages bundle = Messages.create( "NotesSessionManager" );
  private static final Level SESSION_EVENT_LEVEL = DriverSystem.isDebugging() ? Level.INFO : Level.FINER;

  private static NotesSessionManager theInstance;

  private final CallbackReference<Map<ConnectionModel.Id, Session>> sessions;
  // TODO cache ior of host
  private final Set<SessionListener> listeners = new HashSet<SessionListener>();

  // TODO create request object with given IOR or host and callback
  // TODO create response object with resolved host and callback
  // two sessions with same ior are on same host
  // two sessions with same hosts are on same host
  // a session with a host is the same as a session with an IOR and the same host

  private NotesSessionManager() {
    final Callable<Map<ConnectionModel.Id, Session>> creator = new Callable<Map<ConnectionModel.Id, Session>>() {

      @Override
      public Map<ConnectionModel.Id, Session> call() throws Exception {
        return new HashMap<ConnectionModel.Id, Session>();
      }
    };
    switch ( NotesDriverConfiguration.SESSION_SCOPE ) {
    case THREAD:
      sessions = new ThreadReference<Map<ConnectionModel.Id, Session>>( creator );
      break;
    case APPLICATION:
      sessions = new SimpleReference<Map<ConnectionModel.Id, Session>>( creator );
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

  public static synchronized NotesSessionManager getInstance() {
    if ( null == NotesSessionManager.theInstance ) {
      NotesSessionManager.theInstance = new NotesSessionManager();
    }
    return NotesSessionManager.theInstance;
  }

  public Session get( final NotesConnectionModel model ) throws NotesException, DataAccessException {
    final ConnectionModel.Id key = model.getPlainModel().getId();
    try {
      final Map<ConnectionModel.Id, Session> map = sessions.get();
      synchronized ( map ) {
        Session result = map.get( key );
        if ( null == result || !result.isValid() ) {
          result = refreshSession( result, model );
          map.put( key, result );
        }
        return result;
      }
    } catch ( final NotesException e ) {
      throw e;
    } catch ( final DataAccessException e ) {
      throw e;
    } catch ( final Exception e ) {
      throw new DataAccessException( e );
    }
  }

  /**
   * Adds a session listener that is notified when creating, refreshing or
   * destroying the session.
   * 
   * @param l
   *          the session listener
   */
  public void addSessionListener( final SessionListener l ) {
    listeners.add( l );
  }

  /**
   * Removes a session listener.
   * 
   * @param l
   *          the session listener
   */
  public void removeSessionListener( final SessionListener l ) {
    listeners.remove( l );
  }

  /**
   * Returns an array containing all registered session listeners.
   * 
   * @return an array containing all registered session listeners
   */
  public SessionListener[] getSessionListeners() {
    return listeners.toArray( new SessionListener[] {} );
  }

  private Session refreshSession( final Session oldSession, final NotesConnectionModel model ) throws NotesException,
          DataAccessException {
    final CredentialVault vault = model.getCredentialVault();
    final NotesDatabasePath path = model.getPath();
    // request notes authority credential (scope: CONNECTION)
    final AuthorityCredential authorityCredential;
    try {
      authorityCredential = vault.requestCredential( AuthorityCredential.class, Scope.CONNECTION,
              new NotesAuthorityCredentialRequest( model.getPath().getAuthority() ) );
    } catch ( final CredentialVaultException e ) {
      throw new DataAccessException( e );
    }
    // request notes session credential (username-password or token, scope: CREDENTIAL)
    final SessionFactoryCredential sessionCredential;
    try {
      sessionCredential = vault.requestCredential( SessionFactoryCredential.class, Scope.CREDENTIAL,
              new NotesSessionCredentialRequest( path ) );
      if ( null == sessionCredential ) {
        // cancelled by user
        return null;
      }
    } catch ( final CredentialVaultException e ) {
      throw new DataAccessException( e );
    }
    try {
      final String authority = authorityCredential.getAuthority();
      final boolean local = ( null == authority );
      final String hostName = NotesSessionManager.bundle.get( "host.name." + ( local ? "local" : "remote" ), authority );

      NotesSessionManager.bundle.log( NotesSessionManager.SESSION_EVENT_LEVEL, "create.start", hostName, Thread
              .currentThread().getName(), path.getVersion() );
      // create session
      final Session session = sessionCredential.login( authorityCredential );
      session.setConvertMIME( false );
      // log success
      NotesSessionManager.bundle.log( NotesSessionManager.SESSION_EVENT_LEVEL, "create.finished", hostName,
              sessionCredential );
      // call session listeners
      for ( final SessionListener listener : listeners ) {
        if ( null == oldSession ) {
          listener.sessionCreated( session );
        } else {
          listener.sessionRefreshed( oldSession, session );
        }
      }
      // Thread Manager
      final Session result = session;
      if ( NotesDriverConfiguration.SESSION_SCOPE == SessionScope.THREAD ) {
        ThreadManagerImpl.getInstance().register( new Reference<Session>() {

          @Override
          public Session get() throws Exception {
            return result;
          }
        } );
      }
      return result;
    } catch ( final Exception e ) {
      NotesSessionManager.bundle.log( Level.SEVERE, "create.error", path, sessionCredential, e.getMessage() );
      if ( e instanceof NotesException ) {
        throw ( NotesException ) e;
      } else if ( e instanceof DataAccessException ) {
        throw ( DataAccessException ) e;
      } else {
        throw new DataAccessException( e );
      }
    }
  }

  /**
   * Destroys all current sessions.
   * 
   * @param inScope
   *          if <tt>true</tt>, only destroys sessions within the current scope,
   *          otherwise all sessions
   * @throws DataAccessException
   */
  public void destroyAll( final boolean inScope ) throws DataAccessException {
    Collection<Session> sessions = null;
    if ( inScope ) {
      try {
        sessions = this.sessions.get().values();
      } catch ( final Exception e ) {
        // do nothing
        sessions = new HashSet<Session>();
      }
    } else {
      sessions = new HashSet<Session>();
      for ( final Map<ConnectionModel.Id, Session> map : this.sessions.getAll() ) {
        sessions.addAll( map.values() );
      }
    }
    for ( final Session session : sessions ) {
      destroy( session );
    }
  }

  /**
   * Destroys a session.
   * 
   * @param the
   *          session
   * @throws NotesException
   *           if an error occured when recycling the session
   * @throws DataAccessException
   */
  public void destroy( final Session session ) throws DataAccessException {
    try {
      NotesSessionManager.bundle.log( NotesSessionManager.SESSION_EVENT_LEVEL, "destroy.start", Thread.currentThread()
              .getName() );
      try {
        if ( null != session && session.isValid() ) {
          session.recycle();
        }
      } finally {
        for ( final SessionListener listener : listeners ) {
          listener.sessionDestroyed( session );
        }
      }
      NotesSessionManager.bundle.log( NotesSessionManager.SESSION_EVENT_LEVEL, "destroy.finished", Thread
              .currentThread().getName() );
    } catch ( final NotesException e ) {
      throw new DataAccessException( e );
    }
  }

}
