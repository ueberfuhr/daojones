package de.ars.daojones.drivers.notes.security;

import javax.security.auth.login.LoginException;

import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.Session;
import de.ars.daojones.internal.drivers.notes.ThreadManagerImpl;

/**
 * A credential that creates a session based on username and password. If the
 * username is <tt>null</tt>, the user is requested to input the id by the local
 * Notes client, if it is not already open. For remote servers, an anonymous
 * session is created.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class UsernamePasswordCredential extends SessionFactoryCredential {

  private static final long serialVersionUID = 1L;

  private String username;
  private String password;

  /**
   * Returns the user name.
   * 
   * @return the user name
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the user name.
   * 
   * @param username
   *          the user name
   */
  public void setUsername( final String username ) {
    this.username = username;
  }

  /**
   * Returns the password.
   * 
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password.
   * 
   * @param password
   *          the password
   */
  public void setPassword( final String password ) {
    this.password = password;
  }

  private static <T> T nvl( final T t, final T defaultValue ) {
    return null != t ? t : defaultValue;
  }

  @Override
  public Session login( final AuthorityCredential parameters ) throws LoginException {
    //
    try {
      final String authority = parameters.getAuthority();
      if ( null == authority ) {
        ThreadManagerImpl.getInstance().initThread();
        // Local calls based on Notes ID
        // ======================================
        // createSession() - No password verification occurs; the user is prompted for a password if the notes.id file is not open.
        // createSession((String)null, (String)null, (String)null) - Same as the preceding.
        // createSession((String)null, (String)null, passwordString) - Access is granted if the password matches the Notes user ID password.
        //
        // Local calls based on Domino Directory
        // ======================================
        // createSession((String)null, userString, passwordString) - Internet access is granted to the session if the password matches the Internet password in the user's Person record in the Domino Directory.
        return NotesFactory.createSession( ( String ) null, getUsername(), getPassword() );
      } else {
        // Remote calls based on Domino Directory
        // ======================================
        // createSession(hostString, userString, passwordString) - Internet access is granted to the session if the password matches the Internet password in the user's Person record in the host's Domino Directory.
        // createSession(hostString, "", "") - Anonymous Internet access is granted to the session if the host's Server record in the host's Domino Directory permits anonymous access. Parameter two must be an empty string, not null.
        return NotesFactory.createSession( authority, UsernamePasswordCredential.nvl( getUsername(), "" ),
                UsernamePasswordCredential.nvl( getPassword(), "" ) );
      }
    } catch ( final NotesException e ) {
      final LoginException le = new LoginException();
      le.initCause( e );
      throw le;
    }
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append( "username=" ).append( username ).append( ", password=********" );
    return builder.toString();
  }

}
