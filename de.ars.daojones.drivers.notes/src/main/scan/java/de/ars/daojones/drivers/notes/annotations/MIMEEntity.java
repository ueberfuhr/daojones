package de.ars.daojones.drivers.notes.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stores a resource as MIME Entity.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD } )
public @interface MIMEEntity {

  /**
   * The type of entity that defines how to handle the entity. Default value is
   * {@link MIMEEntityType#ATTACHMENT}.
   * 
   * @return the type of entity that defines how to handle the entity.
   */
  MIMEEntityType type() default MIMEEntityType.ATTACHMENT;

}
