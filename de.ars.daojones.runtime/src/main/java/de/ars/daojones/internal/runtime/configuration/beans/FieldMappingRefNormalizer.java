package de.ars.daojones.internal.runtime.configuration.beans;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.ars.daojones.internal.runtime.configuration.beans.ModelElementHandler.ModelElementHandlingException;
import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.internal.runtime.utilities.Messages.ExpanderHandler;
import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.beans.Constructor;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappingReference;
import de.ars.daojones.runtime.configuration.beans.Field;
import de.ars.daojones.runtime.configuration.beans.Invocable;
import de.ars.daojones.runtime.configuration.beans.Method;
import de.ars.daojones.runtime.configuration.beans.MethodParameter;
import de.ars.daojones.runtime.configuration.beans.MethodResult;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

public class FieldMappingRefNormalizer extends DatabaseFieldMappedElementNormalizer {

  private static final Messages bundle = Messages.create( "configuration.beans.Normalizer" );

  private final Map<String, DatabaseFieldMapping> fieldMappingById = new HashMap<String, DatabaseFieldMapping>();
  private final Map<DatabaseFieldMappingReference, DatabaseFieldMappedElement> fieldMappingRefs = new HashMap<DatabaseFieldMappingReference, DatabaseFieldMappedElement>();

  @Override
  protected void normalize( final DatabaseFieldMappedElement model, final BeanModelManager beanModelManager )
          throws ConfigurationException {
    final DatabaseFieldMapping fieldMapping = model.getFieldMapping();
    if ( null == model.getFieldMapping() ) {
      fieldMappingRefs.put( model.getFieldMappingRef(), model );
    } else {
      if ( null != fieldMapping.getId() ) {
        fieldMappingById.put( fieldMapping.getId(), fieldMapping );
      }
    }
  }

  @Override
  protected void finish( final Bean model, final BeanModelManager beanModelManager ) throws ConfigurationException {
    fieldMappingRefs.remove( null );
    for ( final Iterator<Map.Entry<DatabaseFieldMappingReference, DatabaseFieldMappedElement>> it = fieldMappingRefs
            .entrySet().iterator(); it.hasNext(); ) {
      final Map.Entry<DatabaseFieldMappingReference, DatabaseFieldMappedElement> entry = it.next();
      final Object mapping = entry.getKey().getMapping();
      if ( null != mapping && mapping instanceof DatabaseFieldMapping ) {
        entry.getValue().setFieldMapping( ( DatabaseFieldMapping ) mapping );
        it.remove();
      } else if ( null != mapping && mapping instanceof String ) {
        // Fuer Annotationen hilfreich, um Aufloesen hier zu zentralisierenT
        final DatabaseFieldMapping mappingObj = fieldMappingById.get( mapping );
        if ( null != mappingObj ) {
          entry.getKey().setMapping( mappingObj );
          entry.getValue().setFieldMapping( mappingObj );
          // delete field mapping ref
          entry.getValue().setFieldMappingRef( null );
          it.remove();
        }
      }
    }
    if ( !fieldMappingRefs.isEmpty() ) {
      // Build error message
      final String key = "error.unresolvedFieldId";
      final StringBuilder sb = new StringBuilder();
      boolean first = true;
      final String separator = FieldMappingRefNormalizer.bundle.get( key + ".separator" );
      for ( final Map.Entry<DatabaseFieldMappingReference, DatabaseFieldMappedElement> entry : fieldMappingRefs
              .entrySet() ) {
        if ( first ) {
          first = false;
        } else {
          sb.append( separator );
        }
        final DatabaseFieldMappedElement element = entry.getValue();
        final StringBuilder sbElement = new StringBuilder();
        try {
          new ModelElementHandler() {

            private String toString( final List<MethodParameter> parameters ) {
              return Messages.expand( parameters, ", ", new ExpanderHandler<MethodParameter>() {

                @Override
                public String handle( final MethodParameter t ) {
                  return t.getType();
                }
              } );
            }

            @Override
            protected void handle( final MethodParameter model ) throws ConfigurationException,
                    ModelElementHandlingException {
              final Invocable invocable = model.getInvocable();
              if ( invocable instanceof Constructor ) {
                final Constructor constructor = ( Constructor ) invocable;
                final List<MethodParameter> parameters = constructor.getParameters();
                sbElement.append( FieldMappingRefNormalizer.bundle.get( key + ".entry.constructor.parameter",
                        toString( parameters ), parameters.indexOf( model ) ) );
              } else {
                final Method method = ( Method ) invocable;
                final List<MethodParameter> parameters = method.getParameters();
                sbElement.append( FieldMappingRefNormalizer.bundle.get( key + ".entry.method.parameter",
                        method.getName(), toString( parameters ), parameters.indexOf( model ) ) );
              }
            }

            @Override
            protected void handle( final MethodResult model ) throws ConfigurationException,
                    ModelElementHandlingException {
              final Invocable invocable = model.getInvocable();
              final Method method = ( Method ) invocable;
              sbElement.append( FieldMappingRefNormalizer.bundle.get( key + ".entry.method.result", method.getName(),
                      toString( method.getParameters() ) ) );
            }

            @Override
            protected void handle( final Field model ) throws ConfigurationException, ModelElementHandlingException {
              sbElement.append( FieldMappingRefNormalizer.bundle.get( key + ".entry.field", model.getName() ) );
            }
          }.handle( element );
        } catch ( final ModelElementHandlingException e ) {
          throw new ConfigurationException( e );
        }
        sb.append( FieldMappingRefNormalizer.bundle.get( key + ".entry", sbElement ) );
      }
      throw new ConfigurationException( FieldMappingRefNormalizer.bundle.get( key, model.getType(), sb ) );
    }
  }
}
