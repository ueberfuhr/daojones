package de.ars.daojones.runtime.beans.fields;

import java.util.Arrays;
import java.util.List;

import de.ars.daojones.internal.runtime.utilities.ReflectionHelper;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping.UpdatePolicy;
import de.ars.daojones.runtime.configuration.beans.Property;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessor;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorContext;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorProvider;
import de.ars.daojones.runtime.spi.beans.fields.DatabaseAccessor;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;

/**
 * A transfer object providing context information for a converter invocation
 * while loading a bean.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class LoadContext extends ConverterContext {

  private final BeanAccessorProvider beanAccessorProvider;
  private final Reader reader;

  /**
   * Constructor.
   * 
   * @param beanAccessorProvider
   *          the bean accessor provider
   * @param beanAccessorContext
   *          the bean accessor context
   * @param bean
   *          the bean
   * @param descriptor
   *          the field mapping descriptor
   * @param targetType
   *          the target type
   */
  public LoadContext( final BeanAccessorProvider beanAccessorProvider,
          final BeanAccessorContext<?> beanAccessorContext, final Object bean,
          final DatabaseFieldMappedElement descriptor, final Class<?> targetType ) {
    super( beanAccessorContext, bean, descriptor, targetType );
    this.beanAccessorProvider = beanAccessorProvider;
    reader = new Reader() {

      private final DatabaseAccessor db = beanAccessorContext.getDb();

      @Override
      public <E> E readFromDatabase( final String field, final Class<E> c, final Property... metadata )
              throws DataAccessException, UnsupportedFieldTypeException {
        final E result = db.getFieldValue( new FieldContext<E>() {

          @Override
          public String getName() {
            return field;
          }

          @Override
          public Class<? extends E> getType() {
            return c;
          }

          @Override
          public List<Property> getMetadata() {
            return Arrays.asList( metadata );
          }

          @Override
          public UpdatePolicy getUpdatePolicy() {
            return UpdatePolicy.REPLACE;
          }
        } );
        final Class<?> cc = ReflectionHelper.getReferenceType( c );
        if ( null == result || cc.isAssignableFrom( result.getClass() ) ) {
          return result;
        } else {
          throw new UnsupportedFieldTypeException( field, c );
        }
      }

      @Override
      public String[] getFields() throws DataAccessException {
        return db.getFields();
      }

    };
  }

  /**
   * Returns the bean accessor provider.
   * 
   * @return the bean accessor provider
   */
  protected BeanAccessorProvider getBeanAccessorProvider() {
    return beanAccessorProvider;
  }

  /**
   * Creates an instance of a bean type and injects all database values, that
   * are available for the current conversion context, to another bean instance.
   * You can use this if you have multiple attributes of a bean encapsulated
   * into another bean class.
   * 
   * @param <T>
   *          the bean type
   * @return the bean instance
   * @throws FieldAccessException
   *           if accessing or converting a field fails
   * @throws DataAccessException
   *           if accessing the database fails
   * @throws ConfigurationException
   *           if the bean type does not provide a model
   */
  public <Bean> Bean createBean( final Class<Bean> beanType ) throws FieldAccessException, DataAccessException,
          ConfigurationException {
    final BeanAccessorContext<Bean> thisContext = createBeanAccessorContextFor( beanType );
    final BeanAccessor beanAccessor = getBeanAccessorProvider().getBeanAccessor();
    final Bean bean = beanAccessor.createBeanInstance( thisContext );
    return bean;
  }

  /**
   * Returns the reader.
   * 
   * @return the reader
   */
  public Reader getReader() {
    return reader;
  }

  /**
   * A reader is an object that is used to read field values from the database
   * during conversion.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  public static interface Reader {

    /**
     * Reads a field from the database. It is allowed to read multiple fields
     * from the database during conversion.
     * 
     * @param <E>
     *          the field type
     * @param field
     *          the name of the field
     * @param c
     *          the field type
     * @param metadata
     *          the meta data
     * @return the field value
     * @throws DataAccessException
     *           if reading the field from the database occured an error
     * @throws UnsupportedFieldTypeException
     *           if the field type is not supported by the driver
     */
    <E> E readFromDatabase( String field, Class<E> c, Property... metadata ) throws DataAccessException,
            UnsupportedFieldTypeException;

    /**
     * Returns all available field names.
     * 
     * @return the field names
     * @throws DataAccessException
     */
    String[] getFields() throws DataAccessException;

  }

}
