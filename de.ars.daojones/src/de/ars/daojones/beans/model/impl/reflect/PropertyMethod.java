package de.ars.daojones.beans.model.impl.reflect;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import de.ars.daojones.beans.model.IProperty;
import de.ars.daojones.beans.model.IPropertyMethod;
import de.ars.daojones.beans.model.Modifier;
import de.ars.daojones.beans.model.PropertyMethodType;

/**
 * Implementation of {@link IPropertyMethod} using Java reflection.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class PropertyMethod implements IPropertyMethod {

  private final IProperty parent;
  private final PropertyMethodType type;
  private final Method method;

  /**
   * Creates an instance.
   * 
   * @param parent
   *          the parent property
   * @param type
   *          the {@link PropertyMethodType}
   * @param method
   *          the method
   */
  public PropertyMethod( IProperty parent, PropertyMethodType type,
      Method method ) {
    super();
    this.parent = parent;
    this.type = type;
    this.method = method;
  }

  /**
   * @see de.ars.daojones.beans.model.IPropertyMethod#getProperty()
   */
  public IProperty getProperty() {
    return this.parent;
  }

  /**
   * @see de.ars.daojones.beans.model.IPropertyMethod#getType()
   */
  public PropertyMethodType getType() {
    return this.type;
  }

  public Set<Modifier> getModifiers() {
    return Utilities.toModifiers( method.getModifiers() );
  }

  public List<String> getExceptionTypes() {
    return Utilities.toExceptionTypes( method.getExceptionTypes() );
  }

}
