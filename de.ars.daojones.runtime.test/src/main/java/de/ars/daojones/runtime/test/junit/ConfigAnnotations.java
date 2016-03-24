package de.ars.daojones.runtime.test.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.ars.daojones.internal.runtime.test.Constants;

/**
 * A bean configuration that is read by scanning for annotations with a single
 * application context for a test method or a test class.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.METHOD, ElementType.TYPE } )
@Inherited
public @interface ConfigAnnotations {

  /**
   * Returns the id of the application.
   * 
   * @return the id of the application
   */
  String application() default Constants.DEFAULT_APPLICATION;

}
