package de.ars.daojones.drivers.notes.security;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * A utility class that provides methods to create common callback handlers.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2016
 * @since 2.0
 */
public final class CallbackHandlers {

  private CallbackHandlers() {
  }

  /**
   * Creates a callback handler to access the local notes client.
   *
   * @return the callback handler
   */
  public static CallbackHandler localClient() {
    return new CallbackHandler() {

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
    };

  }

  private static abstract class DelegatingCallbackHandler<T extends Callback> implements CallbackHandler {
    private final CallbackHandler delegate;
    private final Class<? extends T>[] classes;

    public DelegatingCallbackHandler( final CallbackHandler delegate, final Class<? extends T>... classes ) {
      super();
      this.delegate = delegate;
      this.classes = classes;
    }

    public abstract void handle( final T callback ) throws IOException, UnsupportedCallbackException;

    @SuppressWarnings( "unchecked" )
    @Override
    public void handle( final Callback[] callbacks ) throws IOException, UnsupportedCallbackException {
      final List<Callback> unhandledCallbacks = new LinkedList<Callback>();
      for ( int i = 0; i < callbacks.length; i++ ) {
        final Callback callback = callbacks[i];
        boolean handled = false;
        for ( final Class<? extends T> c : classes ) {
          if ( c.isInstance( callback ) ) {
            handle( ( T ) callback );
            handled = true;
            break;
          }
        }
        if ( !handled ) {
          unhandledCallbacks.add( callback );
        }
      }
      if ( !unhandledCallbacks.isEmpty() ) {
        this.delegate.handle( unhandledCallbacks.toArray( new Callback[unhandledCallbacks.size()] ) );
      }
    }

  }

  /**
   * Creates a callback handler to access a given notes server.
   *
   * @param authority
   *          the server name that is used for signing in
   * @param server
   *          the server that provides the database replica
   * @param namePasswordTokenHandler
   *          a callback handler for signin in the server.
   * @return the callback handler
   * @see #server(String, CallbackHandler)
   */
  @SuppressWarnings( "unchecked" )
  public static CallbackHandler server( final String authority, final String server,
          final CallbackHandler namePasswordTokenHandler ) {
    return new DelegatingCallbackHandler<Callback>( namePasswordTokenHandler, AuthorityCallback.class,
            ServerCallback.class ) {

      @Override
      public void handle( final Callback callback ) throws IOException, UnsupportedCallbackException {
        if ( callback instanceof AuthorityCallback ) {
          ( ( AuthorityCallback ) callback ).setAuthority( authority );
        } else if ( callback instanceof ServerCallback ) {
          ( ( ServerCallback ) callback ).setServer( server );
        } else {
          throw new UnsupportedCallbackException( callback );
        }
      }
    };
  }

  /**
   * Creates a callback handler to access a given notes server.
   *
   * @param server
   *          the server that provides the database replica, which is used for
   *          signing in too
   * @param namePasswordTokenHandler
   *          a callback handler for signin in the server.
   * @return the callback handler
   * @see #server(String, String, CallbackHandler)
   */
  public static CallbackHandler server( final String server, final CallbackHandler namePasswordTokenHandler ) {
    return CallbackHandlers.server( server, server, namePasswordTokenHandler );
  }

  /**
   * Creates a callback handler to set user name and password.
   *
   * @param user
   *          user name
   * @param password
   *          password
   * @return the callback handler
   */
  public static CallbackHandler userPassword( final String user, final char[] password ) {
    return new CallbackHandler() {

      @Override
      public void handle( final Callback[] callbacks ) throws IOException, UnsupportedCallbackException {
        for ( int i = 0; i < callbacks.length; i++ ) {
          final Callback callback = callbacks[i];
          if ( callback instanceof NameCallback ) {
            ( ( NameCallback ) callback ).setName( user );
          } else if ( callback instanceof PasswordCallback ) {
            ( ( PasswordCallback ) callback ).setPassword( password );
          } else {
            throw new UnsupportedCallbackException( callback );
          }
        }
      }
    };

  }

  /**
   * Creates a callback handler to use a given LTPA token.
   *
   * @param token
   *          LTPA token
   * @return the callback handler
   */
  public static CallbackHandler token( final String token ) {
    return new CallbackHandler() {

      @Override
      public void handle( final Callback[] callbacks ) throws IOException, UnsupportedCallbackException {
        for ( int i = 0; i < callbacks.length; i++ ) {
          final Callback callback = callbacks[i];
          if ( callback instanceof TokenCallback ) {
            ( ( TokenCallback ) callback ).setToken( token );
          } else {
            throw new UnsupportedCallbackException( callback );
          }
        }
      }
    };

  }

}
