package de.ars.daojones.beans.model;

/**
 * Information about a getter or setter for a property.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IPropertyMethod extends IModelElement, IModifierContainer,
    IExceptionTrigger {

  /**
   * Returns the property.
   * 
   * @return the property
   */
  public IProperty getProperty();

  /**
   * Returns the type of the method.
   * 
   * @return the type of the method
   */
  public PropertyMethodType getType();

}
