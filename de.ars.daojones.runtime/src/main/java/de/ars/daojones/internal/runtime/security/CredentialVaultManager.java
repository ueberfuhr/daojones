package de.ars.daojones.internal.runtime.security;

import de.ars.daojones.internal.runtime.utilities.ConcurrentLazyHashMap;
import de.ars.daojones.internal.runtime.utilities.ConcurrentLazyHashMap.ObjectFactory;
import de.ars.daojones.runtime.configuration.connections.Credential;
import de.ars.daojones.runtime.configuration.context.CallbackHandlerFactoryModelManager;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.spi.cache.CallbackHandlerException;
import de.ars.daojones.runtime.spi.database.CredentialVault;

/**
 * A manager for credential vaults.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class CredentialVaultManager {

  // credential connection (per connection+application id)
  private final ConcurrentLazyHashMap<ConnectionModel.Id, CredentialVault, CallbackHandlerException> vaults = new ConcurrentLazyHashMap<ConnectionModel.Id, CredentialVault, CallbackHandlerException>();
  private final CredentialScope<String> applicationCredentialStores = new CredentialScope<String>();
  // per credential instance, because id is only unique within a connection configuration
  private final CredentialScope<Credential> credentialCredentialStores = new CredentialScope<Credential>();
  private final CallbackHandlerFactoryModelManager callbackHandlerFactoryModelManager;

  public CredentialVaultManager( final CallbackHandlerFactoryModelManager callbackHandlerFactoryModelManager ) {
    super();
    this.callbackHandlerFactoryModelManager = callbackHandlerFactoryModelManager;
  }

  public CredentialVault getCredentialVault( final ConnectionModel connectionModel ) throws CallbackHandlerException {
    final ConnectionModel.Id key = connectionModel.getId();
    return vaults.putIfAbsent( key, new ObjectFactory<CredentialVault, CallbackHandlerException>() {

      @Override
      public CredentialVault create() throws CallbackHandlerException {
        return createCredentialVault( connectionModel );
      }

    } );
  }

  protected CredentialVault createCredentialVault( final ConnectionModel connectionModel )
          throws CallbackHandlerException {
    final String application = connectionModel.getId().getApplicationId();
    final Credential credential = connectionModel.getConnection().getCredential();
    return new ConnectionCredentialVaultImpl( //
            connectionModel, //
            callbackHandlerFactoryModelManager,//
            getApplicationCredentials( application ), //
            getCredentialCredentials( credential ) //
    );
  }

  protected CredentialStore getApplicationCredentials( final String application ) throws CallbackHandlerException {
    return applicationCredentialStores.putIfAbsent( application,
            new ObjectFactory<CredentialStore, CallbackHandlerException>() {

              @Override
              public CredentialStore create() throws CallbackHandlerException {
                return new CredentialStore();
              }
            } );
  }

  protected CredentialStore getCredentialCredentials( final Credential credential ) throws CallbackHandlerException {
    return credentialCredentialStores.putIfAbsent( credential,
            new ObjectFactory<CredentialStore, CallbackHandlerException>() {

              @Override
              public CredentialStore create() throws CallbackHandlerException {
                return new CredentialStore();
              }
            } );
  }

  public void clear() {
    applicationCredentialStores.clear();
    credentialCredentialStores.clear();
    vaults.clear();
  }

}
