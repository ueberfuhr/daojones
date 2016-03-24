package de.ars.daojones.runtime.configuration.context;

import java.io.Serializable;
import java.util.Collection;

import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

/**
 * A manager for model elements.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 * @param <Id>
 *          the model id type
 * @param <Model>
 *          the model type
 */
public interface ConfigurationModelManager<Id extends Serializable, Model extends ConfigurationModel<Id>> {

  // TODO add event handler mechanism

  /**
   * Registers a model.
   * 
   * @param model
   *          the model
   * @throws ConfigurationException
   *           if the model is not valid
   */
  public void register( Model model ) throws ConfigurationException;

  /**
   * Removes a model.
   * 
   * @param model
   *          the model
   */
  public void deregister( Model model );

  /**
   * Removes a model with a given id.
   * 
   * @param model
   *          the model
   */
  public void deregister( Id id );

  /**
   * Returns the registered models. This method returns a copy of the models, so
   * changed to this collection do not affect the DaoJones configuration.
   * 
   * @return the registered models
   */
  public Collection<Model> getModels();

  /**
   * Returns a model by id.
   * 
   * @param id
   *          the id
   * @return the model
   */
  public Model getModel( Id id );

}