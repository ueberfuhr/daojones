package de.ars.daojones.runtime.beans.fields;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import de.ars.daojones.internal.runtime.utilities.ReflectionHelper;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping;
import de.ars.daojones.runtime.connections.DataAccessException;

/**
 * A common super class for a converter that maps the database value directly to
 * the Java value and vice-versa.
 * 
 * A converter should be stateless to allow the framework to cache a single
 * instance for multiple fields and even multiple bean types to increase
 * performance.
 * 
 * @param <D>
 *          the database type
 * @param <P>
 *          the property type
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public abstract class TypeConverter<D, P> implements Converter {

  /**
   * Converts the database value to the property type. This method is invoked
   * while reading a database field.
   * 
   * @param context
   *          the context
   * @param value
   *          the database value
   * @return the Java value
   * @throws FieldAccessException
   * @throws DataAccessException
   */
  protected abstract P toPropertyValue( LoadContext context, D value ) throws FieldAccessException, DataAccessException;

  /**
   * Converts the database value to the property type. This method is invoked in
   * case of auto conversion of array types, i.e. the Java type is an array or
   * collection, whereas the mapped database type returned by
   * {@link #getDatabaseType(ConverterContext)} is a single object type. In this
   * case, the converter reads an array automatically and invokes this method
   * for conversion.
   * 
   * The default behaviour of this method is to iterate through the array and
   * invoke {@link #toPropertyValue(ConverterContext, Object)} for each element.
   * 
   * You can overwrite this method to change the default behaviour, e.g. when a
   * database access is necessary for conversion, you can bundle all conversions
   * to a single database query.
   * 
   * @param context
   *          the context
   * @param source
   *          the array of database values
   * @param target
   *          the array of Java values
   * @return the Java value
   * @throws FieldAccessException
   * @throws DataAccessException
   */
  protected void toPropertyValue( final LoadContext context, final D[] source, final P[] target )
          throws FieldAccessException, DataAccessException {
    // Single element loading
    for ( int i = 0; i < target.length; i++ ) {
      final P value = toPropertyValue( context, source[i] );
      target[i] = value;
    }
  }

  /**
   * Converts the property value to the database type. This method is invoked
   * while updating a database field.
   * 
   * @param context
   *          the context
   * @param values
   *          the Java values
   * @return the database value
   * @throws FieldAccessException
   * @throws DataAccessException
   */
  protected abstract D toDatabaseValue( StoreContext context, P value ) throws FieldAccessException,
          DataAccessException;

  /**
   * Converts the property value to the database type. This method is invoked
   * while updating a database field. This method is invoked in case of auto
   * conversion of array types, i.e. the Java type is an array or collection,
   * whereas the mapped database type returned by
   * {@link #getDatabaseType(ConverterContext)} is a single object type. In this
   * case, the converter writes an array automatically and invokes this method
   * for conversion.
   * 
   * The default behaviour of this method is to iterate through the values and
   * invoke {@link #toDatabaseValue(ConverterContext, Object)} for each element.
   * 
   * You can overwrite this method to change the default behaviour, e.g. when a
   * database access is necessary for conversion, you can bundle all conversions
   * to a single database query.
   * 
   * @param context
   *          the context
   * @param source
   *          the array of Java values
   * @param target
   *          the array of database values
   * @throws FieldAccessException
   * @throws DataAccessException
   */
  protected void toDatabaseValue( final StoreContext context, final P[] source, final D[] target )
          throws FieldAccessException, DataAccessException {
    for ( int i = 0; i < target.length; i++ ) {
      target[i] = toDatabaseValue( context, source[i] );
    }
  }

  /**
   * Returns the type that is read from the database.
   * 
   * @param context
   *          the context
   * @return the type that is read from the database
   */
  protected abstract Class<D> getDatabaseType( ConverterContext context );

  private static boolean isMultiple( final Class<?> c ) {
    return Iterable.class.isAssignableFrom( c ) || c.isArray();
  }

  private Object toResult( final P[] result, final Class<?> c ) {
    return Iterable.class.isAssignableFrom( c ) ? Arrays.asList( result ) : result;
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public final Object load( final LoadContext context ) throws FieldAccessException, DataAccessException,
          UnsupportedFieldTypeException {
    final DatabaseFieldMapping mapping = context.getDescriptor().getFieldMapping();
    final String fieldName = mapping.getName();
    // handle arrays and collections
    final Class<D> dbType = getDatabaseType( context );
    // dbType can be a single object type, a collection or an array
    final Class<P> javaType = ( Class<P> ) context.getTargetType();
    // javaType can be a single object type, a collection or an array
    final boolean isDbMultiple = TypeConverter.isMultiple( dbType );
    final boolean isJavaMultiple = TypeConverter.isMultiple( javaType );
    final boolean isAutoConvert = isJavaMultiple && !isDbMultiple;
    // if the Java type tells to read an array or collection, read an array from database
    try {
      final Class<?> dbAccessType = isAutoConvert ? ReflectionHelper.toArrayType( dbType ) : dbType;
      final Object value = context.getReader().readFromDatabase( fieldName, dbAccessType, context.getMetadata() );
      if ( isAutoConvert ) {
        final int length = null != value ? Array.getLength( value ) : 0;
        final P[] result = ReflectionHelper.createArray( ( Class<P> ) javaType.getComponentType(), length );
        toPropertyValue( context, ( D[] ) value, result );
        return toResult( result, javaType );
      } else {
        final D result = ( D ) value;
        final P convertedValue = toPropertyValue( context, result );
        return convertedValue;
      }
    } catch ( final ClassNotFoundException e ) {
      throw new FieldAccessException( fieldName );
    }
  }

  @SuppressWarnings( "unchecked" )
  private static <T> Collection<T> toCollection( final Object value, final Class<T> componentType ) {
    if ( null == value ) {
      return new LinkedList<T>();
    } else if ( value.getClass().isArray() ) {
      return Arrays.asList( ( T[] ) value );
    } else {
      return ( Collection<T> ) value;
    }
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public final void store( final StoreContext context, final Object value ) throws FieldAccessException,
          DataAccessException, UnsupportedFieldTypeException {
    final DatabaseFieldMapping mapping = context.getDescriptor().getFieldMapping();
    final String fieldName = mapping.getName();
    // handle arrays and collections
    final Class<D> dbType = getDatabaseType( context );
    // dbType can be a single object type, a collection or an array
    final Class<P> javaType = ( Class<P> ) context.getTargetType();
    // javaType can be a single object type, a collection or an array
    final boolean isDbMultiple = TypeConverter.isMultiple( dbType );
    final boolean isJavaMultiple = TypeConverter.isMultiple( javaType );
    final boolean isAutoConvert = isJavaMultiple && !isDbMultiple;
    try {
      final Class<?> dbAccessType = isAutoConvert ? ReflectionHelper.toArrayType( dbType ) : dbType;
      Object valueToStore;
      if ( isAutoConvert ) {
        // if the Java type tells to write an array or collection, write an array to database
        final Collection<P> pList = TypeConverter.toCollection( value, ( Class<P> ) javaType.getComponentType() );
        final P[] pArr = ( P[] ) Array.newInstance( javaType.getComponentType(), pList.size() );
        pList.toArray( pArr );
        final D[] dArr = ( D[] ) Array.newInstance( dbType, pList.size() );
        toDatabaseValue( context, pArr, dArr );
        valueToStore = dArr;
      } else {
        valueToStore = toDatabaseValue( context, ( P ) value );
      }
      context.getWriter().storeToDatabase( fieldName, ( Class<Object> ) dbAccessType, valueToStore,
              mapping.getUpdatePolicy(), context.getMetadata() );
    } catch ( final ClassNotFoundException e ) {
      throw new FieldAccessException( fieldName );
    }
  }

}
