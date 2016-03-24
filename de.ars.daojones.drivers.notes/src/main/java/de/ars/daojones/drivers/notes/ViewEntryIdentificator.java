package de.ars.daojones.drivers.notes;

import java.io.Serializable;

/**
 * An identificator that refers to a single document that is displayed as an
 * entry within a view. A document can have multiple view entries in case of
 * multi-value fields where each values is displayed as a separate entry.<br/>
 * <br/>
 * <b>Please note:</b> A view entry identificator can only address only one
 * entry for a document within a view. If the view displays multiple entries for
 * one document, only the first reference is determined.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class ViewEntryIdentificator extends NotesIdentificator {

  private static final long serialVersionUID = 1L;
  private final String unid;
  private final ViewIdentificator view;

  /**
   * Creates a view entry identificator with the given view identificator and
   * universal id.
   * 
   * @param unid
   *          the universal id
   * @param database
   *          the view identificator
   */
  public ViewEntryIdentificator( final String unid, final ViewIdentificator view ) {
    super();
    this.unid = unid;
    this.view = view;
  }

  /**
   * Returns the identificator that refers to the document out of the view.
   * 
   * @return the document identificator
   */
  public DocumentIdentificator toDocumentIdentificator() {
    return new DocumentIdentificator( unid, view.getDatabase() );
  }

  /**
   * Returns the view identificator.
   * 
   * @return the view identificator
   */
  public ViewIdentificator getView() {
    return view;
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
    return NotesElement.VIEW_ENTRY;
  }

  private static final String PREFIX = "ENTRY:";
  private static final String SEPARATOR = ":VIEW:";

  static boolean isIdentificatorFor( final Serializable ser ) {
    return ser.toString().startsWith( ViewEntryIdentificator.PREFIX );
  }

  @Override
  public Serializable getId( final String application ) {
    return getId();
  }

  protected String getId() {
    return ViewEntryIdentificator.PREFIX + getUniversalId() + ViewEntryIdentificator.SEPARATOR + getView().getId();
  }

  /**
   * Parses an id to a document identificator.
   * 
   * @param ser
   *          the id
   * @return the document identificator
   */
  public static ViewEntryIdentificator valueOf( final Serializable ser ) {
    ViewEntryIdentificator result = null;
    if ( null != ser ) {
      final String id = ser.toString();
      if ( id.startsWith( ViewEntryIdentificator.PREFIX ) ) {
        final int idxUnid = ViewEntryIdentificator.PREFIX.length();
        final int idxSeparator = id.indexOf( ViewEntryIdentificator.SEPARATOR );
        final String unid = id.substring( idxUnid, idxSeparator );
        final int idxDb = idxSeparator + ViewEntryIdentificator.SEPARATOR.length();
        final String db = id.length() > idxDb ? id.substring( idxDb, id.length() ) : null;
        result = new ViewEntryIdentificator( unid, ViewIdentificator.valueOf( db ) );
      }
    }
    return result;

  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( view == null ) ? 0 : view.hashCode() );
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
    final ViewEntryIdentificator other = ( ViewEntryIdentificator ) obj;
    if ( view == null ) {
      if ( other.view != null ) {
        return false;
      }
    } else if ( !view.equals( other.view ) ) {
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
    case VIEW_ENTRY:
      return this;
    case DOCUMENT:
      return toDocumentIdentificator();
    default:
      return getView().to( target );
    }
  }

}
