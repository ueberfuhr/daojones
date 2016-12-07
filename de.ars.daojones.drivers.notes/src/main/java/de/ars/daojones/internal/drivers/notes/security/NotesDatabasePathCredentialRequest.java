package de.ars.daojones.internal.drivers.notes.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import de.ars.daojones.drivers.notes.NotesDatabasePath;
import de.ars.daojones.drivers.notes.security.NotesDatabasePathCallback;
import de.ars.daojones.drivers.notes.security.NotesDatabasePathCredential;
import de.ars.daojones.runtime.spi.database.CredentialVault.CredentialRequest;
import de.ars.daojones.runtime.spi.database.CredentialVaultException;

/**
 * A generic Notes database path credential request.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2016
 * @since 2.0
 */
public class NotesDatabasePathCredentialRequest implements CredentialRequest<NotesDatabasePathCredential> {

  private final NotesDatabasePathCallback callback = new NotesDatabasePathCallback();

  public NotesDatabasePathCredentialRequest( final NotesDatabasePath initialValue ) {
    super();
    callback.setDatabasePath( initialValue );
  }

  @Override
  public NotesDatabasePathCredential execute( final CallbackHandler callbackHandler ) throws CredentialVaultException {
    try {
      callbackHandler.handle( new Callback[] { callback } );
    } catch ( final IOException e ) {
      throw new CredentialVaultException( e );
    } catch ( final UnsupportedCallbackException e ) {
      // don't do anything
    }
    final NotesDatabasePathCredential result = new NotesDatabasePathCredential();
    result.setDatabasePath( callback.getDatabasePath() );
    return result;
  }

}