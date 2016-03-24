package de.ars.daojones.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.ars.daojones.connections.ConnectionMetaData;

/**
 * Sets the name of the data source where the Bean can be read from.
 * @see ConnectionMetaData
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
public @interface DataSource {

	/**
	 * Returns the name of the table.
	 * @return the name of the table
	 */
	public String value();
	
	/**
	 * The type of the datasource that specifies where to read and
	 * if to be able to update, delete or create entries.
	 * @return the type of the datasource
	 */
	public DataSourceType type() default DataSourceType.TABLE;
	
}
