package de.ars.daojones.runtime.beans.fields;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares an implementation class of {@link Converter} to be a global
 * converter for all fields with a given type.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
@Target( ElementType.TYPE )
@Retention( RetentionPolicy.RUNTIME )
public @interface Converts {

  /*
   * TODO extend documentation with Person example
   */

  /**
   * The type that the converter converts.
   * 
   * @return the type that the converter converts
   */
  Class<?> value();

}
