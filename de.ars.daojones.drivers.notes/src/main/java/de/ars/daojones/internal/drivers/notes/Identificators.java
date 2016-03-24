package de.ars.daojones.internal.drivers.notes;

import java.io.Serializable;

import lotus.domino.Base;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import de.ars.daojones.drivers.notes.DatabaseIdentificator;
import de.ars.daojones.drivers.notes.DocumentIdentificator;
import de.ars.daojones.drivers.notes.NotesIdentificator;
import de.ars.daojones.drivers.notes.ViewEntryIdentificator;
import de.ars.daojones.drivers.notes.ViewIdentificator;
import de.ars.daojones.runtime.connections.DataAccessException;

/**
 * This internal factory class provides factory methods to easily create
 * identificator objects. These methods must not be provided as constructors
 * directly within the identificator types, because the Notes API should not be
 * part of the public API.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public final class Identificators {

  private Identificators() {
    super();
  }

  public static DatabaseIdentificator createIdentificator( final Database db ) throws NotesException {
    return new DatabaseIdentificator( db.getReplicaID(), db.getServer() );
  }

  public static Database findReference( final Session session, final Database currentDatabase,
          final DatabaseIdentificator identificator ) throws NotesException, DataAccessException {
    if ( null != identificator ) {
      if ( null != currentDatabase && identificator.equals( Identificators.createIdentificator( currentDatabase ) ) ) {
        return currentDatabase;
      } else {
        return NotesConnector.openDatabaseByReplica( session, identificator.getReplica(), identificator.getHost() );
      }
    } else {
      return null;
    }

  }

  /**
   * Creates a document identificator that refers to the given document.
   * 
   * @param document
   *          the document that is referenced
   * @throws NotesException
   */
  public static DocumentIdentificator createIdentificator( final Document document ) throws NotesException {
    final DatabaseIdentificator db = Identificators.createIdentificator( document.getParentDatabase() );
    return new DocumentIdentificator( document.getUniversalID(), db );
  }

  public static Document findReference( final Session session, final Database currentDatabase,
          final DocumentIdentificator identificator ) throws NotesException, DataAccessException {
    final Database db = Identificators.findReference( session, currentDatabase, identificator.getDatabase() );
    return db.getDocumentByUNID( identificator.getUniversalId() );
  }

  /**
   * Creates a view entry identificator that refers to the given view entry.
   * 
   * @param view
   *          the view
   * @param entry
   *          the view entry that is referenced
   * @throws NotesException
   */
  public static ViewEntryIdentificator createIdentificator( final View view, final ViewEntry entry )
          throws NotesException {
    final ViewIdentificator viewId = Identificators.createIdentificator( view );
    return new ViewEntryIdentificator( entry.getUniversalID(), viewId );
  }

  public static ViewEntry findReference( final Session session, final Database currentDatabase,
          final ViewEntryIdentificator identificator ) throws NotesException, DataAccessException {
    final View view = Identificators.findReference( session, currentDatabase, identificator.getView() );
    final Document doc = Identificators.findReference( session, currentDatabase,
            identificator.toDocumentIdentificator() );
    return NotesViewHelper.findViewEntry( view, doc );
  }

  /**
   * Creates a view identificator that refers to the given view.
   * 
   * @param view
   *          the view that is referenced
   * @throws NotesException
   */
  public static ViewIdentificator createIdentificator( final View view ) throws NotesException {
    final DatabaseIdentificator db = Identificators.createIdentificator( view.getParent() );
    return new ViewIdentificator( view.getName(), db );
  }

  public static View findReference( final Session session, final Database currentDatabase,
          final ViewIdentificator identificator ) throws NotesException, DataAccessException {
    final Database db = Identificators.findReference( session, currentDatabase, identificator.getDatabase() );
    return db.getView( identificator.getViewName() );
  }

  public static Base findReference( final Session session, final Database currentDatabase,
          final NotesIdentificator identificator ) throws NotesException, DataAccessException {
    switch ( identificator.getElementType() ) {
    case DATABASE:
      return Identificators.findReference( session, currentDatabase, ( DatabaseIdentificator ) identificator );
    case DOCUMENT:
      return Identificators.findReference( session, currentDatabase, ( DocumentIdentificator ) identificator );
    case VIEW:
      return Identificators.findReference( session, currentDatabase, ( ViewIdentificator ) identificator );
    case VIEW_ENTRY:
      return Identificators.findReference( session, currentDatabase, ( ViewEntryIdentificator ) identificator );
    default:
      throw new IllegalArgumentException( "" + identificator.getElementType().name() );
    }
  }

  public static NotesIdentificator valueOf( final Serializable ser ) {
    return NotesIdentificator.valueOf( ser );
  }

}
