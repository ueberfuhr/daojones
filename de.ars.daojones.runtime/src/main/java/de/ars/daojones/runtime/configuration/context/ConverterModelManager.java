package de.ars.daojones.runtime.configuration.context;

import de.ars.daojones.runtime.configuration.beans.GlobalConverter;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

/**
 * A manager for global converter models.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public interface ConverterModelManager extends ConfigurationModelManager<GlobalConverterModel.Id, GlobalConverterModel> {

  /**
   * Finds a global converter for a given type. The converter could also be a
   * converter for a super type of the given class.
   * 
   * @param application
   *          the application
   * @param type
   *          the type
   * @return the converter
   * @throws ConfigurationException
   */
  GlobalConverter findConverterFor( String application, Class<?> type ) throws ConfigurationException;

}
