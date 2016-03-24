package de.ars.daojones.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the kind of accessing the database to read or write the property.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@Target( { ElementType.METHOD, ElementType.TYPE } )
@Retention( RetentionPolicy.RUNTIME )
public @interface AccessStrategy {

  /**
   * Returns the kind of accessing the database. The default value is
   * {@link StrategyPolicy#LAZY}.
   * 
   * @return the kind of accessing the database
   */
  public StrategyPolicy value();

}
