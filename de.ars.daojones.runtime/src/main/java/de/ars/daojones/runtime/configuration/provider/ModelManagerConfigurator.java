package de.ars.daojones.runtime.configuration.provider;

import de.ars.daojones.runtime.configuration.context.ConfigurationModelManager;

/**
 * A common model manager configurator interface.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 * @param <ModelConfiguration>
 *          the model configuration type
 * @param <ModelManager>
 *          the model manager type
 */
public interface ModelManagerConfigurator<ModelConfiguration, ModelManager extends ConfigurationModelManager<?, ?>> {

  /**
   * Configures the model manager.
   * 
   * @param configuration
   *          the model configuration
   * @param modelManager
   *          the model manager
   * @throws ConfigurationException
   */
  void configure( ModelConfiguration configuration, ModelManager modelManager ) throws ConfigurationException;

}
