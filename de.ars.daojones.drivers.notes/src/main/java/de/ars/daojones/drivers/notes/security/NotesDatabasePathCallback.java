package de.ars.daojones.drivers.notes.security;

import javax.security.auth.callback.Callback;

import de.ars.daojones.drivers.notes.NotesDatabasePath;

/**
 * A generic callback for the database path. The initial value of this callback
 * is set by the database path.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2016
 * @since 2.0
 */
public class NotesDatabasePathCallback implements Callback {

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

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append( "NotesDatabasePathCallback [databasePath=" ).append( databasePath ).append( "]" );
    return builder.toString();
  }

}
