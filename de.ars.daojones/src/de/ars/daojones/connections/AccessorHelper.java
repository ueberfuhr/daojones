package de.ars.daojones.connections;

import java.util.HashMap;
import java.util.Map;

import de.ars.daojones.annotations.Abstract;
import de.ars.daojones.annotations.DataSource;
import de.ars.daojones.annotations.model.DataSourceInfo;

/**
 * A utility class containing static helper methods for accessing fields,
 * annotations or other elements of DaoJones beans.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class AccessorHelper {

  private AccessorHelper() {
  }

  /**
   * Returns true if the bean has the {@link Abstract} annotation
   * 
   * @param beanType
   *          the class
   * @return true, if the bean class is {@link Abstract}
   */
  public static boolean isAbstract( Class<?> beanType ) {
    return beanType.getAnnotation( Abstract.class ) != null;
  }

  /**
   * Returns information about the {@link DataSource} of a DaoJones bean.
   * 
   * @param beanType
   *          the DaoJones bean class
   * @return information about the {@link DataSource}
   */
  @SuppressWarnings( "unchecked" )
  public static DataSourceInfo getDataSource( Class<?> beanType ) {
    if ( null == beanType
        || beanType.getClass().isAssignableFrom( Object.class ) )
      return null;
    final DataSource d = beanType.getAnnotation( DataSource.class );
    if ( null != d )
      return new DataSourceInfo( d );
    final DataSourceInfo superInfo = getDataSource( beanType.getSuperclass() );
    if ( null != superInfo )
      return superInfo;
    for ( Class i : beanType.getInterfaces() ) {
      final DataSourceInfo interfaceInfo = getDataSource( i );
      if ( null != interfaceInfo )
        return interfaceInfo;
    }
    return null;
  }

  /**
   * Returns the information about {@link DataSource}s of a DaoJones bean mapped
   * to the bean type.
   * 
   * @param beanTypes
   *          the bean types
   * @return the information about {@link DataSource}s
   */
  public static Map<DataSourceInfo, Class<?>> getDataSources(
      Class<?>... beanTypes ) {
    final Map<DataSourceInfo, Class<?>> result = new HashMap<DataSourceInfo, Class<?>>();
    for ( Class<?> beanType : beanTypes ) {
      if(!isAbstract( beanType )) {
        final DataSourceInfo info = getDataSource( beanType );
        result.put( info, beanType );
      }
    }
    return result;
  }

}
