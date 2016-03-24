package de.ars.daojones.runtime.beans.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a field to be <tt>computed</tt>. Computed field values
 * are calculated automatically by the database before storing the database
 * entry. Bean values that are markes as <tt>computed</tt> will be reinjected
 * after storing or deleting a bean.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
@Target( { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD } )
@Retention( RetentionPolicy.RUNTIME )
@Inherited
public @interface Computed {
}
