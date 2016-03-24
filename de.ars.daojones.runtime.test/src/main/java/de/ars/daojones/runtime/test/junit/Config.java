package de.ars.daojones.runtime.test.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.ars.daojones.internal.runtime.test.Constants;

/**
 * A bean or connection configuration with a single configuration file and a
 * single application context for a test method or a test class.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.METHOD, ElementType.TYPE } )
@Inherited
public @interface Config {

  /**
   * Returns the id of the application.
   * 
   * @return the id of the application
   */
  String application() default Constants.DEFAULT_APPLICATION;

  /**
   * Returns the path to the configuration file that must be available in the
   * classpath of the test class. The path is relative to the test class. If you
   * want to address a global file in the root of the classpath, set a
   * <tt>&quot;/&quot;</tt> in front of the path.
   * 
   * @return the path to the configuration file that must be available in the
   *         class path of the test class
   */
  String value();

  /**
   * The type of configuration that is part of this file.
   * 
   * @return the configuration type
   */
  ConfigType type() default ConfigType.CONNECTIONS;

}
