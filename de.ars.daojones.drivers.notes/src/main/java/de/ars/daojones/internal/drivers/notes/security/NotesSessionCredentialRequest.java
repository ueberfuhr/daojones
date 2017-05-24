package de.ars.daojones.internal.drivers.notes.security;

import java.io.IOException;
import java.util.Locale;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.LanguageCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import de.ars.daojones.drivers.notes.security.SessionFactoryCredential;
import de.ars.daojones.drivers.notes.security.SessionTokenCredential;
import de.ars.daojones.drivers.notes.security.TokenCallback;
import de.ars.daojones.drivers.notes.security.UsernamePasswordCredential;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.spi.database.CredentialVault.CredentialRequest;
import de.ars.daojones.runtime.spi.database.CredentialVaultException;

/**
 * A request that tries a single sign on using a token callback. If this fails,
 * it uses username and password callbacks. If this fails too, anonymous access
 * is created.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class NotesSessionCredentialRequest implements CredentialRequest<SessionFactoryCredential> {

  private static final Messages callbackBundle = Messages.create( "security.CallbackMessages",
          Messages.PUBLIC_NAMESPACE );

  // returns the unsupportedCallback
  private static Callback tryCallbacksAndReturnUnsupported( final CallbackHandler callbackHandler,
          final Callback... callbacks ) throws CredentialVaultException {
    try {
      callbackHandler.handle( callbacks );
      return null;
    } catch ( final IOException e ) {
      throw new CredentialVaultException( e );
    } catch ( final UnsupportedCallbackException e ) {
      return e.getCallback();
    }
  }

  @Override
  public SessionFactoryCredential execute( final CallbackHandler callbackHandler ) throws CredentialVaultException {
    final SessionFactoryCredential result;
    // First: Try token callback
    final TokenCallback tokenCallback = new TokenCallback();
    NotesSessionCredentialRequest.tryCallbacksAndReturnUnsupported( callbackHandler, tokenCallback );
    if ( null != tokenCallback.getToken() ) {
      final SessionTokenCredential stc = new SessionTokenCredential();
      stc.setToken( tokenCallback.getToken() );
      result = stc;
    } else {
      // Second: Try username and password
      // (language callback for prompting the username and password)
      final LanguageCallback languageCallback = new LanguageCallback();
      languageCallback.setLocale( Locale.getDefault() );
      NotesSessionCredentialRequest.tryCallbacksAndReturnUnsupported( callbackHandler, languageCallback );
      final Locale locale = null != languageCallback.getLocale() ? languageCallback.getLocale() : null;
      // name and password
      final NameCallback nameCallback = new NameCallback(
              NotesSessionCredentialRequest.callbackBundle.get( locale, "NameCallback.prompt" ) );
      final PasswordCallback passwordCallback = new PasswordCallback(
              NotesSessionCredentialRequest.callbackBundle.get( locale, "PasswordCallback.prompt" ), false );
      NotesSessionCredentialRequest.tryCallbacksAndReturnUnsupported( callbackHandler, nameCallback, passwordCallback );
      final UsernamePasswordCredential upc = new UsernamePasswordCredential();
      upc.setUsername( nameCallback.getName() );
      if ( null != passwordCallback.getPassword() ) {
        upc.setPassword( String.valueOf( passwordCallback.getPassword() ) );
      }
      result = upc;
    }
    return result;
  }
}