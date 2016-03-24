package de.ars.daojones.runtime.beans.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation binds a Java element to an existing field binding within the
 * class. You can annotate the same elements as with {@link Field}.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 * @see Field
 */
@Target( { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD } )
@Retention( RetentionPolicy.RUNTIME )
@Inherited
public @interface FieldRef {

  /**
   * Returns the id of the referenced field.
   * 
   * @return the id of the field
   * @see Field#id()
   */
  public String value();

}
