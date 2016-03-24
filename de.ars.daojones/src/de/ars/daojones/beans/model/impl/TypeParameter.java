package de.ars.daojones.beans.model.impl;

import de.ars.daojones.beans.model.ITypeParameter;

/**
 * Default implementation of {@link ITypeParameter}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class TypeParameter implements ITypeParameter {

  private String name;
  private String supertype;

  /**
   * @see de.ars.daojones.beans.model.ITypeParameter#getName()
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   * 
   * @param name
   * @see de.ars.daojones.beans.model.ITypeParameter#getName()
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * @see de.ars.daojones.beans.model.ITypeParameter#getSupertype()
   */
  public String getSupertype() {
    return supertype;
  }

  /**
   * Sets the supertype.
   * 
   * @param supertype
   * @see de.ars.daojones.beans.model.ITypeParameter#getSupertype()
   */
  public void setSupertype( String supertype ) {
    this.supertype = supertype;
  }

}
