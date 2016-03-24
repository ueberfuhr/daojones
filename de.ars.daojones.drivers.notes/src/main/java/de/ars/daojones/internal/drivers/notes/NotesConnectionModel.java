package de.ars.daojones.internal.drivers.notes;

import de.ars.daojones.drivers.notes.NotesDatabasePath;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.spi.database.CredentialVault;

/**
 * Transfer object for the connection model and further Notes-specific
 * information.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0.0
 */
class NotesConnectionModel {

  private final ConnectionModel plainModel;
  private final NotesDatabasePath path;
  private final CredentialVault credentialVault;

  public NotesConnectionModel( final ConnectionModel plainModel, final NotesDatabasePath path,
          final CredentialVault credentialVault ) {
    super();
    this.plainModel = plainModel;
    this.path = path;
    this.credentialVault = credentialVault;
  }

  public ConnectionModel getPlainModel() {
    return plainModel;
  }

  public NotesDatabasePath getPath() {
    return path;
  }

  public CredentialVault getCredentialVault() {
    return credentialVault;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( plainModel == null ) ? 0 : plainModel.hashCode() );
    return result;
  }

  @Override
  public boolean equals( final Object obj ) {
    if ( this == obj ) {
      return true;
    }
    if ( obj == null ) {
      return false;
    }
    if ( getClass() != obj.getClass() ) {
      return false;
    }
    final NotesConnectionModel other = ( NotesConnectionModel ) obj;
    if ( plainModel == null ) {
      if ( other.plainModel != null ) {
        return false;
      }
    } else if ( !plainModel.equals( other.plainModel ) ) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append( "NotesConnectionModel [path=" ).append( path );
    builder.append( ", connection-id=" ).append( plainModel.getId() ).append( "]" );
    return builder.toString();
  }
}
