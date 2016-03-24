package de.ars.daojones.drivers.notes.security;

import de.ars.daojones.runtime.spi.security.Credential;

/**
 * A credential that provides an authority. The authority is the host name or IP
 * address, optionally extended by a port number, of a Domino server where a
 * session is authenticated. If the authority is <tt>null</tt>, the local client
 * or Domino server is used.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class AuthorityCredential implements Credential {

  private static final long serialVersionUID = 1L;

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
   *          the authority.
   */
  public void setAuthority( final String authority ) {
    this.authority = authority;
  }

}
