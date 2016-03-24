package de.ars.daojones.drivers.notes.security;

import javax.security.auth.callback.Callback;

/**
 * A callback for a LTPA token.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class TokenCallback implements Callback {

  private String token;

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append( "TokenCallback [token=" ).append( token ).append( "]" );
    return builder.toString();
  }

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

}
