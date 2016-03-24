package de.ars.daojones.runtime.configuration.beans;

import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.beans.Invocable;

/**
 * A field mapped element that is part of an invocable (a constructor or a
 * method).
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public abstract class InvocableElement extends DatabaseFieldMappedElement {

  private static final long serialVersionUID = 1L;

  // @XmlTransient
  private transient Invocable invocable;

  /**
   * Returns the invocable.
   * 
   * @return the invocable
   */
  public Invocable getInvocable() {
    return invocable;
  }

  /**
   * Sets the invocable.
   * 
   * @param invocable
   *          the invocable
   */
  public void setInvocable( final Invocable invocable ) {
    this.invocable = invocable;
  }

}
