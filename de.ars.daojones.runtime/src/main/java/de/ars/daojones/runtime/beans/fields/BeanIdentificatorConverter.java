package de.ars.daojones.runtime.beans.fields;

import java.util.HashMap;
import java.util.Map;

import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.DataAccessException;

/**
 * A converter that reads an identificator, creates an instance of the target
 * type and injects database values into the bean by using the current
 * conversion context.
 * 
 * You can use this if you have references to other beans as property value.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class BeanIdentificatorConverter extends TypeConverter<Identificator, Object> {

  // Avoid cycles for cyclic referencing elements
  private final static ThreadLocal<Map<Identificator, Object>> cacheMap = new ThreadLocal<Map<Identificator, Object>>();

  @Override
  protected Object toPropertyValue( final LoadContext context, final Identificator value ) throws FieldAccessException,
          DataAccessException {
    Map<Identificator, Object> cache = BeanIdentificatorConverter.cacheMap.get();
    final boolean isCacheMapInitializer = null == cache;
    if ( isCacheMapInitializer ) {
      cache = new HashMap<Identificator, Object>();
      BeanIdentificatorConverter.cacheMap.set( cache );
    }
    try {
      if ( null != value ) {
        if ( cache.containsKey( value ) ) {
          return cache.get( value );
        } else {
          try {
            final Class<?> targetType = context.getTargetType();
            final Connection<?> con = context.getConnectionProvider().getConnection(
                    targetType.isArray() ? targetType.getComponentType() : targetType );
            final Object ce = con.findById( value );
            cache.put( value, ce );
            return ce;
          } catch ( final DataAccessException e ) {
            throw new FieldAccessException( "Unable to find content element by id!", context.getDescriptor()
                    .getFieldMapping().getName(), e );
          }
        }
      } else {
        return null;
      }
    } finally {
      if ( isCacheMapInitializer ) {
        BeanIdentificatorConverter.cacheMap.remove();
      }
    }
  }

  @Override
  protected Identificator toDatabaseValue( final StoreContext context, final Object value )
          throws FieldAccessException, DataAccessException {
    throw new UnsupportedOperationException();
  }

  @Override
  protected Class<Identificator> getDatabaseType( final ConverterContext context ) {
    return Identificator.class;
  }

}
