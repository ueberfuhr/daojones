package de.ars.daojones.internal.runtime.security;

import java.io.IOException;
import java.util.Properties;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import de.ars.daojones.internal.runtime.utilities.ConcurrentLazyHashMap.ObjectFactory;
import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.internal.runtime.utilities.PropertiesHelper;
import de.ars.daojones.runtime.configuration.context.CallbackHandlerFactoryModel;
import de.ars.daojones.runtime.configuration.context.CallbackHandlerFactoryModelManager;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.spi.cache.CallbackHandlerException;
import de.ars.daojones.runtime.spi.database.CredentialVault;
import de.ars.daojones.runtime.spi.database.CredentialVaultException;
import de.ars.daojones.runtime.spi.security.CallbackHandlerContext;
import de.ars.daojones.runtime.spi.security.CallbackHandlerContextImpl;
import de.ars.daojones.runtime.spi.security.Credential;

/**
 * A credential vault instance per connection.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class ConnectionCredentialVaultImpl implements CredentialVault {

  private static final Messages bundle = Messages.create( "security.ConnectionCredentialVaultImpl" );

  private final CredentialStore applicationCredentials;
  private final CredentialStore credentialCredentials;
  private final CredentialStore connectionCredentials = new CredentialStore();
  // credential vault for other scopes
  private final ConnectionModel connectionModel;
  private final CallbackHandlerFactoryModelManager callbackHandlerFactoryModelManager;
  private CallbackHandler callbackHandler;

  public ConnectionCredentialVaultImpl( final ConnectionModel connectionModel,
          final CallbackHandlerFactoryModelManager callbackHandlerFactoryModelManager,
          final CredentialStore applicationCredentials, final CredentialStore credentialCredentials ) {
    super();
    this.connectionModel = connectionModel;
    this.callbackHandlerFactoryModelManager = callbackHandlerFactoryModelManager;
    this.credentialCredentials = credentialCredentials;
    this.applicationCredentials = applicationCredentials;
  }

  protected CallbackHandler getCallbackHandler() throws CallbackHandlerException {
    synchronized ( this ) {
      if ( null == callbackHandler ) {
        // create callback handler lazily
        final de.ars.daojones.runtime.configuration.connections.Credential credential = connectionModel.getConnection()
                .getCredential();
        if ( null != credential ) {
          final String type = credential.getType();
          final CallbackHandlerFactoryModel chfm = callbackHandlerFactoryModelManager.getModel( type );
          if ( null == chfm ) {
            throw new CallbackHandlerException( ConnectionCredentialVaultImpl.bundle.get(
                    "error.missingCallbackHandlerFactory", type ) );
          } else {
            try {
              final String application = connectionModel.getId().getApplicationId();
              final Properties properties = PropertiesHelper.createFrom( credential.getProperties() );
              final CallbackHandlerContext ctx = new CallbackHandlerContextImpl( application, properties );
              callbackHandler = chfm.getInstance().createCallbackHandler( ctx );
            } catch ( final ConfigurationException e ) {
              throw new CallbackHandlerException( e );
            }
          }
        } else {
          callbackHandler = new CallbackHandler() {

            @Override
            public void handle( final Callback[] callbacks ) throws IOException, UnsupportedCallbackException {
              throw new UnsupportedCallbackException( callbacks.length > 0 ? callbacks[0] : null );
            }
          };
        }
      }
    }
    return callbackHandler;
  }

  @Override
  public <T extends Credential> T requestCredential( final Class<T> t, final Scope scope,
          final CredentialRequest<T> request ) throws CredentialVaultException {
    switch ( scope ) {
    case CONNECTION:
      return executeCachedCredentialRequest( t, scope, request, connectionCredentials );
    case CREDENTIAL:
      return executeCachedCredentialRequest( t, scope, request, credentialCredentials );
    case APPLICATION:
      return executeCachedCredentialRequest( t, scope, request, applicationCredentials );
    default:
      // transient
      return executeCredentialRequest( t, scope, request );
    }
  }

  @SuppressWarnings( "unchecked" )
  protected <T extends Credential> T executeCachedCredentialRequest( final Class<T> t, final Scope scope,
          final CredentialRequest<T> request, final CredentialStore store ) throws CredentialVaultException {
    final String key = t.getName() + "@" + scope.name();
    return ( T ) store.putIfAbsent( key, new ObjectFactory<Credential, CredentialVaultException>() {

      @Override
      public Credential create() throws CredentialVaultException {
        return executeCredentialRequest( t, scope, request );
      }

    } );
  }

  protected <T extends Credential> T executeCredentialRequest( final Class<T> t, final Scope scope,
          final CredentialRequest<T> request ) throws CredentialVaultException {
    try {
      // long-running, UI blocking invocation!
      return request.execute( getCallbackHandler() );
    } catch ( final CallbackHandlerException e ) {
      throw new CredentialVaultException( e );
    }
  }

}
