package de.ars.daojones.runtime.beans.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation declares a meta property to a field mapping.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @see Metadata
 */
@Target( { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD } )
@Retention( RetentionPolicy.RUNTIME )
@Inherited
public @interface Property {

  /**
   * Returns the name of the property.
   * 
   * @return the name of the property
   */
  String name();

  /**
   * Returns the value of the property. This is optional for meta properties
   * that are used to mark field mappings without any usage of a special value.
   * 
   * @return the value of the property
   */
  String value() default "";

}
