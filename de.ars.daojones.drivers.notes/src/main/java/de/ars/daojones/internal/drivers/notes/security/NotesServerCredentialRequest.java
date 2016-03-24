package de.ars.daojones.internal.drivers.notes.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import de.ars.daojones.drivers.notes.security.ServerCallback;
import de.ars.daojones.drivers.notes.security.ServerCredential;
import de.ars.daojones.runtime.spi.database.CredentialVault.CredentialRequest;
import de.ars.daojones.runtime.spi.database.CredentialVaultException;

/**
 * A Notes database path credential request that uses a server callback to
 * modify the Notes database path that is given by the model.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class NotesServerCredentialRequest implements CredentialRequest<ServerCredential> {

  private final ServerCallback serverCallback = new ServerCallback();

  public NotesServerCredentialRequest( final String initialValue ) {
    super();
    serverCallback.setServer( initialValue );
  }

  @Override
  public ServerCredential execute( final CallbackHandler callbackHandler ) throws CredentialVaultException {
    try {
      callbackHandler.handle( new Callback[] { serverCallback } );
    } catch ( final IOException e ) {
      throw new CredentialVaultException( e );
    } catch ( final UnsupportedCallbackException e ) {
      // don't do anything
    }
    final ServerCredential result = new ServerCredential();
    result.setServer( serverCallback.getServer() );
    return result;
  }

}