package de.ars.daojones;

/**
 * A transfer object containing information
 * about a method parameter.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T> the parameter type
 */
public class Parameter<T> {

	private T value;
	private Class<? super T> type;
	
	/**
	 * Creates an instance based on a given value.
	 * The type of the parameter will be the concrete type of the value.
	 * If value is null, the type will be {@link Object}.
	 * @param value the value
	 */
	@SuppressWarnings("unchecked")
	public Parameter(T value) {
		this(value, (Class<? super T>)(value != null ? value.getClass() : Object.class));
	}
	
	/**
	 * Creates an instance based on a given value and a type.
	 * This has to be used when the parameter can be null or
	 * the type of the parameter is a subclass or interface.
	 * @param value the value
	 * @param type the parameter type
	 */
	public Parameter(T value, Class<? super T> type) {
		this.value = value;
		this.type = type;
	}

	/**
	 * Returns the type.
	 * Must not be null.
	 * @return the type
	 */
	public Class<? super T> getType() {
		return type;
	}

	/**
	 * Returns the value.
	 * May be null.
	 * @return the value
	 */
	public T getValue() {
		return value;
	}
	
}
