package de.ars.daojones.drivers.notes.security;

import javax.security.auth.callback.Callback;

import de.ars.daojones.drivers.notes.NotesDatabasePath;

/**
 * A callback for the host of the Domino server. The initial value of this
 * callback is set by the database path (see
 * {@link NotesDatabasePath#getAuthority()}). The following values can be
 * specified:<br/>
 * <br/>
 * <ul>
 * <li>the name of the server, e.g. <tt>host.acme.com</tt></li>
 * <li>the IP address of the server, e.g. <tt>192.168.0.0</tt></li>
 * <li>one of the previous, followed by a port number, e.g.
 * <tt>host.acme.com:63148</tt></li>
 * <li>a value of <tt>null</tt> to indicate that the local Notes client or
 * Domino server is used</li>
 * </ul>
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class AuthorityCallback implements Callback {

  private String authority;

  /**
   * Returns the authority.
   * 
   * @return the authority
   */
  public String getAuthority() {
    return authority;
  }

  /**
   * Sets the authority.
   * 
   * @param authority
   *          the authority
   */
  public void setAuthority( final String authority ) {
    this.authority = authority;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append( "AuthorityCallback [authority=" ).append( authority ).append( "]" );
    return builder.toString();
  }

}
