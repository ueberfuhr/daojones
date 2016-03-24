package de.ars.daojones.beans.model;

/**
 * This class provides information about a constructor of a bean. The
 * constructor must be re-declared when implementing a bean.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IConstructor extends IModelElement, IModifierContainer,
    IExceptionTrigger {

  /**
   * Returns the bean that this constructor creates.
   * 
   * @return the bean
   */
  public IBean getBean();

  /**
   * Returns the fully qualified names of the parameters. In case of the default
   * constructor, this method may return an empty array or null.
   * 
   * @return the fully qualified names of the parameters
   */
  public String[] getParameterTypes();

}
