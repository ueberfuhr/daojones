package de.ars.daojones.runtime.configuration.context;

import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

/**
 * A manager for bean models.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public interface BeanModelManager extends ConfigurationModelManager<BeanModel.Id, BeanModel> {

  /**
   * Returns a bean model that contains the mapped fields of the bean type and
   * all implemented interfaces and the super classes. Overwritten mapped
   * elements are excluded.
   * 
   * @param application
   *          the application id
   * @param beanType
   *          the bean type
   * @return the effective bean model that is used at runtime for the mapping or
   *         <tt>null</tt>, if the bean type is not configured
   * @throws ConfigurationException
   *           if there are any conflicts or validation errors in the model
   */
  BeanModel getEffectiveModel( String application, Class<?> beanType ) throws ConfigurationException;

  /**
   * Finds all bean models with a given type mapping. In general, it is possible
   * to map multiple classes to the same database form or view. The connection
   * then decides which type is loaded during a query.<br/>
   * <br/>
   * <b>Please note:</b> This method returns the raw bean models, not the
   * effective bean models. To get the effective bean model, you need to load
   * the bean type (and all of its dependencies) and invoke
   * {@link #getEffectiveModel(String, Class)}.
   * 
   * @param application
   *          the application id
   * @param mapping
   *          the type mapping
   * @return the bean models
   */
  BeanModel[] findModelsByTypeMapping( String application, DatabaseTypeMapping mapping );

  /**
   * Returns the converter model manager. Use this instance to find global
   * converters.
   * 
   * @return the converter model manager
   */
  ConverterModelManager getConverterModelManager();

}
