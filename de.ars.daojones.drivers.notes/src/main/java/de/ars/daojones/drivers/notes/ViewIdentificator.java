package de.ars.daojones.drivers.notes;

import java.io.Serializable;

/**
 * An identificator that refers to a view.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class ViewIdentificator extends NotesIdentificator {

  private static final long serialVersionUID = 1L;
  private final String name;
  private final DatabaseIdentificator database;

  /**
   * Creates a view identificator with the given database identificator and
   * universal id.
   * 
   * @param name
   *          the view name
   * @param database
   *          the database identificator
   */
  public ViewIdentificator( final String name, final DatabaseIdentificator database ) {
    super();
    this.name = name;
    this.database = database;
  }

  /**
   * Returns the database identificator.
   * 
   * @return the database the database identificator
   */
  public DatabaseIdentificator getDatabase() {
    return database;
  }

  /**
   * Returns the view name.
   * 
   * @return the view name
   */
  public String getViewName() {
    return name;
  }

  @Override
  public NotesElement getElementType() {
    return NotesElement.VIEW;
  }

  private static final String PREFIX = "VIEW:";
  private static final String SEPARATOR = ":PARENT:";

  static boolean isIdentificatorFor( final Serializable ser ) {
    return ser.toString().startsWith( ViewIdentificator.PREFIX );
  }

  @Override
  public Serializable getId( final String application ) {
    return getId();
  }

  protected String getId() {
    return ViewIdentificator.PREFIX + getViewName() + ViewIdentificator.SEPARATOR + getDatabase().getId();
  }

  /**
   * Parses an id to a view identificator.
   * 
   * @param ser
   *          the id
   * @return the view identificator
   */
  public static ViewIdentificator valueOf( final Serializable ser ) {
    ViewIdentificator result = null;
    if ( null != ser ) {
      final String id = ser.toString();
      if ( id.startsWith( ViewIdentificator.PREFIX ) ) {
        final int idxName = ViewIdentificator.PREFIX.length();
        final int idxSeparator = id.indexOf( ViewIdentificator.SEPARATOR );
        final String name = id.substring( idxName, idxSeparator );
        final int idxDb = idxSeparator + ViewIdentificator.SEPARATOR.length();
        final String db = id.length() > idxDb ? id.substring( idxDb, id.length() ) : null;
        result = new ViewIdentificator( name, DatabaseIdentificator.valueOf( db ) );
      }
    }
    return result;

  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( database == null ) ? 0 : database.hashCode() );
    result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
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
    final ViewIdentificator other = ( ViewIdentificator ) obj;
    if ( database == null ) {
      if ( other.database != null ) {
        return false;
      }
    } else if ( !database.equals( other.database ) ) {
      return false;
    }
    if ( name == null ) {
      if ( other.name != null ) {
        return false;
      }
    } else if ( !name.equals( other.name ) ) {
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
    case VIEW:
      return this;
    default:
      return getDatabase().to( target );
    }
  }

}
