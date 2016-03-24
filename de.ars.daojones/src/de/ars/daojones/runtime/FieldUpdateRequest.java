package de.ars.daojones.runtime;

/**
 * A simple transfer object for a request to save a field.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T> the field type
 */
class FieldUpdateRequest<T> {

	private Field<T> field;
	private T value;
	public FieldUpdateRequest(Field<T> field, T value) {
		super();
		this.field = field;
		this.value = value;
	}
	public FieldUpdateRequest() {
		super();
	}
	public Field<T> getField() {
		return field;
	}
	public void setField(Field<T> field) {
		this.field = field;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	
}
