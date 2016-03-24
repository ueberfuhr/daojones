package de.ars.daojones.runtime.configuration.provider;

import java.lang.reflect.AnnotatedElement;

import de.ars.daojones.runtime.beans.annotations.Metadata;
import de.ars.daojones.runtime.beans.annotations.Property;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

public class PropertyAnnotationHandler extends AbstractFieldAnnotationHandler {

  @Override
  public void handle( final AnnotatedElement element, final DatabaseFieldMappedElement model )
          throws ConfigurationException {
    final Property propertyAnnotation = element.getAnnotation( Property.class );
    if ( null != propertyAnnotation ) {
      handle( model, propertyAnnotation );
    }
    final Metadata metadataAnnotation = element.getAnnotation( Metadata.class );
    if ( null != metadataAnnotation ) {
      handle( model, metadataAnnotation.value() );
    }
  }

  protected void handle( final DatabaseFieldMappedElement model, final Property... properties )
          throws ConfigurationException {
    for ( final Property p : properties ) {
      addProperty( model.getFieldMapping(), p.name(), p.value().length() == 0 ? null : p.value() );
    }
  }

}
