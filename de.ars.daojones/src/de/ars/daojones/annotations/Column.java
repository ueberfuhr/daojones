package de.ars.daojones.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the column where the property value is stored into.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@Target( ElementType.METHOD )
@Retention( RetentionPolicy.RUNTIME )
@Inherited
public @interface Column {

  /**
   * Returns the column where the property value is stored into.
   * 
   * @return the column where the property value is stored into
   */
  public String value();

  /**
   * Returns a class that selects the column name.
   * 
   * @return the class that selects the column name
   */
  public Class<? extends ColumnSelector> selector() default DefaultColumnSelector.class;

}
