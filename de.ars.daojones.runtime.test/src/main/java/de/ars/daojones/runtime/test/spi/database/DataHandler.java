package de.ars.daojones.runtime.test.spi.database;

import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;

/**
 * Implementations of this interface convert objects of any type to the property
 * value of type String and vice-versa. Create a custom property converter for
 * custom or driver-specific types that this Test Support is not aware of.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <E>
 *          the field type
 */
public interface DataHandler<E> extends Aware<E> {

  /**
   * Converts the string property value to the field type.
   * 
   * @param context
   *          the field context
   * @param index
   *          the test model index
   * @param value
   *          the property value
   * @return the field value
   * @throws DataAccessException
   * @throws UnsupportedFieldTypeException
   */
  E convertRead( final FieldContext<E> context, TestModelIndex index, final String value ) throws DataAccessException,
          UnsupportedFieldTypeException;

  /**
   * Converts the field type to the string property.
   * 
   * @param context
   *          the field context
   * @param index
   *          the test model index
   * @param value
   *          the field value
   * @return the string property
   * @throws DataAccessException
   * @throws UnsupportedFieldTypeException
   */
  String convertWrite( final FieldContext<E> context, TestModelIndex index, final E value ) throws DataAccessException,
          UnsupportedFieldTypeException;

}
