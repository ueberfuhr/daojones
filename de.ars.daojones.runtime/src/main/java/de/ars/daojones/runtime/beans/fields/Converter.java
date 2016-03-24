package de.ars.daojones.runtime.beans.fields;

import de.ars.daojones.runtime.connections.DataAccessException;

/**
 * A common interface for a converter that maps the database value and the
 * property value.
 * 
 * A converter should be stateless to allow the framework to cache a single
 * instance for multiple fields and even multiple bean types to increase
 * performance.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface Converter {

  /**
   * Loads the database value.
   * 
   * @param context
   *          the context
   * @return the converted value that is assigned to the bean
   * @throws FieldAccessException
   *           if converting the field value occured an error
   * @throws DataAccessException
   *           if reading a field from the database occured an error
   * @throws UnsupportedFieldTypeException
   *           if the field type is not supported by the driver
   */
  Object load( LoadContext context ) throws FieldAccessException, DataAccessException, UnsupportedFieldTypeException;

  /**
   * Converts the property value to the database type. This method is invoked
   * while updating a database field.
   * 
   * @param context
   *          the context
   * @param value
   *          the value that is read from the bean and that has to be converted
   *          and written to the database
   * @throws FieldAccessException
   *           if converting the field value occured an error
   * @throws DataAccessException
   *           if writing a field to the database occured an error
   * @throws UnsupportedFieldTypeException
   *           if the field type is not supported by the driver
   */
  void store( final StoreContext context, Object value ) throws FieldAccessException, DataAccessException,
          UnsupportedFieldTypeException;

}
