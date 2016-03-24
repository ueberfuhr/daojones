package de.ars.daojones;

/**
 * An exception that occurs when accessing a property's 
 * field of a DaoJones bean.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class FieldAccessException extends Exception {

	private static final long serialVersionUID = 7256611193145637684L;

	private String fieldName;
	private Class<?> fieldType;
	
	/**
	 * Returns the name of the field.
	 * @return the name of the field
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Returns the type of the field.
	 * @return the type of the field
	 */
	public Class<?> getFieldType() {
		return fieldType;
	}

	/**
	 * Creates an instance.
	 * @param cause the nested exception
	 * @param fieldName the name of the field
	 * @param fieldType the type of the field
	 */
	public FieldAccessException(Throwable cause, String fieldName, Class<?> fieldType) {
		this(null, cause, fieldName, fieldType);
	}

	/**
	 * Creates an instance.
	 * @param message the message
	 * @param fieldName the name of the field
	 * @param fieldType the type of the field
	 */
	public FieldAccessException(String message, String fieldName, Class<?> fieldType) {
		this(message, null, fieldName, fieldType);
	}
	/**
	 * Creates an instance.
	 * @param cause the nested exception
	 * @param message the message
	 * @param fieldName the name of the field
	 * @param fieldType the type of the field
	 */
	public FieldAccessException(String message, Throwable cause, String fieldName, Class<?> fieldType) {
		super(getMessage(getMessage(message, fieldName, fieldType), fieldName, fieldType), cause);
		this.fieldName = fieldName;
		this.fieldType = fieldType;
	}
	
	private static String getMessage(String message, String fieldName, Class<?> fieldType) {
		return (null != message ? "" : message) + " (" + fieldName + "/" + fieldType + ")";
	}

}
