package de.ars.daojones.beans.model.impl.jit;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;
import de.ars.daojones.beans.model.IBean;
import de.ars.daojones.beans.model.IConstructor;
import de.ars.daojones.beans.model.Modifier;

/**
 * JIT implementation of {@link IConstructor}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class Constructor implements IConstructor {

  private static final Logger logger = Logger.getLogger( Constructor.class
      .getName() );
  private final IBean bean;
  private final CtConstructor constructor;

  /**
   * Creates an instance.
   * 
   * @param bean
   *          the parent bean
   * @param constructor
   *          the constructor
   */
  public Constructor( IBean bean, CtConstructor constructor ) {
    super();
    this.bean = bean;
    this.constructor = constructor;
  }

  /**
   * Returns the constructor.
   * 
   * @return the constructor
   */
  public CtConstructor getCtConstructor() {
    return constructor;
  }

  /**
   * @see de.ars.daojones.beans.model.IConstructor#getBean()
   */
  public IBean getBean() {
    return bean;
  }

  /**
   * @see de.ars.daojones.beans.model.IConstructor#getParameterTypes()
   */
  public String[] getParameterTypes() {
    try {
      final CtClass[] params = constructor.getParameterTypes();
      final String[] result = new String[params.length];
      for ( int i = 0; i < params.length; i++ ) {
        result[i] = params[i].getName();
      }
      return result;
    } catch ( NotFoundException e ) {
      logger.log( Level.SEVERE,
          "Unable to read parameter types of constructor " + constructor + "!",
          e );
      return new String[0];
    }
  }

  /**
   * @see de.ars.daojones.beans.model.IModifierContainer#getModifiers()
   */
  public Set<Modifier> getModifiers() {
    return Utilities.toModifiers( constructor.getModifiers() );
  }

  /**
   * @see de.ars.daojones.beans.model.IExceptionTrigger#getExceptionTypes()
   */
  public List<String> getExceptionTypes() {
    try {
      return Utilities.toExceptionTypes( constructor.getExceptionTypes() );
    } catch ( NotFoundException e ) {
      logger.log( Level.SEVERE,
          "Unable to read exception types of constructor " + constructor + "!",
          e );
      return new LinkedList<String>();
    }
  }

}
