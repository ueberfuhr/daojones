package de.ars.daojones.runtime;

/**
 * A transfer object containing information
 * about a method parameter.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T> the parameter type
 */
public class Field<T> {

	private String name;
	private Class<? super T> type;
	
	/**
	 * Creates an instance.
	 * @param name the name
	 * @param type the parameter type
	 */
	public Field(String name, Class<? super T> type) {
		this.name = name;
		this.type = type;
	}

	/**
	 * Returns the type.
	 * @return the type
	 */
	public Class<? super T> getType() {
		return type;
	}

	/**
	 * Returns the name.
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
}
