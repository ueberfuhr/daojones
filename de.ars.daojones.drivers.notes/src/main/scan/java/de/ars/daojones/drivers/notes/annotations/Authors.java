package de.ars.daojones.drivers.notes.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.ars.daojones.drivers.notes.types.User;

/**
 * Annotate a field of type {@link User} to indicate that this field should be
 * treated as an AUTHORS field.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD } )
public @interface Authors {

}
