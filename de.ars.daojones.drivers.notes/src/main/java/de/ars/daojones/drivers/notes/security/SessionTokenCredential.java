package de.ars.daojones.drivers.notes.security;

import javax.security.auth.login.LoginException;

import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.Session;

/**
 * A credential that creates a session based on a token.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class SessionTokenCredential extends SessionFactoryCredential {

  private static final long serialVersionUID = 1L;

  private String token;

  /**
   * Returns the token.
   * 
   * @return the token
   */
  public String getToken() {
    return token;
  }

  /**
   * Sets the token.
   * 
   * @param token
   *          the token
   */
  public void setToken( final String token ) {
    this.token = token;
  }

  @Override
  public Session login( final AuthorityCredential parameters ) throws LoginException {
    // Single sign-on
    // ======================================
    // createSession(hostString, tokenString) - Internet access is granted to the session based on the token. The token must be a valid token for single sign-on obtained from Session.getSessionToken, the LtpaToken cookie used by WebSphere®, or the HTTP cookie list in a servlet.
    try {
      return NotesFactory.createSession( parameters.getAuthority(), getToken() );
    } catch ( final NotesException e ) {
      final LoginException le = new LoginException();
      le.initCause( e );
      throw le;
    }
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append( "token=" ).append( token );
    return builder.toString();
  }

}
