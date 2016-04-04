package de.ars.daojones.drivers.notes;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.internal.drivers.notes.utilities.URIParser;

/**
 * A database path to a Notes database. This is an URI that consists of<br/>
 * <ul>
 * <li><code>notes://</code> as the protocol name</li>
 * <li>The name or IP address of the Notes server. If not specified, the local
 * Notes client is used to communicate with the Notes server.</li>
 * <li>The path to the NSF file or a replica id of the database.</li>
 * <li>A type parameter that specifies if the NSF file or a replica id is part
 * of the URI.</li>
 * <li>A server parameter to specify the server on which the replica of the
 * database is opened. <i>This parameter is only used for local client access to
 * a replica id. This must not be the server that the DaoJones client
 * communicates with. (See examples below.)</i></li>
 * <li>A version parameter with a current value of <code>1.1</code> (for future
 * purposes)</li>
 * </ul>
 * <br/>
 * <b>Examples:</b>
 * <ul>
 * <li>This URI opens the database company/employees.nsf on server acme.com:<br/>
 * <code>notes://acme.com/company/employees.nsf?version=1.1</code></li>
 * <li>This URI opens the database company/employees.nsf using the local Notes
 * client: <br/>
 * <code>notes:///company/employees.nsf?version=1.1</code></li>
 * <li>This URI opens the replica of the database with the given replica id on
 * server acme.com:<br/>
 * <code>notes://acme.com/C1257421103A2141?version=1.1&type=replica</code></li>
 * <li>This URI opens the replica of the database with the given replica id on
 * acme.com (using the local Notes client):<br/>
 * <code>notes:///C1257421103A2141?version=1.1&type=replica&server=acme.com</code>
 * </li>
 * </ul>
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 1.1.0
 */
public class NotesDatabasePath implements Serializable, Cloneable {

  private static final Messages bundle = Messages.create( "NotesDatabasePath" );
  private static final long serialVersionUID = 1L;

  private static final String SCHEME = "notes";
  private static final String PARAMETER_ENCODING = "UTF-8";

  private static final String PROPERTY_TYPE_NAME = "type";
  private static final PathType PROPERTY_TYPE_DEFAULT = PathType.FILEPATH;
  private static final String PROPERTY_VERSION_NAME = "version";
  private static final String PROPERTY_VERSION_DEFAULT = "1.1";
  private static final String PROPERTY_SERVER_NAME = "server";

  /**
   * The type of path that is specified.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH
   * @since 1.1.0
   */
  public static enum PathType {
    /**
     * A replica ID is specified.
     */
    REPLICA,
    /**
     * A file path is specified.
     */
    FILEPATH;
  }

  private PathType type = NotesDatabasePath.PROPERTY_TYPE_DEFAULT;
  private String authority;
  private String server;
  private String database;
  private String version = NotesDatabasePath.PROPERTY_VERSION_DEFAULT;

  /**
   * Returns the type of path.
   * 
   * @return the type of path
   */
  public PathType getType() {
    return type;
  }

  /**
   * Sets the type of path.
   * 
   * @param type
   *          the type of path
   */
  public void setType( final PathType type ) {
    this.type = type;
  }

  /**
   * Returns the authority. The authority is used for the kind of database
   * access.
   * 
   * @return the authority
   */
  public String getAuthority() {
    return authority;
  }

  /**
   * Sets the authority. The authority is used for the kind of database access
   * 
   * @param authority
   *          the authority
   */
  public void setAuthority( final String authority ) {
    this.authority = authority;
  }

  /**
   * Returns true, if the path is a local path.
   * 
   * @return true, if the path is a local path
   */
  public boolean isUseLocalClient() {
    return null == getAuthority() || getAuthority().length() == 0;
  }

  /**
   * Returns the server.
   * 
   * @return the server or <code>null</code> for local access
   */
  public String getServer() {
    return server;
  }

  /**
   * Sets the server.
   * 
   * @param server
   *          the server or <code>null</code> for local access
   */
  public void setServer( final String server ) {
    this.server = server;
  }

  /**
   * Returns the database.
   * 
   * @return the database
   */
  public String getDatabase() {
    return database;
  }

  /**
   * Sets the database.
   * 
   * @param database
   *          the database
   */
  public void setDatabase( final String database ) {
    this.database = database;
  }

