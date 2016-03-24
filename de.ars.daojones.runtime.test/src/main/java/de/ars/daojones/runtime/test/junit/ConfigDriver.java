package de.ars.daojones.runtime.test.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.ars.daojones.internal.runtime.test.DefaultTestConnectionFactoryModel;

/**
 * Use this annotation to declare the usage of a driver. This will configure the
 * default connection, that is initialized when no other connection is
 * configured using a connection configuration file (e.g. when using annotation
 * based test model configuration).
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.METHOD, ElementType.TYPE } )
@Inherited
public @interface ConfigDriver {

  /**
   * Returns the driver id of the default connection.
   * 
   * @return the driver id of the default connection
   */
  String value() default DefaultTestConnectionFactoryModel.ID;

}
