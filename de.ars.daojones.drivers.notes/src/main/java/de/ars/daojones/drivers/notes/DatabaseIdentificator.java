package de.ars.daojones.drivers.notes;

import java.io.Serializable;

/**
 * An identificator for a database.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class DatabaseIdentificator extends NotesIdentificator {

  private static final long serialVersionUID = 1L;

  private final String replica;
  private final String host;

  /**
   * Creates an identificator to a database with a given replica id and host.
   * 
   * @param replica
   *          the replica id
   * @param host
   *          the host
   */
  public DatabaseIdentificator( final String replica, final String host ) {
    super();
    this.replica = replica;
    this.host = host;
  }

  /**
   * Returns the replica id.
   * 
   * @return the replica id
   */
  public String getReplica() {
    return replica;
  }

  /**
   * Returns the host.
   * 
   * @return the host
   */
  public String getHost() {
    return host;
  }

  private static final String PREFIX = "DB:";
  private static final String SEPARATOR = ":@:";

  static boolean isIdentificatorFor( final Serializable ser ) {
    return ser.toString().startsWith( DatabaseIdentificator.PREFIX );
  }

  @Override
  public Serializable getId( final String application ) {
    return getId();
  }

  protected String getId() {
    return DatabaseIdentificator.PREFIX + getReplica() + DatabaseIdentificator.SEPARATOR
            + ( null != getHost() ? getHost() : "" );
  }

  /**
   * Parses an id to a database identificator.
   * 
   * @param ser
   *          the id
   * @return the database identificator
   */
  public static DatabaseIdentificator valueOf( final Serializable ser ) {
    DatabaseIdentificator result = null;
    if ( null != ser ) {
      final String id = ser.toString();
      if ( id.startsWith( DatabaseIdentificator.PREFIX ) ) {
        final int idxReplica = DatabaseIdentificator.PREFIX.length();
        final int idxSeparator = id.indexOf( DatabaseIdentificator.SEPARATOR );
        final String replica = id.substring( idxReplica, idxSeparator );
        final int idxHost = idxSeparator + DatabaseIdentificator.SEPARATOR.length();
        final String host = id.length() > idxHost ? id.substring( idxHost, id.length() ) : null;
        result = new DatabaseIdentificator( replica, host );
      }
    }
    return result;
  }

  @Override
  public NotesElement getElementType() {
    return NotesElement.DATABASE;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( host == null ) ? 0 : host.hashCode() );
    result = prime * result + ( ( replica == null ) ? 0 : replica.hashCode() );
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
    final DatabaseIdentificator other = ( DatabaseIdentificator ) obj;
    if ( host == null ) {
      if ( other.host != null ) {
        return false;
      }
    } else if ( !host.equals( other.host ) ) {
      return false;
    }
    if ( replica == null ) {
      if ( other.replica != null ) {
        return false;
      }
    } else if ( !replica.equals( other.replica ) ) {
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
    case DATABASE:
      return this;
    default:
      return null;
    }
  }

}
