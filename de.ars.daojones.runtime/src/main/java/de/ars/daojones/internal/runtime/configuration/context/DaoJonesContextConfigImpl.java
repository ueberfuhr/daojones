package de.ars.daojones.internal.runtime.configuration.context;

import de.ars.daojones.internal.runtime.security.CredentialVaultManager;
import de.ars.daojones.runtime.configuration.context.ApplicationModel;
import de.ars.daojones.runtime.configuration.context.ApplicationModelManager;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.context.CacheFactoryModel;
import de.ars.daojones.runtime.configuration.context.CacheFactoryModelManager;
import de.ars.daojones.runtime.configuration.context.CallbackHandlerFactoryModel;
import de.ars.daojones.runtime.configuration.context.CallbackHandlerFactoryModelManager;
import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModel;
import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModelManager;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModelManager;
import de.ars.daojones.runtime.configuration.context.DaoJonesContextConfiguration;
import de.ars.daojones.runtime.configuration.context.GlobalConverterModel;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessor;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorProvider;

public final class DaoJonesContextConfigImpl implements DaoJonesContextConfiguration, BeanAccessorProvider {

  private final ConnectionFactoryModelManagerImpl connectionFactoryModelManager;
  private final CacheFactoryModelManagerImpl cacheFactoryModelManager;
  private final CallbackHandlerFactoryModelManagerImpl callbackHandlerFactoryModelManager;
  private final ApplicationModelManagerImpl applicationModelManager;
  private final ConnectionModelManagerImpl connectionModelManager;
  private final BeanModelManagerImpl beanModelManager;
  private final BeanAccessorProvider beanAccessorProvider;
  private final CredentialVaultManager credentialVaultManager;

  public DaoJonesContextConfigImpl( final EventManager eventManager, final BeanAccessorProvider beanAccessorProvider ) {
    super();
    applicationModelManager = new ApplicationModelManagerImpl( ApplicationModel.class );
    connectionFactoryModelManager = new ConnectionFactoryModelManagerImpl( ConnectionFactoryModel.class );
    cacheFactoryModelManager = new CacheFactoryModelManagerImpl( CacheFactoryModel.class );
    callbackHandlerFactoryModelManager = new CallbackHandlerFactoryModelManagerImpl( CallbackHandlerFactoryModel.class );
    connectionModelManager = new ConnectionModelManagerImpl( ConnectionModel.class, eventManager );
    beanModelManager = new BeanModelManagerImpl( BeanModel.class, GlobalConverterModel.class );
    credentialVaultManager = new CredentialVaultManager( callbackHandlerFactoryModelManager );
    this.beanAccessorProvider = beanAccessorProvider;
  }

  @Override
  public ConnectionFactoryModelManager getConnectionFactoryModelManager() {
    return connectionFactoryModelManager;
  }

  @Override
  public CacheFactoryModelManager getCacheFactoryModelManager() {
    return cacheFactoryModelManager;
  }

  @Override
  public CallbackHandlerFactoryModelManager getCallbackHandlerFactoryModelManager() {
    return callbackHandlerFactoryModelManager;
  }

  @Override
  public ApplicationModelManager getApplicationModelManager() {
    return applicationModelManager;
  }

  @Override
  public ConnectionModelManager getConnectionModelManager() {
    return connectionModelManager;
  }

  @Override
  public BeanModelManager getBeanModelManager() {
    return beanModelManager;
  }

  public CredentialVaultManager getCredentialVaultManager() {
    return credentialVaultManager;
  }

  /**
   * Clears all models.
   */
  public void clear() {
    connectionModelManager.clear();
    applicationModelManager.clear();
    connectionFactoryModelManager.clear();
    cacheFactoryModelManager.clear();
    beanModelManager.clear();
    credentialVaultManager.clear();
  }

  /*
   * non-public API
   */
  @Override
  public BeanAccessor getBeanAccessor() {
    return beanAccessorProvider.getBeanAccessor();
  }

}
