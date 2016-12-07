package de.ars.daojones.drivers.notes.security;

import de.ars.daojones.drivers.notes.NotesDatabasePath;
import de.ars.daojones.runtime.spi.security.Credential;

/**
 * A common credential for a database path.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2016
 * @since 2.0
 */
public class NotesDatabasePathCredential implements Credential {

  private static final long serialVersionUID = 1L;

  private NotesDatabasePath databasePath;

  /**
   * Returns the database path.
   *
   * @return the database path
   */
  public NotesDatabasePath getDatabasePath() {
    return databasePath;
  }

  /**
   * Sets the database path.
   *
   * @param databasePath
   *          the database path
   */
  public void setDatabasePath( final NotesDatabasePath databasePath ) {
    this.databasePath = databasePath;
  }

}
