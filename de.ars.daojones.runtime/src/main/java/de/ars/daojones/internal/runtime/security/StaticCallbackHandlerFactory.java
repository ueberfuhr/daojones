package de.ars.daojones.internal.runtime.security;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.LanguageCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.runtime.spi.cache.CallbackHandlerException;
import de.ars.daojones.runtime.spi.security.CallbackHandlerContext;
import de.ars.daojones.runtime.spi.security.CallbackHandlerFactory;

public class StaticCallbackHandlerFactory implements CallbackHandlerFactory {

  public static final String USERNAME_PROPERTY = "username";
  public static final String PASSWORD_PROPERTY = "password";

  private static final Messages bundle = Messages.create( "runtime.security.StaticCallbackHandlerFactory" );

  @Override
  public CallbackHandler createCallbackHandler( final CallbackHandlerContext context ) throws CallbackHandlerException {
    return new CallbackHandler() {

      private final Properties props = context.getProperties();

      @Override
      public void handle( final Callback[] callbacks ) throws IOException, UnsupportedCallbackException {
        // Anonymous access should be possible
        for ( final Callback callback : callbacks ) {
          if ( callback instanceof NameCallback ) {
            final String username = props.getProperty( StaticCallbackHandlerFactory.USERNAME_PROPERTY );
            ( ( NameCallback ) callback ).setName( username );
          } else if ( callback instanceof LanguageCallback ) {
            // do nothing special
            ( ( LanguageCallback ) callback ).setLocale( Locale.getDefault() );
          } else if ( callback instanceof TextOutputCallback ) {
            // do nothing special
            final TextOutputCallback textOutput = ( TextOutputCallback ) callback;
            final Level level;
            switch ( textOutput.getMessageType() ) {
            case TextOutputCallback.ERROR:
              level = Level.SEVERE;
              break;
            case TextOutputCallback.WARNING:
              level = Level.WARNING;
              break;
            default:
              level = Level.INFO;
              break;
            }
            StaticCallbackHandlerFactory.bundle.log( level, textOutput.getMessage() );
          } else if ( callback instanceof PasswordCallback ) {
            final String password = props.getProperty( StaticCallbackHandlerFactory.PASSWORD_PROPERTY );
            if ( null != password ) {
              ( ( PasswordCallback ) callback ).setPassword( password.toCharArray() );
            }
          } else {
            throw new UnsupportedCallbackException( callback );
          }
        }
      }
    };
  }
}
