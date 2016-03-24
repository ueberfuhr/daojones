package de.ars.daojones.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that gives information about the transformation of the database
 * value into an application value.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@Target( ElementType.METHOD )
@Retention( RetentionPolicy.RUNTIME )
@Inherited
public @interface Transform {

  /**
   * Returns a transformer class handling the transformation.
   * 
   * @return the transformer class
   */
  Class<? extends Transformer> value();

}
