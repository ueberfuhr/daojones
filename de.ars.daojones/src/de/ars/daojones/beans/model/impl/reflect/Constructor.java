package de.ars.daojones.beans.model.impl.reflect;

import java.util.List;
import java.util.Set;

import de.ars.daojones.beans.model.IBean;
import de.ars.daojones.beans.model.IConstructor;
import de.ars.daojones.beans.model.Modifier;

/**
 * Implementation of {@link IConstructor} using Java reflection.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class Constructor implements IConstructor {

  private final IBean parent;
  private final java.lang.reflect.Constructor<?> c;

  /**
   * Creates an instance.
   * 
   * @param parent
   *          the parent bean
   * @param c
   *          the constructor
   */
  public Constructor( IBean parent, java.lang.reflect.Constructor<?> c ) {
    super();
    this.parent = parent;
    this.c = c;
  }

  /**
   * @see de.ars.daojones.beans.model.IConstructor#getBean()
   */
  public IBean getBean() {
    return this.parent;
  }

  /**
   * @see de.ars.daojones.beans.model.IConstructor#getParameterTypes()
   */
  public String[] getParameterTypes() {
    final Class<?>[] params = this.c.getParameterTypes();
    final String[] result = new String[params.length];
    for ( int i = 0; i < params.length; i++ ) {
      result[i] = params[i].getName();
    }
    return result;
  }

  /**
   * @see de.ars.daojones.beans.model.IModifierContainer#getModifiers()
   */
  public Set<Modifier> getModifiers() {
    return Utilities.toModifiers( c.getModifiers() );
  }

  /**
   * @see de.ars.daojones.beans.model.IExceptionTrigger#getExceptionTypes()
   */
  public List<String> getExceptionTypes() {
    return Utilities.toExceptionTypes( c.getExceptionTypes() );
  }

}
