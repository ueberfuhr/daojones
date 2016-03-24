package de.ars.daojones.runtime.configuration.provider;

import java.lang.reflect.AnnotatedElement;

import de.ars.daojones.runtime.beans.annotations.Computed;
import de.ars.daojones.runtime.beans.fields.Properties;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

public class ComputedAnnotationHandler extends AbstractFieldAnnotationHandler {

  @Override
  public void handle( final AnnotatedElement element, final DatabaseFieldMappedElement model )
          throws ConfigurationException {
    final Computed computedAnnotation = element.getAnnotation( Computed.class );
    if ( null != computedAnnotation ) {
      addProperty( model.getFieldMapping(), Properties.COMPUTED_PROPERTY, Boolean.toString( true ) );
    }
  }

}
