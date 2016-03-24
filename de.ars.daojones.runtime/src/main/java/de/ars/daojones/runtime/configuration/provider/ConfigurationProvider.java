package de.ars.daojones.runtime.configuration.provider;

/**
 * A common configuration provider interface.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 * @param <Model>
 *          the model type
 */
public interface ConfigurationProvider<Model> {

  /**
   * Reads the configuration without configuring any model manager.
   * 
   * @return the configuration
   * @throws ConfigurationException
   */
  Iterable<Model> readConfiguration() throws ConfigurationException;

}
