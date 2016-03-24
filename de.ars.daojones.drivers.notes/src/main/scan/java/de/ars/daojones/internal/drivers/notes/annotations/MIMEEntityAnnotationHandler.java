package de.ars.daojones.internal.drivers.notes.annotations;

import java.lang.reflect.AnnotatedElement;

import de.ars.daojones.drivers.notes.NotesDriverConfiguration;
import de.ars.daojones.drivers.notes.annotations.MIMEEntity;
import de.ars.daojones.drivers.notes.annotations.MIMEEntityType;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.provider.AbstractFieldAnnotationHandler;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

public class MIMEEntityAnnotationHandler extends AbstractFieldAnnotationHandler {

  @Override
  public void handle( final AnnotatedElement element, final DatabaseFieldMappedElement model )
          throws ConfigurationException {
    final MIMEEntity me = element.getAnnotation( MIMEEntity.class );
    if ( null != me ) {
      addProperty( model.getFieldMapping(), NotesDriverConfiguration.MODEL_PROPERTY_FIELD_TYPE,
              NotesDriverConfiguration.MODEL_PROPERTY_MIME_ENTITY );
      final MIMEEntityType meType = me.type();
      if ( null != meType && meType != MIMEEntityType.ATTACHMENT ) {
        addProperty( model.getFieldMapping(), NotesDriverConfiguration.MODEL_PROPERTY_MIME_ENTITY_TYPE, meType.name()
                .toLowerCase() );
      }
    }

  }

}
