package de.ars.daojones.runtime.spi.beans.fields;

import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.connections.DataAccessException;

/**
 * An object that accesses fields within the database.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface DatabaseAccessor {

  /**
   * Returns the identificator.
   * 
   * @return the identificator
   * @throws DataAccessException
   */
  Identificator getIdentificator() throws DataAccessException;

  /**
   * Reads a field value.
   * 
   * @param <E>
   *          the field type
   * @param context
   *          the context
   * @return the field value
   * @throws DataAccessException
   *           if reading the field from the database occured an error
   * @throws UnsupportedFieldTypeException
   *           if the field type is not supported by the driver
   */
  <E> E getFieldValue( FieldContext<E> context ) throws DataAccessException, UnsupportedFieldTypeException;

  /**
   * Sets a field value. This value change must not be persistent until the
   * {@link #store()} method is invoked.
   * 
   * @param <E>
   *          the field type
   * @param context
   *          the context
   * @param value
   *          the converted value
   * @throws DataAccessException
   *           if storing the value to the database occured an error
   * @throws UnsupportedFieldTypeException
   *           if the field type is not supported by the driver
   */
  <E> void setFieldValue( FieldContext<E> context, E value ) throws DataAccessException, UnsupportedFieldTypeException;

  /**
   * Returns all available field names.
   * 
   * @return the field names
   * @throws DataAccessException
   */
  String[] getFields() throws DataAccessException;

}
