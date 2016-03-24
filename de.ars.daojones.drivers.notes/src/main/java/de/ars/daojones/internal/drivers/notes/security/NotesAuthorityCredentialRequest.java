package de.ars.daojones.internal.drivers.notes.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import de.ars.daojones.drivers.notes.security.AuthorityCallback;
import de.ars.daojones.drivers.notes.security.AuthorityCredential;
import de.ars.daojones.runtime.spi.database.CredentialVault.CredentialRequest;
import de.ars.daojones.runtime.spi.database.CredentialVaultException;

/**
 * A Notes database path credential request that uses an authority callback to
 * modify the Notes database path that is given by the model.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class NotesAuthorityCredentialRequest implements CredentialRequest<AuthorityCredential> {

  private final AuthorityCallback authorityCallback = new AuthorityCallback();

  public NotesAuthorityCredentialRequest( final String initialValue ) {
    super();
    authorityCallback.setAuthority( initialValue );
  }

  @Override
  public AuthorityCredential execute( final CallbackHandler callbackHandler ) throws CredentialVaultException {
    try {
      callbackHandler.handle( new Callback[] { authorityCallback } );
    } catch ( final IOException e ) {
      throw new CredentialVaultException( e );
    } catch ( final UnsupportedCallbackException e ) {
      // don't do anything
    }
    final AuthorityCredential result = new AuthorityCredential();
    result.setAuthority( authorityCallback.getAuthority() );
    return result;
  }

}