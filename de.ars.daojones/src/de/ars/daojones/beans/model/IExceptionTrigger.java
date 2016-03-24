package de.ars.daojones.beans.model;

import java.util.List;

/**
 * An interface for elements declaring and triggering exceptions.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IExceptionTrigger {

  /**
   * Returns the fully qualified type names of the declared exceptions in the
   * declaration order.
   * 
   * @return the declared type names or null, if no exception is declared
   */
  public List<String> getExceptionTypes();

}
