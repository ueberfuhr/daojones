package de.ars.daojones.internal.runtime.configuration;

import java.io.Serializable;

import de.ars.daojones.runtime.configuration.context.ConfigurationModel;
import de.ars.daojones.runtime.configuration.context.ConfigurationModelManager;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

/**
 * A model normalizer reads the bean model and commonly replaces null values by
 * default values.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <Id>
 *          the id type of the model
 * @param <T>
 *          the model type
 * @param <MM>
 *          the model manager type
 */
public interface ModelNormalizer<Id extends Serializable, T extends ConfigurationModel<Id>, MM extends ConfigurationModelManager<Id, T>> {

  /**
   * This method is invoked after reading a bean configuration and has the task
   * to replace null values by default values.
   * 
   * @param model
   *          the bean model
   * @param modelManager
   * @throws ConfigurationException
   */
  public void normalize( T model, MM modelManager ) throws ConfigurationException;

}