  /**
   * Returns the version of this path.
   * 
   * @return the version of this path
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets the version of this path.
   * 
   * @param version
   *          the version of this path
   */
  public void setVersion( final String version ) {
    this.version = version;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( database == null ) ? 0 : database.hashCode() );
    result = prime * result + ( ( authority == null ) ? 0 : authority.hashCode() );
    result = prime * result + ( ( server == null ) ? 0 : server.hashCode() );
    result = prime * result + ( ( type == null ) ? 0 : type.hashCode() );
    result = prime * result + ( ( version == null ) ? 0 : version.hashCode() );
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
    final NotesDatabasePath other = ( NotesDatabasePath ) obj;
    if ( database == null ) {
      if ( other.database != null ) {
        return false;
      }
    } else if ( !database.equals( other.database ) ) {
      return false;
    }
    if ( authority == null ) {
      if ( other.authority != null ) {
        return false;
      }
    } else if ( !authority.equals( other.authority ) ) {
      return false;
    }
    if ( server == null ) {
      if ( other.server != null ) {
        return false;
      }
    } else if ( !server.equals( other.server ) ) {
      return false;
    }
    if ( type == null ) {
      if ( other.type != null ) {
        return false;
      }
    } else if ( !type.equals( other.type ) ) {
      return false;
    }
    if ( version == null ) {
      if ( other.version != null ) {
        return false;
      }
    } else if ( !version.equals( other.version ) ) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append( NotesDatabasePath.SCHEME );
    sb.append( "://" );
    if ( null != authority ) {
      sb.append( authority );
    }
    sb.append( '/' );
    sb.append( database );
    sb.append( '?' );
    sb.append( NotesDatabasePath.PROPERTY_TYPE_NAME );
    sb.append( '=' );
    try {
      sb.append( URLEncoder.encode( type.name().toLowerCase(), NotesDatabasePath.PARAMETER_ENCODING ) );
    } catch ( final UnsupportedEncodingException e ) {
      sb.append( type.name().toLowerCase() );
    }
    if ( null != version ) {
      sb.append( '&' );
      sb.append( NotesDatabasePath.PROPERTY_VERSION_NAME );
      sb.append( '=' );
      try {
        sb.append( URLEncoder.encode( version, NotesDatabasePath.PARAMETER_ENCODING ) );
      } catch ( final UnsupportedEncodingException e ) {
        sb.append( version );
      }
    }
    if ( null != server ) {
      sb.append( '&' );
      sb.append( NotesDatabasePath.PROPERTY_SERVER_NAME );
      sb.append( '=' );
      try {
        sb.append( URLEncoder.encode( server, NotesDatabasePath.PARAMETER_ENCODING ) );
      } catch ( final UnsupportedEncodingException e ) {
        sb.append( server );
      }
    }
    return sb.toString();
  }

  private static String trim( final String text, final Character... characters ) {
    final StringBuffer sb = new StringBuffer( text );
    final Collection<Character> trimChars = new HashSet<Character>( Arrays.asList( characters ) );
    while ( sb.length() > 0 && ( Character.isWhitespace( sb.charAt( 0 ) ) || trimChars.contains( sb.charAt( 0 ) ) ) ) {
      sb.deleteCharAt( 0 );
    }
    while ( sb.length() > 0
            && ( Character.isWhitespace( sb.charAt( sb.length() - 1 ) ) || trimChars
                    .contains( sb.charAt( sb.length() - 1 ) ) ) ) {
      sb.deleteCharAt( sb.length() - 1 );
    }
    return sb.toString();
  }

  @Override
  public NotesDatabasePath clone() {
    try {
      return ( NotesDatabasePath ) super.clone();
    } catch ( final CloneNotSupportedException e ) {
      throw new AssertionError(); // should not occur
    }
  }

  /**
   * Parses a path.
   * 
   * @param path
   *          the path string
   * @return the {@link NotesDatabasePath} object
   * @throws URISyntaxException
   */
  public static NotesDatabasePath valueOf( final String path ) throws URISyntaxException {
    final URI uri = new URI( path );
    if ( !NotesDatabasePath.SCHEME.equals( uri.getScheme() ) ) {
      throw new URISyntaxException( path,
              NotesDatabasePath.bundle.get( "invalidscheme", path, NotesDatabasePath.SCHEME ) );
    }
    final Properties params = URIParser.parseParameters( uri, NotesDatabasePath.PARAMETER_ENCODING );
    final NotesDatabasePath result = new NotesDatabasePath();
    result.setAuthority( uri.getAuthority() );
    result.setDatabase( NotesDatabasePath.trim( uri.getPath(), '/', '\\' ) );
    result.setServer( params.getProperty( NotesDatabasePath.PROPERTY_SERVER_NAME ) );
    result.setType( PathType.valueOf( params.getProperty( NotesDatabasePath.PROPERTY_TYPE_NAME,
            NotesDatabasePath.PROPERTY_TYPE_DEFAULT.name() ).toUpperCase() ) );
    result.setVersion( params.getProperty( NotesDatabasePath.PROPERTY_VERSION_NAME,
            NotesDatabasePath.PROPERTY_VERSION_DEFAULT ) );
    return result;
  }
}
