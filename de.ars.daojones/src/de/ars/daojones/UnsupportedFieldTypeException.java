package de.ars.daojones;

/**
 * This exception is thrown when a {@link FieldAccessor} tries to access
 * a field that has an unsupported type.
 * For a list of supported types see {@link FieldAccessor}.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @see FieldAccessor
 */
public class UnsupportedFieldTypeException extends FieldAccessException {

	private static final long serialVersionUID = 7256611193145637684L;

	/**
	 * Creates an instance.
	 * @param fieldName the name of the field
	 * @param fieldType the type of the field
	 */
	public UnsupportedFieldTypeException(String fieldName, Class<?> fieldType) {
		super("Invalid field type!", fieldName, fieldType);
	}

}
