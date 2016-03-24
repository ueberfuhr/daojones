package de.ars.daojones.runtime.configuration.provider;

import de.ars.daojones.runtime.configuration.context.ApplicationModel;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.CacheFactoryModel;
import de.ars.daojones.runtime.configuration.context.CallbackHandlerFactoryModel;
import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.context.GlobalConverterModel;
import de.ars.daojones.runtime.connections.events.ConnectionEventListener;

/**
 * Adapter class for {@link ConfigurationSource}.<br/>
 * <br/>
 * <b>Development hints:</b><br/>
 * Use this class as super class and overwrite only those methods that you need
 * to implement. This will make your implementation more independent from any
 * interface extensions and your application more compatible.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public abstract class ConfigurationSourceAdapter implements ConfigurationSource {

  /**
   * A generic method that could be overwritten instead of the non-generic
   * methods.
   * 
   * @param modelClass
   *          the model class
   * @return the configuration provider
   * @throws ConfigurationException
   */
  protected <Model> ConfigurationProvider<Model> getConfigurationProviderFor( final Class<Model> modelClass )
          throws ConfigurationException {
    return null;
  }

  @Override
  public ConfigurationProvider<ConnectionFactoryModel> getConnectionFactoryModelConfigurationProvider()
          throws ConfigurationException {
    return getConfigurationProviderFor( ConnectionFactoryModel.class );
  }

  @Override
  public ConfigurationProvider<CacheFactoryModel> getCacheFactoryModelConfigurationProvider()
          throws ConfigurationException {
    return getConfigurationProviderFor( CacheFactoryModel.class );
  }

  @Override
  public ConfigurationProvider<CallbackHandlerFactoryModel> getCallbackHandlerFactoryModelConfigurationProvider()
          throws ConfigurationException {
    return getConfigurationProviderFor( CallbackHandlerFactoryModel.class );
  }

  @Override
  public ConfigurationProvider<ApplicationModel> getApplicationModelConfigurationProvider()
          throws ConfigurationException {
    return getConfigurationProviderFor( ApplicationModel.class );
  }

  @Override
  public ConfigurationProvider<BeanModel> getBeanModelConfigurationProvider() throws ConfigurationException {
    return getConfigurationProviderFor( BeanModel.class );
  }

  @Override
  public ConfigurationProvider<GlobalConverterModel> getGlobalConverterModelConfigurationProvider()
          throws ConfigurationException {
    return getConfigurationProviderFor( GlobalConverterModel.class );
  }

  @Override
  public ConfigurationProvider<ConnectionModel> getConnectionModelConfigurationProvider() throws ConfigurationException {
    return getConfigurationProviderFor( ConnectionModel.class );
  }

  @Override
  public ConfigurationProvider<ConnectionEventListener> getConnectionEventListenerConfigurationProvider()
          throws ConfigurationException {
    return getConfigurationProviderFor( ConnectionEventListener.class );
  }

}
