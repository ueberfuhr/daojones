package de.ars.daojones.drivers.notes;

import java.io.Serializable;

/**
 * An identificator that refers to a single document.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class DocumentIdentificator extends NotesIdentificator {

  private static final long serialVersionUID = 1L;
  private final String unid;
  private final DatabaseIdentificator database;

  /**
   * Creates a document identificator with the given database identificator and
   * universal id.
   * 
   * @param unid
   *          the universal id
   * @param database
   *          the database identificator
   */
  public DocumentIdentificator( final String unid, final DatabaseIdentificator database ) {
    super();
    this.unid = unid;
    this.database = database;
  }

  /**
   * Returns the database identificator.
   * 
   * @return the database identificator
   */
  public DatabaseIdentificator getDatabase() {
    return database;
  }

  /**
   * Returns the universal id.
   * 
   * @return the universal id
   */
  public String getUniversalId() {
    return unid;
  }

  @Override
  public NotesElement getElementType() {
    return NotesElement.DOCUMENT;
  }

  private static final String PREFIX = "DOC:";
  private static final String SEPARATOR = ":PARENT:";

  static boolean isIdentificatorFor( final Serializable ser ) {
    return ser.toString().startsWith( DocumentIdentificator.PREFIX );
  }

  @Override
  public Serializable getId( final String application ) {
    return getId();
  }

  protected String getId() {
    return DocumentIdentificator.PREFIX + getUniversalId() + DocumentIdentificator.SEPARATOR + getDatabase().getId();
  }

  /**
   * Parses an id to a document identificator.
   * 
   * @param ser
   *          the id
   * @return the document identificator
   */
  public static DocumentIdentificator valueOf( final Serializable ser ) {
    DocumentIdentificator result = null;
    if ( null != ser ) {
      final String id = ser.toString();
      if ( id.startsWith( DocumentIdentificator.PREFIX ) ) {
        final int idxUnid = DocumentIdentificator.PREFIX.length();
        final int idxSeparator = id.indexOf( DocumentIdentificator.SEPARATOR );
        final String unid = id.substring( idxUnid, idxSeparator );
        final int idxDb = idxSeparator + DocumentIdentificator.SEPARATOR.length();
        final String db = id.length() > idxDb ? id.substring( idxDb, id.length() ) : null;
        result = new DocumentIdentificator( unid, DatabaseIdentificator.valueOf( db ) );
      }
    }
    return result;

  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( database == null ) ? 0 : database.hashCode() );
    result = prime * result + ( ( unid == null ) ? 0 : unid.hashCode() );
    return result;
  }

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
    final DocumentIdentificator other = ( DocumentIdentificator ) obj;
    if ( database == null ) {
      if ( other.database != null ) {
        return false;
      }
    } else if ( !database.equals( other.database ) ) {
      return false;
    }
    if ( unid == null ) {
      if ( other.unid != null ) {
        return false;
      }
    } else if ( !unid.equals( other.unid ) ) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return getId();
  }

  @Override
  protected NotesIdentificator to( final NotesElement target ) {
    switch ( target ) {
    case DOCUMENT:
      return this;
    default:
      return getDatabase().to( target );
    }
  }
}
