package de.ars.daojones.runtime.beans.fields;

import de.ars.daojones.internal.runtime.utilities.Messages;

/**
 * An exception that occurs when accessing a field.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public class FieldAccessException extends Exception {

  private static final long serialVersionUID = 1L;

  private static final Messages bundle = Messages.create( "beans.fields.FieldAccessException" );

  private final String fieldName;

  /**
   * Creates an instance.
   * 
   * @param fieldName
   *          the name of the field
   */
  public FieldAccessException( final String fieldName ) {
    this( null, fieldName, null );
  }

  /**
   * Creates an instance.
   * 
   * @param message
   *          the message
   * @param fieldName
   *          the name of the field
   */
  public FieldAccessException( final String message, final String fieldName ) {
    this( message, fieldName, null );
  }

  /**
   * Creates an instance.
   * 
   * @param cause
   *          the nested exception
   * @param fieldName
   *          the name of the field
   * @param fieldType
   *          the type of the field
   */
  public FieldAccessException( final String fieldName, final Throwable cause ) {
    this( null, fieldName, cause );
  }

  /**
   * Creates an instance.
   * 
   * @param cause
   *          the nested exception
   * @param message
   *          the message
   * @param fieldName
   *          the name of the field
   * @param fieldType
   *          the type of the field
   */
  public FieldAccessException( final String message, final String fieldName, final Throwable cause ) {
    super( FieldAccessException.getMessage( message, fieldName ), cause );
    this.fieldName = fieldName;
  }

  /**
   * Returns the name of the field.
   * 
   * @return the name of the field
   */
  public String getFieldName() {
    return fieldName;
  }

  private static String getMessage( final String detail, final String fieldName ) {
    return FieldAccessException.bundle.get( "error.message", fieldName, null != detail ? detail
            : FieldAccessException.bundle.get( "error.detail.default" ) );
  }

}
