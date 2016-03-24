package de.ars.daojones.runtime.spi.beans.fields;

import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.beans.identification.BeanIdentificator;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping.UpdatePolicy;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.ApplicationContext;

/**
 * A component that reads or writes a field and invokes methods and constructors
 * of a bean type.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface BeanAccessor extends BeanIdentificator {

  /**
   * Reads a property value from the bean. This value is the Java property
   * value.
   * 
   * @param bean
   *          the bean
   * @param field
   *          the field
   * @return the value
   * @throws FieldAccessException
   *           if accessing the field fails
   * @throws ConfigurationException
   *           if the bean does not have the given field
   */
  Object getValue( Object bean, DatabaseFieldMappedElement field ) throws FieldAccessException, ConfigurationException;

  /**
   * Reads a property value from the bean. This value is the Database property
   * value.<br/>
   * <b>Please note:</b> The value returned by this method is not the value that
   * is currently stored within the database. It is the converted value that is
   * assigned to the Java bean's property. You can imagine that this value is
   * stored into the database during the next update.
   * 
   * @param ctx
   *          the application context
   * @param bean
   *          the bean
   * @param field
   *          the field
   * @return the value
   * @throws FieldAccessException
   *           if accessing the field fails
   * @throws ConfigurationException
   *           if the bean does not have the given field
   */
  Object getDatabaseValue( final ApplicationContext ctx, final Object bean, final String field )
          throws FieldAccessException, ConfigurationException;

  /**
   * Writes a single value to the bean.
   * 
   * @param bean
   *          the bean
   * @param field
   *          the field
   * @param value
   *          the value
   * @throws FieldAccessException
   *           if accessing the field fails
   * @throws ConfigurationException
   *           if the bean does not have the given field
   * @throws ClassCastException
   *           if the field type does not match the given value
   */
  void setValue( Object bean, DatabaseFieldMappedElement field, Object value ) throws FieldAccessException,
          ConfigurationException, ClassCastException;

  /**
   * Creates an instance of a bean type by invoking the constructor. If there
   * isn't any constructor model, the default constructor is invoked.<br/>
   * <br/>
   * <b>Please note:</b> This method invokes the full injection of constructor
   * parameters, fields and method parameters.
   * 
   * @param <T>
   *          the bean type
   * @param context
   *          the bean accessor context
   * @return the bean instance
   * @throws FieldAccessException
   *           if accessing or converting a field fails
   * @throws DataAccessException
   *           if accessing the database fails
   * @throws ConfigurationException
   *           if the bean does not have the given field
   */
  <T> T createBeanInstance( BeanAccessorContext<T> context ) throws FieldAccessException, DataAccessException,
          ConfigurationException;

  /**
   * Injects field and method parameter values into a bean. At first, fields are
   * injected, then methods are invoked.<br/>
   * <br/>
   * <b>Please note:</b> It is not possible to inject constructor parameters to
   * a bean that was already initialized.
   * 
   * @param <T>
   *          the bean type
   * @param context
   *          the bean accessor context
   * @param bean
   *          the bean
   * @throws FieldAccessException
   *           if accessing or converting a field fails
   * @throws DataAccessException
   *           if accessing the database fails
   * @throws ConfigurationException
   *           if the bean does not have the given field
   * @throws AlreadyInjectingException
   *           if another bean instance with this id is injected at the current
   *           thread
   */
  <T> void inject( BeanAccessorContext<T> context, T bean ) throws FieldAccessException, DataAccessException,
          ConfigurationException, AlreadyInjectingException;

  /**
   * Stores fields and method result values into the database.<br/>
   * 
   * @param <T>
   *          the bean type
   * @param context
   *          the bean accessor context
   * @param bean
   *          the bean
   * @throws FieldAccessException
   *           if accessing or converting a field fails
   * @throws DataAccessException
   *           if accessing the database fails
   * @throws ConfigurationException
   *           if the bean does not have the given field
   */
  <T> void store( BeanAccessorContext<T> context, T bean ) throws FieldAccessException, DataAccessException,
          ConfigurationException;

  /**
   * Injects only the identificator and sets all fields to <tt>null</tt> that
   * have an update policy unequal to {@link UpdatePolicy#REPLACE}.
   * 
   * @param <T>
   *          the bean type
   * @param context
   *          the bean accessor context
   * @param bean
   *          the bean
   * @throws FieldAccessException
   *           if accessing or converting a field fails
   * @throws DataAccessException
   *           if accessing the database fails
   * @throws ConfigurationException
   *           if the bean does not have the given field
   * @throws AlreadyInjectingException
   *           if another bean instance with this id is injected at the current
   *           thread
   */
  <T> void reinjectAfterStore( BeanAccessorContext<T> context, T bean ) throws FieldAccessException,
          DataAccessException, ConfigurationException, AlreadyInjectingException;

}
