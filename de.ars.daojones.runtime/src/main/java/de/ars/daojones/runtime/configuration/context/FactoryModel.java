package de.ars.daojones.runtime.configuration.context;

import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

/**
 * A model describing a factory.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 1.2.0
 * @param <T>
 *          the factory type
 */
public interface FactoryModel<T> extends ConfigurationModel<String> {

  /**
   * Returns the factory object.
   * 
   * @return the factory object
   * @throws ConfigurationException
   */
  public abstract T getInstance() throws ConfigurationException;

}