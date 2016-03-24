package de.ars.daojones.runtime.beans.fields;

import de.ars.daojones.internal.runtime.utilities.Messages;

/**
 * This exception is thrown when the field type is not supported by the driver.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public class UnsupportedFieldTypeException extends FieldAccessException {

  private static final Messages bundle = Messages.create( "spi.database.UnsupportedFieldTypeException" );
  private static final long serialVersionUID = 7256611193145637684L;

  private final Class<?> fieldType;

  /**
   * Creates an instance.
   * 
   * @param fieldName
   *          the name of the field
   * @param fieldType
   *          the type of the field
   */
  public UnsupportedFieldTypeException( final String fieldName, final Class<?> fieldType ) {
    this( fieldName, fieldType, null );
  }

  /**
   * Creates an instance.
   * 
   * @param fieldName
   *          the name of the field
   * @param fieldType
   *          the type of the field
   * @param cause
   *          the cause
   */
  public UnsupportedFieldTypeException( final String fieldName, final Class<?> fieldType, final Throwable cause ) {
    super( UnsupportedFieldTypeException.bundle.get( "message", fieldName, null != fieldType ? fieldType.getName()
            : null ), fieldName, cause );
    this.fieldType = fieldType;
  }

  /**
   * Returns the invalid field type.
   * 
   * @return the invalid field type
   */
  public Class<?> getFieldType() {
    return fieldType;
  }

}
