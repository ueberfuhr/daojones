package de.ars.daojones.runtime.beans.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.ars.daojones.runtime.beans.identification.Identificator;

/**
 * A marker annotation for an instance field that contains the identificator.
 * This instance field must be of type {@link Identificator} or one of it's
 * super types, at least {@link Object}.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface Id {
}