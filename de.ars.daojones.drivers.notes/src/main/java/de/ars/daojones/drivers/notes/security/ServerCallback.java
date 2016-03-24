package de.ars.daojones.drivers.notes.security;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;

import de.ars.daojones.drivers.notes.NotesDatabasePath;

/**
 * A callback for the server where a database is opened. The initial value of
 * this callback is set by the database path (see
 * {@link NotesDatabasePath#getServer()}). A {@link CallbackHandler}, that
 * supports this type of callback, must support {@link AuthorityCallback} too.
 * Otherwise, it is never requested to specify a server.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class ServerCallback implements Callback {

  private String server;

  /**
   * Returns the host.
   * 
   * @return the host
   */
  public String getServer() {
    return server;
  }

  /**
   * Sets the server.
   * 
   * @param server
   *          the server
   */
  public void setServer( final String server ) {
    this.server = server;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append( "ServerCallback [server=" ).append( server ).append( "]" );
    return builder.toString();
  }

}
