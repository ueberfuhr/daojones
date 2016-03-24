package de.ars.daojones.runtime.spi.database;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;

/**
 * A utility class containing static helper methods for accessing fields,
 * annotations or other elements of DaoJones beans.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 */
public final class AccessorHelper {

  private AccessorHelper() {
  }

  /**
   * Returns true, if the bean type is abstract.
   * 
   * @param beanType
   *          the class
   * @return true, if the bean type is abstract
   */
  public static boolean isAbstract( final Class<?> beanType ) {
    return beanType.isInterface() || ( ( beanType.getModifiers() & Modifier.ABSTRACT ) != 0 );
  }

  /**
   * Returns information about the type mapping of a bean.
   * 
   * @param beanModel
   *          the bean model
   * @return information about the datasource
   */
  public static <T> DatabaseTypeMapping getTypeMapping( final BeanModel beanModel ) {
    return beanModel.getBean().getTypeMapping();
  }

  /**
   * Returns the information about datasources of a bean mapped to the bean
   * type.
   * 
   * @param beanModelManager
   *          the bean model manager
   * @param applicationId
   *          the application id
   * @param beanTypes
   *          the bean types
   * @return the information about datasources
   */
  public static Map<DatabaseTypeMapping, Class<?>> getTypeMappings( final BeanModelManager beanModelManager,
          final String applicationId, final Class<?>... beanTypes ) {
    final Map<DatabaseTypeMapping, Class<?>> result = new HashMap<DatabaseTypeMapping, Class<?>>();
    for ( final Class<?> beanType : beanTypes ) {
      if ( !AccessorHelper.isAbstract( beanType ) ) {
        final BeanModel model = beanModelManager.getModel( new BeanModel.Id( applicationId, beanType.getName() ) );
        if ( null != model ) {
          final DatabaseTypeMapping info = AccessorHelper.getTypeMapping( model );
          result.put( info, beanType );
        }
      }
    }
    return result;
  }
}
