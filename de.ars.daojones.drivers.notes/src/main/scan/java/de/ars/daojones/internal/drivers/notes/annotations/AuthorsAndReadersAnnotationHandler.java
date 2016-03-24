package de.ars.daojones.internal.drivers.notes.annotations;

import java.lang.reflect.AnnotatedElement;

import de.ars.daojones.drivers.notes.NotesDriverConfiguration;
import de.ars.daojones.drivers.notes.annotations.Authors;
import de.ars.daojones.drivers.notes.annotations.Readers;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.provider.AbstractFieldAnnotationHandler;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

public class AuthorsAndReadersAnnotationHandler extends AbstractFieldAnnotationHandler {

  @Override
  public void handle( final AnnotatedElement element, final DatabaseFieldMappedElement model )
          throws ConfigurationException {
    if ( null != element.getAnnotation( Authors.class ) ) {
      addProperty( model.getFieldMapping(), NotesDriverConfiguration.MODEL_PROPERTY_FIELD_TYPE,
              NotesDriverConfiguration.MODEL_PROPERTY_AUTHORS );
    } else if ( null != element.getAnnotation( Readers.class ) ) {
      addProperty( model.getFieldMapping(), NotesDriverConfiguration.MODEL_PROPERTY_FIELD_TYPE,
              NotesDriverConfiguration.MODEL_PROPERTY_READERS );
    }

  }

}
