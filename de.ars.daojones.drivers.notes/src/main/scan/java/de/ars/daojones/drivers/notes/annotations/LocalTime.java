package de.ars.daojones.drivers.notes.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;

/**
 * Annotate a field of type {@link Date} to indicate that this field provides a
 * time without any time zone. This is read as local time that uses the current
 * time zone of the application.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2017
 * @since 2.0
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD } )
public @interface LocalTime {

}
