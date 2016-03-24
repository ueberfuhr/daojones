package de.ars.daojones.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a DaoJones Bean as an abstract bean that cannot be instantiated.
 * The generated Bean implementation then is an abstract class. 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Abstract {

}
