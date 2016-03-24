package de.ars.daojones.drivers.notes.security;

import de.ars.daojones.runtime.spi.security.Credential;

/**
 * A credential that provides a server. The server is used to open databases on
 * servers by using the local Notes client and a replica id.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class ServerCredential implements Credential {

  private static final long serialVersionUID = 1L;

  private String server;

  /**
   * Returns the server.
   * 
   * @return the server
   */
  public String getServer() {
    return server;
  }

  /**
   * Sets the server.
   * 
   * @param server
   *          the server.
   */
  public void setServer( final String server ) {
    this.server = server;
  }

}
