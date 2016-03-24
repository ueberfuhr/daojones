package de.ars.daojones.runtime.configuration.provider;

import java.lang.reflect.AnnotatedElement;

import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

/**
 * A handler that customizes the model in case of a custom annotation.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface FieldAnnotationHandler {

  /**
   * Handles an annotated element by customizing the model.
   * 
   * @param element
   *          the annotated element
   * @param model
   *          the model
   * @throws ConfigurationException
   */
  void handle( AnnotatedElement element, DatabaseFieldMappedElement model ) throws ConfigurationException;

}
