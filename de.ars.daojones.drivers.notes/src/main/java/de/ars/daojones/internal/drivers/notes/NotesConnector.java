package de.ars.daojones.internal.drivers.notes;

import java.io.Serializable;
import java.util.logging.Level;

import de.ars.daojones.drivers.notes.NotesDatabasePath;
import de.ars.daojones.drivers.notes.NotesDatabasePath.PathType;
import de.ars.daojones.drivers.notes.security.ServerCredential;
import de.ars.daojones.internal.drivers.notes.security.NotesServerCredentialRequest;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.database.CredentialVault;
import de.ars.daojones.runtime.spi.database.CredentialVault.Scope;
import de.ars.daojones.runtime.spi.database.CredentialVaultException;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;

public final class NotesConnector {

  /*
   * CONNECTIONS CAN BE BUILT USING
   *  - a host and a database file name
   *  - a version parameter (for later issues) "?version=1.1"
   *
   * Examples:
   * =========
   *
   * This URL opens the database ARS/kontakt.nsf on server MIRACULIX:
   *  notes://MIRACULIX/ARS/kontakt.nsf?version=1.1
   * This URL opens the database ARS/kontakt.nsf using the local Notes client:
   *  notes:///ARS/kontakt.nsf?version=1.1
   * This URL opens the replica of the database with the given replica id on ASTERIX:
   *  notes://ASTERIX/C1257421003F2111?version=1.1&type=replica
   * This URL opens the replica of the database with the given replica id on ASTERIX (using the local Notes client):
   *  notes:///C1257421003F2111?version=1.1&type=replica&server=ASTERIX
   */

  private static final Level DB_EVENT_LEVEL = Level.FINER;
  private static final Messages bundle = Messages.create( "NotesConnector" );

  private static final class DatabaseMetadata implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean softDeletionsAllowed;
    private boolean allowOpenSoftDeleted;
    private int undeletedExpireTime;

    private DatabaseMetadata( final Database db ) throws NotesException {
      super();
      try {
        softDeletionsAllowed = db.getOption( Database.DBOPT_SOFTDELETE );
      } catch ( final NotesException e ) {
        // Mybe not implemented for CORBA communication!
        softDeletionsAllowed = false;
      }
      try {
        allowOpenSoftDeleted = db.isAllowOpenSoftDeleted();
      } catch ( final NotesException e ) {
        // Mybe not implemented for CORBA communication!
        allowOpenSoftDeleted = false;
      }
      try {
        undeletedExpireTime = db.getUndeleteExpireTime();
      } catch ( final NotesException e ) {
        // Mybe not implemented for CORBA communication!
        undeletedExpireTime = 0;
      }
    }

    public boolean isAllowOpenSoftDeleted() {
      return allowOpenSoftDeleted;
    }

    public int getUndeletedExpireTime() {
      return undeletedExpireTime;
    }

