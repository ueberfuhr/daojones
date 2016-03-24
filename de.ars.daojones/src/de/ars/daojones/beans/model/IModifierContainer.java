package de.ars.daojones.beans.model;

import java.util.Set;

/**
 * An element that is descripted by modifiers. In the code generation context,
 * this can be methods and types.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IModifierContainer {

  /**
   * Returns the modifiers, especially the visibility type.
   * 
   * @return the modifiers
   * @see Modifier
   */
  public Set<Modifier> getModifiers();

}
