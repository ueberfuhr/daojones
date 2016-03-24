package de.ars.daojones.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to tell the DaoJones runtime
 * where the information about inheritation can be found.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inheritations {

	/**
	 * The name of the inheritations file.
	 * @return the name of the inheritations file
	 */
	String value();
	
}
