package de.ars.daojones.runtime.beans.fields;

import java.lang.reflect.Array;

import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorContext;
import de.ars.daojones.runtime.spi.beans.fields.DatabaseAccessor;

/**
 * A converter that uses the bean model for the target type to create an
 * instance of this type and inject database values into the bean by using the
 * current conversion context.
 * 
 * You can use this if you have multiple attributes of a bean encapsulated into
 * another bean class.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class BeanDelegateConverter implements Converter {

  /**
   * Returns the target type of conversion. The bean model of this type is used.
   * Overwrite this method if the target type is not the same like the field
   * type (available at the context parameter), but a sub type.
   * 
   * @param context
   *          the context
   * @return the target type
   * @throws FieldAccessException
   * @throws DataAccessException
   * @throws ConfigurationException
   */
  protected Class<?> getTargetType( final LoadContext context ) throws FieldAccessException, DataAccessException,
          ConfigurationException {
    return context.getTargetType();
  };

  @Override
  public Object load( final LoadContext context ) throws FieldAccessException, DataAccessException,
          UnsupportedFieldTypeException {
    try {
      final Class<?> targetType = getTargetType( context );
      if ( null == targetType ) {
        throw new UnsupportedFieldTypeException( context.getDescriptor().getFieldMapping().getName(), targetType );
      } else {
        // Array Support
        final boolean isArrayTarget = targetType.isArray();
        final Class<?> targetComponentType = isArrayTarget ? targetType.getComponentType() : targetType;
        final Object wrapper = context.getBean();
        if ( targetComponentType.isAssignableFrom( wrapper.getClass() ) ) {
          // if the bean is already type of its own attribute -> return the bean
          return BeanDelegateConverter.autobox( wrapper, isArrayTarget, targetComponentType );
        } else {
          try {
            // Try to find an alternative database accessor (driver-specific view entry -> document entry switch)
            final String fieldName = context.getDescriptor().getFieldMapping().getName();
            LoadContext contextToUse = context;
            try {
              final DatabaseAccessor db = context.getReader().readFromDatabase( fieldName, DatabaseAccessor.class,
                      context.getMetadata() );
              if ( null != db ) {
                contextToUse = BeanDelegateConverter.deriveFrom( contextToUse, db );
              }
            } catch ( final UnsupportedFieldTypeException e ) {
              // nothing to do
            }
            final Object bean = contextToUse.createBean( targetType );
            return BeanDelegateConverter.autobox( bean, isArrayTarget, targetComponentType );
          } catch ( final ConfigurationException e ) {
            throw new UnsupportedFieldTypeException( context.getDescriptor().getFieldMapping().getName(), targetType, e );
          }
        }
      }
    } catch ( final ConfigurationException e ) {
      throw new DataAccessException( e );
    }
  }

  private static LoadContext deriveFrom( final LoadContext delegate, final DatabaseAccessor db )
          throws ConfigurationException {
    final BeanAccessorContext<?> bac = BeanAccessorContext.createSubContextFor( delegate.getBeanAccessorContext(),
            delegate.getBeanAccessorContext().getBeanType(), db );
    return new LoadContext( delegate.getBeanAccessorProvider(), bac, delegate.getBean(), delegate.getDescriptor(),
            delegate.getTargetType() );
  }

  private static Object autobox( final Object result, final boolean isArrayTarget, final Class<?> targetComponentType ) {
    if ( isArrayTarget ) {
      // Array Support
      final Object arr = Array.newInstance( targetComponentType, 1 );
      Array.set( arr, 0, result );
      return arr;
    } else {
      return result;
    }
  }

  @Override
  public void store( final StoreContext context, final Object value ) throws FieldAccessException, DataAccessException,
          UnsupportedFieldTypeException {
    // TODO Implement writing values
    throw new UnsupportedOperationException();
  }

}
