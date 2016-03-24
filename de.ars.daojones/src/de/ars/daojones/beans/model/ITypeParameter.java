package de.ars.daojones.beans.model;

/**
 * A generic type parameter.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface ITypeParameter {

  /**
   * Returns the name of the type.
   * 
   * @return the name
   */
  public String getName();

  /**
   * Returns the qualified name of the superclass or the name of another
   * {@link ITypeParameter}. This contains TypeParameters too.
   * 
   * @return the qualified name of the superclass
   */
  public String getSupertype();

}
