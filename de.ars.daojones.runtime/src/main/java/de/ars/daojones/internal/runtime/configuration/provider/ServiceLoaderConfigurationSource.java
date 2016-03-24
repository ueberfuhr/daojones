package de.ars.daojones.internal.runtime.configuration.provider;

import java.util.ServiceLoader;

import de.ars.daojones.runtime.configuration.context.ApplicationModel;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.CacheFactoryModel;
import de.ars.daojones.runtime.configuration.context.CallbackHandlerFactoryModel;
import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.context.GlobalConverterModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationProvider;
import de.ars.daojones.runtime.configuration.provider.ConfigurationSource;
import de.ars.daojones.runtime.connections.events.ConnectionEventListener;

public class ServiceLoaderConfigurationSource implements ConfigurationSource {

  private static class ServiceLoaderConfigurationProvider<T> implements ConfigurationProvider<T> {
    private final Class<T> clazz;

    public ServiceLoaderConfigurationProvider( final Class<T> clazz ) {
      super();
      this.clazz = clazz;
    }

    @Override
    public Iterable<T> readConfiguration() throws ConfigurationException {
      return ServiceLoader.load( clazz, clazz.getClassLoader() );
    }

  }

  public static final Class<?>[] serviceClasses = { //
  CacheFactoryModel.class, //
      ConnectionFactoryModel.class, //
      CallbackHandlerFactoryModel.class, //
      ApplicationModel.class, //
      BeanModel.class, //
      GlobalConverterModel.class, //
      ConnectionModel.class, //
      ConnectionEventListener.class //
  };

  @Override
  public ConfigurationProvider<ConnectionFactoryModel> getConnectionFactoryModelConfigurationProvider() {
    return new ServiceLoaderConfigurationProvider<ConnectionFactoryModel>( ConnectionFactoryModel.class );
  }

  @Override
  public ConfigurationProvider<CacheFactoryModel> getCacheFactoryModelConfigurationProvider() {
    return new ServiceLoaderConfigurationProvider<CacheFactoryModel>( CacheFactoryModel.class );
  }

  @Override
  public ConfigurationProvider<CallbackHandlerFactoryModel> getCallbackHandlerFactoryModelConfigurationProvider()
          throws ConfigurationException {
    return new ServiceLoaderConfigurationProvider<CallbackHandlerFactoryModel>( CallbackHandlerFactoryModel.class );
  }

  @Override
  public ConfigurationProvider<ApplicationModel> getApplicationModelConfigurationProvider() {
    return new ServiceLoaderConfigurationProvider<ApplicationModel>( ApplicationModel.class );
  }

  @Override
  public ConfigurationProvider<BeanModel> getBeanModelConfigurationProvider() {
    return new ServiceLoaderConfigurationProvider<BeanModel>( BeanModel.class );
  }

  @Override
  public ConfigurationProvider<GlobalConverterModel> getGlobalConverterModelConfigurationProvider() {
    return new ServiceLoaderConfigurationProvider<GlobalConverterModel>( GlobalConverterModel.class );
  }

  @Override
  public ConfigurationProvider<ConnectionModel> getConnectionModelConfigurationProvider() {
    return new ServiceLoaderConfigurationProvider<ConnectionModel>( ConnectionModel.class );
  }

  @Override
  public ConfigurationProvider<ConnectionEventListener> getConnectionEventListenerConfigurationProvider() {
    return new ServiceLoaderConfigurationProvider<ConnectionEventListener>( ConnectionEventListener.class );
  }

}
