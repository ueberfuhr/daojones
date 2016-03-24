package de.ars.daojones.runtime.beans.identification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.ars.daojones.runtime.connections.Connection;

/**
 * This annotation declares a {@link BeanIdentificator} that identifies the
 * corresponding entry within the database. This is used during the invocation
 * of {@link Connection#update(Object...)} and
 * {@link Connection#delete(Object...)}. By default, the
 * {@link DefaultBeanIdentificator} is used to place the necessary information
 * into an instance field of the bean. With this annotation, it is possible to
 * switch this behaviour, e.g. to use bytecode weaving or an external registry
 * instead.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
@Retention( RetentionPolicy.RUNTIME )
@Inherited
@Target( ElementType.TYPE )
public @interface IdentifiedBy {

  /**
   * The implementation type of a {@link BeanIdentificator}.
   * 
   * @return the implementation type of a {@link BeanIdentificator}
   */
  Class<? extends BeanIdentificator> value();

}
