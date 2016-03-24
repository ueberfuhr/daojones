package de.ars.daojones.runtime.test.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.ars.daojones.internal.runtime.test.Constants;
import de.ars.daojones.runtime.configuration.context.ApplicationModel;
import de.ars.daojones.runtime.configuration.context.ApplicationModelManager;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.context.CacheFactoryModelManager;
import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModelManager;
import de.ars.daojones.runtime.configuration.context.ConnectionModelManager;
import de.ars.daojones.runtime.configuration.context.DaoJonesContextConfiguration;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.context.Application;
import de.ars.daojones.runtime.context.ApplicationContext;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.context.InjectionEngine;

/**
 * An annotation that marks instance variables and test method parameters to get
 * DaoJones artifacts injected. Currently, the following types are supported:
 * <ul>
 * <li>{@link DaoJonesContext}</li>
 * <li>{@link DaoJonesContextConfiguration}</li>
 * <li>{@link ConnectionProvider}</li>
 * <li>{@link ApplicationModelManager}</li>
 * <li>{@link ApplicationModel}</li>
 * <li>{@link Application}</li>
 * <li>{@link ApplicationContext}</li>
 * <li>{@link BeanModelManager}</li>
 * <li>{@link CacheFactoryModelManager}</li>
 * <li>{@link ConnectionFactoryModelManager}</li>
 * <li>{@link ConnectionModelManager}</li>
 * <li>{@link InjectionEngine}</li>
 * </ul>
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 1.2
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.FIELD, ElementType.PARAMETER } )
public @interface Inject {

  /**
   * Returns the id of the application.
   * 
   * @return the id of the application
   */
  String application() default Constants.DEFAULT_APPLICATION;

}
