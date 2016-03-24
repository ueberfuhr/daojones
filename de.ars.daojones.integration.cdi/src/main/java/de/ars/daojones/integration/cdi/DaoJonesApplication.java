package de.ars.daojones.integration.cdi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Use this annotation to identify an application-dependent object that should
 * be injected.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
@Qualifier
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE } )
public @interface DaoJonesApplication {

  /**
   * The name of the DaoJones application.
   * 
   * @return the name of the DaoJones application
   */
  @Nonbinding
  String value();

}