    public boolean isSoftDeletionsAllowed() {
      return softDeletionsAllowed;
    }
  }

  private final NotesSessionManager sessionManager;
  private final NotesConnectionModel model;
  private Database database;
  private DatabaseMetadata databaseMetadata;

  public NotesConnector( final NotesSessionManager sessionManager, final NotesConnectionModel model ) {
    super();
    this.sessionManager = sessionManager;
    this.model = model;
  }

  public NotesConnectionModel getModel() {
    return model;
  }

  public NotesSessionManager getSessionManager() {
    return sessionManager;
  }

  private DatabaseMetadata getDatabaseMetadata() throws NotesException, DataAccessException {
    synchronized ( this ) {
      if ( null == databaseMetadata ) {
        getDatabase();
      }
      return databaseMetadata;
    }
  }

  /**
   * Validates the deletion of a document. This method validates that a document
   * can be opened for reading and writing items. A document cannot be opened if
   * it is marked as deleted (with the database having soft deletion enabled)
   * and the database does not allow to open deleted documents. <b>Use this
   * method instead of {@link Document#isDeleted()} because it avoids CORBA
   * calls.</b>
   *
   * @param doc
   *          the document
   * @return <tt>true</tt>, if the document is not deleted.
   * @throws NotesException
   * @throws DataAccessException
   */
  public boolean validateDeletion( final Document doc ) throws NotesException, DataAccessException {
    // soft deletion is not enabled -> valid
    // soft deletion is allowed -> valid
    // deleted document can be opened -> valid
    // document is not deleted -> valid
    return validateDeletion() || null != doc && !doc.isDeleted();
  }

  /**
   * Validates, if a document can be opened, no matter if it is deleted or not.
   * A document can be opened if the database has soft deletion disabled and
   * does allow to open deleted documents. <b>Use this method before the
   * invocation of {@link Document#isDeleted()} because it avoids CORBA
   * calls.</b>
   *
   * the document
   *
   * @return <tt>true</tt>, if a document can be opened, no matter if it is
   *         deleted or not.
   * @throws DataAccessException
   * @throws NotesException
   */
  public boolean validateDeletion() throws NotesException, DataAccessException {
    // store metadata to variable to avoid multi-threading issues
    final DatabaseMetadata md = getDatabaseMetadata();
    // soft deletion is not enabled -> valid
    // soft deletion is allowed -> valid
    // deleted document can be opened -> valid
    return null != md
            && ( !md.isSoftDeletionsAllowed() || md.getUndeletedExpireTime() <= 0 || md.isAllowOpenSoftDeleted() );
  }

  private Session getSessionObject() throws NotesException, DataAccessException {
    return getSessionManager().get( getModel() );
  }

  private Database createDatabase() throws NotesException, DataAccessException {
    Database db = null;
    // open database
    final Session session = getSessionObject();
    if ( null != session ) {
      final CredentialVault credentialVault = getModel().getCredentialVault();
      final NotesDatabasePath path = getModel().getPath();
      // get database file name
      final StringBuffer sbDatabase = new StringBuffer( path.getDatabase() );
      while ( sbDatabase.charAt( 0 ) == '/' ) {
        sbDatabase.deleteCharAt( 0 );
      }
      for ( int i = sbDatabase.length() - 1; i >= 0; i-- ) {
        if ( sbDatabase.charAt( i ) == '/' ) {
          sbDatabase.replace( i, i + 1, "\\" );
        }
      }
      final String database = sbDatabase.toString();
      final String host = session.getServerName();
      final PathType type = path.getType();
      final boolean local = null != host;
      NotesConnector.bundle.log( NotesConnector.DB_EVENT_LEVEL, "open.start." + ( local ? "local" : "remote" ),
              database, host );
      // if local client, request server callback
      // open database
      switch ( type ) {
      case REPLICA:
        String server;
        if ( local ) {
          try {
            final ServerCredential serverCredential = credentialVault.requestCredential( ServerCredential.class,
                    Scope.CONNECTION, new NotesServerCredentialRequest( path.getServer() ) );
            server = serverCredential.getServer();
          } catch ( final CredentialVaultException e ) {
            server = null;
          }
        } else {
          server = null;
        }
        db = NotesConnector.openDatabaseByReplica( session, database, server );
        break;
      case FILEPATH:
        if ( local ) {
          ThreadManagerImpl.getInstance().initThread();
          db = getSession().getDatabase( null, database );
        } else {
          db = getSession().getDatabase( host, database );
        }
        break;
      default:
        throw new UnsupportedOperationException();
      }
      if ( null == db ) {
        NotesConnector.bundle.log( NotesConnector.DB_EVENT_LEVEL, "open.null", path );
      } else {
        final String server = db.getServer();
        NotesConnector.bundle.log( NotesConnector.DB_EVENT_LEVEL,
                "open." + ( server == null || server.length() == 0 ? "local" : "remote" ) + ".success",
                db.getFilePath(), server );
      }
    }
    return db;
  }

  public static Database openDatabaseByReplica( final Session session, final String database, final String server )
          throws NotesException, DataAccessException {
    final Database db = session.getDatabase( server, database );
    if ( !db.openByReplicaID( server, database ) || !db.isOpen() ) {
      throw new DataAccessException( NotesConnector.bundle.get( "open.error", database ) );
    } else {
      return db;
    }
  }

  private boolean isValid( final Database db ) {
    try {
      // Recycled -> NotesException
      db.isOpen();
      return true;
    } catch ( final NotesException e ) {
      return false;
    }
  }

  /**
   * Returns the database. The database must be open.
   *
   * @return the open database
   */
  Database getDatabase() throws NotesException, DataAccessException {
    synchronized ( this ) {
      if ( null == database || !isValid( database ) ) {
        database = createDatabase();
      }
      if ( !database.isOpen() ) {
        database.open();
      }
      if ( null == databaseMetadata ) {
        databaseMetadata = new DatabaseMetadata( database );
      }
    }
    return database;
  }

  private synchronized void destroy() throws NotesException, DataAccessException {
    try {
      if ( null != database ) {
        database.recycle();
      }
    } finally {
      database = null;
      databaseMetadata = null;
    }
  }

  /*
   * *************************************** I N T E R F A C E M E T H O D S *
   * **************************************
   */

  /**
   * Returns a session to the database.
   *
   * @return a session to the database
   * @throws DataAccessException
   */
  Session getSession() throws NotesException, DataAccessException {
    return getSessionObject();
  }

  /**
   * Destroys the connection to the database. After calling this method, you can
   * call getSession() and getDatabase() again to rebuild the connection.
   *
   * @throws DataAccessException
   */
  public void close() throws DataAccessException {
    try {
      destroy();
    } catch ( final NotesException e ) {
      NotesConnector.bundle.log( Level.WARNING, "destroy.error", Thread.currentThread().getName() );
    }
  }

  /**
   * Automatically destroys the session.
   *
   * @see java.lang.Object#finalize()
   */
  @Override
  protected void finalize() throws Throwable {
    close();
    super.finalize();
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return getModel().hashCode();
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( final Object obj ) {
    if ( this == obj ) {
      return true;
    }
    if ( obj == null ) {
      return false;
    }
    if ( getClass() != obj.getClass() ) {
      return false;
    }
    return getModel().equals( ( ( NotesConnector ) obj ).getModel() );
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Notes Connector to session with connectiondata " + this.getModel();
  }

}
