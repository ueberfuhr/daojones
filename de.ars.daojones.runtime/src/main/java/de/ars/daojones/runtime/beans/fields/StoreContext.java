package de.ars.daojones.runtime.beans.fields;

import java.util.Arrays;
import java.util.List;

import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping.UpdatePolicy;
import de.ars.daojones.runtime.configuration.beans.Property;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorContext;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorProvider;
import de.ars.daojones.runtime.spi.beans.fields.DatabaseAccessor;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;

/**
 * A transfer object providing context information for a converter invocation
 * while storing a bean.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class StoreContext extends ConverterContext {

  private final BeanAccessorProvider beanAccessorProvider;
  private final Writer writer;

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
  public StoreContext( final BeanAccessorProvider beanAccessorProvider,
          final BeanAccessorContext<?> beanAccessorContext, final Object bean,
          final DatabaseFieldMappedElement descriptor, final Class<?> targetType ) {
    super( beanAccessorContext, bean, descriptor, targetType );
    this.beanAccessorProvider = beanAccessorProvider;
    writer = new Writer() {

      private final DatabaseAccessor db = beanAccessorContext.getDb();

      @Override
      public <E> void storeToDatabase( final String field, final Class<E> type, final E value,
              final UpdatePolicy updatePolicy, final Property... metadata ) throws DataAccessException,
              UnsupportedFieldTypeException {
        db.setFieldValue( new FieldContext<E>() {

          @Override
          public String getName() {
            return field;
          }

          @Override
          public Class<? extends E> getType() {
            return type;
          }

          @Override
          public List<Property> getMetadata() {
            return Arrays.asList( metadata );
          }

          @Override
          public UpdatePolicy getUpdatePolicy() {
            return updatePolicy;
          }
        }, value );
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
   * Returns the reader.
   * 
   * @return the reader
   */
  public Writer getWriter() {
    return writer;
  }

  /**
   * A writer is an object that is used to write a field value during
   * conversion.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  public static interface Writer {

    /**
     * Stores the converted value to the database. It is allowed to store
     * multiple values to fields.
     * 
     * @param <E>
     *          the field type
     * @param field
     *          the name of the field
     * @param type
     *          the database type
     * @param value
     *          the converted value
     * @param updatePolicy
     *          the update policy
     * @param metadata
     *          the meta data
     * @throws DataAccessException
     *           if storing the value to the database occured an error
     * @throws UnsupportedFieldTypeException
     *           if the field type is not supported by the driver
     */
    <E> void storeToDatabase( String field, Class<E> type, E value, UpdatePolicy updatePolicy, Property... metadata )
            throws DataAccessException, UnsupportedFieldTypeException;

  }

}
