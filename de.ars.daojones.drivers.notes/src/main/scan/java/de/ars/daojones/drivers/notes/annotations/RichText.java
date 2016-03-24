package de.ars.daojones.drivers.notes.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotations a field that is stored as a rich text item. Allowed field types
 * are:
 * <ul>
 * <li><tt>java.lang.String</tt></li>
 * <li>All primitive types and their wrapper classes</li>
 * <li>Enumerations</li>
 * <li>Arrays of the above</li>
 * <li><tt>de.ars.daojones.runtime.beans.identification.Identificator</tt>
 * (<i>Document links</i>!)</li>
 * </ul>
 * 
 * Resources (<tt>de.ars.daojones.runtime.beans.fields.Resource</tt>) are stored
 * into rich text items by default, so this annotation is not necessary in such
 * cases.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD } )
public @interface RichText {

}
