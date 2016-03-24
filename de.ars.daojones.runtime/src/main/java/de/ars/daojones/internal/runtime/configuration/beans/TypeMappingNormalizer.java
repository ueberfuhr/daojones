package de.ars.daojones.internal.runtime.configuration.beans;

import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

/**
 * Replaces missing type mappings by a type mapping that has the simple name of
 * the class.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class TypeMappingNormalizer implements BeanModelNormalizer {

  @Override
  public void normalize( final BeanModel model, final BeanModelManager beanModelManager ) throws ConfigurationException {
    final Bean bean = model.getBean();
    if ( null == bean.getTypeMapping() ) {
      bean.setTypeMapping( new DatabaseTypeMapping() );
    }
    if ( null == bean.getTypeMapping().getName() ) {
      final String typeName = bean.getType();
      final String simpleTypeName = typeName.contains( "." ) ? typeName.substring( typeName.lastIndexOf( '.' ) + 1 )
              : typeName;
      bean.getTypeMapping().setName( simpleTypeName );
    }
    // default type is declared by TypeMapping class
    //    if ( null == bean.getTypeMapping().getType() ) {
    //      bean.getTypeMapping().setType( DataSourceType.TABLE );
    //    }
  }
}
