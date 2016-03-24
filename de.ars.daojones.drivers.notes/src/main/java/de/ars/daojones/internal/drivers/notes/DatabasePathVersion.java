package de.ars.daojones.internal.drivers.notes;

/**
 * The version of the database URI.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 1.1.0
 */
public enum DatabasePathVersion {

  /**
   * Unspecified version.
   */
  VERSION_UNSPECIFIED( "unspecified" ),
  /**
   * Version 1.1.
   */
  VERSION_1_1( "1.1" );

  private final String identifier;

  private DatabasePathVersion( final String identifier ) {
    this.identifier = identifier;
  }

  /**
   * Returns the identifier for this version.
   * 
   * @return the identifier
   */
  public String getIdentifier() {
    return identifier;
  }

  /**
   * Returns the {@link DatabasePathVersion} by identifier.
   * 
   * @param identifier
   *          the identifier
   * @return the {@link DatabasePathVersion} or {@link #VERSION_UNSPECIFIED}, if
   *         not found
   */
  public static DatabasePathVersion getByIdentifier( final String identifier ) {
    for ( final DatabasePathVersion version : DatabasePathVersion.values() ) {
      if ( version.getIdentifier().equals( identifier ) ) {
        return version;
      }
    }
    return VERSION_UNSPECIFIED;
  }

}
