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
 * A source where configuration models can be read out from.<br/>
 * <br/>
 * <b>Development hints:</b><br/>
 * Implement this interface directly, if you want to get compiler errors in case
 * of new configuration elements.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public interface ConfigurationSource {

  // more static

  /**
   * Returns the connection factory model configuration provider. This method
   * returns <tt>null</tt>, if this configuration source does not support such a
   * provider.
   * 
   * @return the connection factory model configuration provider
   * @throws ConfigurationException
   */
  ConfigurationProvider<ConnectionFactoryModel> getConnectionFactoryModelConfigurationProvider()
          throws ConfigurationException;

  /**
   * Returns the cache factory model configuration provider. This method returns
   * <tt>null</tt>, if this configuration source does not support such a
   * provider.
   * 
   * @return the cache factory model configuration provider
   * @throws ConfigurationException
   */
  ConfigurationProvider<CacheFactoryModel> getCacheFactoryModelConfigurationProvider() throws ConfigurationException;

  /**
   * Returns the callback handler factory model configuration provider. This
   * method returns <tt>null</tt>, if this configuration source does not support
   * such a provider.
   * 
   * @return the callback handler factory model configuration provider
   * @throws ConfigurationException
   */
  ConfigurationProvider<CallbackHandlerFactoryModel> getCallbackHandlerFactoryModelConfigurationProvider()
          throws ConfigurationException;

  // more dynamic

  /**
   * Returns the application model configuration provider. This method returns
   * <tt>null</tt>, if this configuration source does not support such a
   * provider.
   * 
   * @return the application model configuration provider
   * @throws ConfigurationException
   */
  ConfigurationProvider<ApplicationModel> getApplicationModelConfigurationProvider() throws ConfigurationException;

  /**
   * Returns the bean model configuration provider. This method returns
   * <tt>null</tt>, if this configuration source does not support such a
   * provider.
   * 
   * @return the bean model configuration provider
   * @throws ConfigurationException
   */
  ConfigurationProvider<BeanModel> getBeanModelConfigurationProvider() throws ConfigurationException;

  /**
   * Returns the global converter model configuration provider. This method
   * returns <tt>null</tt>, if this configuration source does not support such a
   * provider.
   * 
   * @return the bean model configuration provider
   * @throws ConfigurationException
   */
  ConfigurationProvider<GlobalConverterModel> getGlobalConverterModelConfigurationProvider()
          throws ConfigurationException;

  /**
   * Returns the connection model configuration provider. This method returns
   * <tt>null</tt>, if this configuration source does not support such a
   * provider.
   * 
   * @return the connection model configuration provider
   * @throws ConfigurationException
   */
  ConfigurationProvider<ConnectionModel> getConnectionModelConfigurationProvider() throws ConfigurationException;

  /**
   * Returns the connection event listener provider. This method returns
   * <tt>null</tt>, if this configuration source does not support such a
   * provider.
   * 
   * @return the connection event listener provider
   * @throws ConfigurationException
   */
  ConfigurationProvider<ConnectionEventListener> getConnectionEventListenerConfigurationProvider()
          throws ConfigurationException;

}
