package de.ars.daojones.annotations.model;

import java.io.Serializable;

import de.ars.daojones.annotations.DataSource;
import de.ars.daojones.annotations.DataSourceType;

/**
 * A class containing the informations of the
 * {@link de.ars.daojones.annotations.DataSource} annotation. This is used for
 * the driver implementation because annotations cannot be instantiated during
 * runtime.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class DataSourceInfo implements Serializable {

	private static final long serialVersionUID = 2412565415612886442L;

	private String value;
	private DataSourceType type;

	/**
	 * Creates an empty instance.
	 */
	public DataSourceInfo() {
		super();
	}

	/**
	 * Creates an instance based on a given {@link DataSource} annotation.
	 * 
	 * @param dataSource
	 *            the {@link DataSource} annotation
	 */
	public DataSourceInfo(DataSource dataSource) {
		this(dataSource.type(), dataSource.value());
	}

	/**
	 * Creates an instance based on a given {@link DataSourceType} and name.
	 * 
	 * @param type
	 *            the {@link DataSourceType}
	 * @param value
	 *            the name of the datasource
	 */
	public DataSourceInfo(DataSourceType type, String value) {
		this();
		this.type = type;
		this.value = value;
	}

	/**
	 * Returns the name of the datasource.
	 * 
	 * @return the name of the datasource
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the name of the datasource.
	 * 
	 * @param value
	 *            the name of the datasource
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns the type of the datasource.
	 * 
	 * @return the type of the datasource
	 */
	public DataSourceType getType() {
		return type;
	}

	/**
	 * Sets the type of the datasource.
	 * 
	 * @param type
	 *            the type of the datasource
	 */
	public void setType(DataSourceType type) {
		this.type = type;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataSourceInfo other = (DataSourceInfo) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getValue() + " (" + getType() + ")";
	}

}
