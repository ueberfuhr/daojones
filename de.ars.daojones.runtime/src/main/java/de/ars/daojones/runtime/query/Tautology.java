package de.ars.daojones.runtime.query;

import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.context.ApplicationContext;

/**
 * A criterion that always returns <tt>true</tt>. This is only for combining
 * purposes.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 1.0
 */
public class Tautology extends AbstractSearchCriterion {

  private static final long serialVersionUID = 1L;

  @Override
  public int hashCode() {
    return 1;
  }

  @Override
  public boolean equals( final Object obj ) {
    if ( this == obj ) {
      return true;
    }
    if ( obj == null ) {
      return false;
    }
    return obj instanceof Tautology;
  }

  @Override
  public String toString() {
    return " TRUE ";
  }

  @Override
  public boolean matches( final ApplicationContext ctx, final Object bean ) throws ConfigurationException {
    return true;
  }

}
