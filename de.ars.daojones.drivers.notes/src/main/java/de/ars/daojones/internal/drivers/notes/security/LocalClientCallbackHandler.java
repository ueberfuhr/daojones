package de.ars.daojones.internal.drivers.notes.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import de.ars.daojones.drivers.notes.security.AuthorityCallback;
import de.ars.daojones.drivers.notes.security.ServerCallback;

public class LocalClientCallbackHandler implements CallbackHandler {
  @Override
  public void handle( final Callback[] callbacks ) throws IOException, UnsupportedCallbackException {
    for ( int i = 0; i < callbacks.length; i++ ) {
      final Callback callback = callbacks[i];
      if ( callback instanceof AuthorityCallback ) {
        ( ( AuthorityCallback ) callback ).setAuthority( null );
      } else if ( callback instanceof ServerCallback ) {
        ( ( ServerCallback ) callback ).setServer( null );
      } else if ( callback instanceof NameCallback ) {
        ( ( NameCallback ) callback ).setName( null );
      } else if ( callback instanceof PasswordCallback ) {
        ( ( PasswordCallback ) callback ).setPassword( null );
      } else {
        throw new UnsupportedCallbackException( callback );
      }
    }
  }
}