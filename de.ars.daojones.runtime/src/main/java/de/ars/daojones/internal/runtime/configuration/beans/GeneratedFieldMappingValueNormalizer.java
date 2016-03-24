package de.ars.daojones.internal.runtime.configuration.beans;

import de.ars.daojones.internal.runtime.configuration.beans.ModelElementHandler.ModelElementHandlingException;
import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.beans.Constructor;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping;
import de.ars.daojones.runtime.configuration.beans.Field;
import de.ars.daojones.runtime.configuration.beans.Invocable;
import de.ars.daojones.runtime.configuration.beans.Method;
import de.ars.daojones.runtime.configuration.beans.MethodParameter;
import de.ars.daojones.runtime.configuration.beans.MethodResult;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

public class GeneratedFieldMappingValueNormalizer extends DatabaseFieldMappedElementNormalizer {

  @Override
  protected void normalize( final DatabaseFieldMappedElement model, final BeanModelManager beanModelManager )
          throws ConfigurationException {
    final DatabaseFieldMapping fieldMapping = model.getFieldMapping();
    if ( null != fieldMapping && null == model.getFieldMappingRef() ) {
      if ( null == fieldMapping.getName() || "".equals( fieldMapping.getName() ) ) {
        try {
          new ModelElementHandler() {

            @Override
            protected void handle( final MethodParameter model ) throws ConfigurationException {
              final Invocable invokable = model.getInvocable();
              final int idx = invokable.getParameters().indexOf( model );
              if ( invokable instanceof Constructor ) {
                final Bean bean = ( ( Constructor ) invokable ).getDeclaringBean();
                final String typeName = bean.getType();
                final String simpleTypeName = typeName.contains( "." ) ? typeName
                        .substring( typeName.lastIndexOf( '.' ) + 1 ) : typeName;
                fieldMapping.setName( simpleTypeName + Integer.toString( idx ) );
              } else {
                fieldMapping.setName( ( ( Method ) invokable ).getName() + Integer.toString( idx ) );
              }
            }

            @Override
            protected void handle( final MethodResult model ) throws ConfigurationException {
              fieldMapping.setName( ( ( Method ) model.getInvocable() ).getName() + "-result" );
            }

            @Override
            protected void handle( final Field model ) throws ConfigurationException {
              fieldMapping.setName( model.getName() );
            }
          }.handle( model );
        } catch ( final ModelElementHandlingException e ) {
          // could not occur
          throw new ConfigurationException( e );
        }
      }
    }
  }

}
