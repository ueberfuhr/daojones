package de.ars.daojones.runtime.beans.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping.DataSourceType;

/**
 * Sets the name of the data source where the Bean can be read from. This
 * annotation is not mandatory since the simple name of the class is the name of
 * the data source and the data source is a table.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 1.0
 */
@Retention( RetentionPolicy.RUNTIME )
@Inherited
@Target( ElementType.TYPE )
public @interface DataSource {

  /**
   * Returns the name of the data source. If not specified, the name of the data
   * source is the simple name of the annotated class.
   * 
   * @return the name of the data source
   */
  public String value() default "";

  /**
   * The type of the data source that specifies where to read and if to be able
   * to update, delete or create entries.
   * 
   * @return the type of the data source
   */
  public DataSourceType type() default DataSourceType.TABLE;

}
