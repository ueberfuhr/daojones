package de.ars.daojones.runtime.docs.database_access_advanced;

import java.lang.reflect.AnnotatedElement;

import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.provider.AbstractFieldAnnotationHandler;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

public class UppercaseHandler extends AbstractFieldAnnotationHandler {

  @Override
  public void handle(final AnnotatedElement element,
          final DatabaseFieldMappedElement model) throws ConfigurationException {
    if (null != element.getAnnotation(Uppercase.class)) {
      addProperty(model.getFieldMapping(), "case", "upper-case");
    }

  }

}
