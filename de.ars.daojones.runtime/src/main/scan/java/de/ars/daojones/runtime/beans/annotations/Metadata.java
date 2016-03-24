package de.ars.daojones.runtime.beans.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation declares multiple meta properties to a field mapping.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
@Target( { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD } )
@Retention( RetentionPolicy.RUNTIME )
@Inherited
public @interface Metadata {

  /**
   * Returns the meta properties.
   * 
   * @return the meta properties
   */
  Property[] value();

}
