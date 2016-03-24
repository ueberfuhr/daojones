package de.ars.daojones.runtime.test.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A set of connection configurations for a test method or a test class. Use
 * this annotation when multiple application contexts or multiple configuration
 * files have to be available.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.METHOD, ElementType.TYPE } )
@Inherited
public @interface Configs {

  /**
   * Returns the configurations.
   * 
   * @return the configurations
   */
  Config[] value();

}
