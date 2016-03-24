package de.ars.daojones.runtime.configuration.context;

import java.io.Serializable;

/**
 * A common model element to configure the context.
 * 
 * @param <Id>
 *          the type of id
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public interface ConfigurationModel<Id extends Serializable> extends Serializable {

  /**
   * Returns the id. Must not be <tt>null</tt>.
   * 
   * @return the id
   */
  public abstract Id getId();

  /**
   * Returns the human readable name.
   * 
   * @return the human readable name
   */
  public abstract String getName();

  /**
   * Returns the human readable description.
   * 
   * @return the human readable description
   */
  public abstract String getDescription();

}