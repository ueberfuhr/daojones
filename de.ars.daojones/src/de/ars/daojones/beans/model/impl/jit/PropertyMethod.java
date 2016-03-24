package de.ars.daojones.beans.model.impl.jit;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.CtMethod;
import javassist.NotFoundException;

import de.ars.daojones.beans.model.IProperty;
import de.ars.daojones.beans.model.IPropertyMethod;
import de.ars.daojones.beans.model.Modifier;
import de.ars.daojones.beans.model.PropertyMethodType;

/**
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 */
public class PropertyMethod implements IPropertyMethod {

  private static final Logger logger = Logger.getLogger( PropertyMethod.class
      .getName() );
  private final CtMethod method;
  private final IProperty parent;
  private final PropertyMethodType type;

  /**
   * Creates an instance.
   * 
   * @param parent
   *          the parent {@link IProperty}
   * @param method
   *          the method
   * @param type
   *          the {@link PropertyMethodType}
   */
  public PropertyMethod( IProperty parent, CtMethod method,
      PropertyMethodType type ) {
    super();
    this.parent = parent;
    this.method = method;
    this.type = type;
  }

  /**
   * Returns the {@link CtMethod}.
   * 
   * @return the {@link CtMethod}
   */
  public CtMethod getCtMethod() {
    return this.method;
  }

  /**
   * @see de.ars.daojones.beans.model.IPropertyMethod#getProperty()
   */
  public IProperty getProperty() {
    return parent;
  }

  /**
   * @see de.ars.daojones.beans.model.IPropertyMethod#getType()
   */
  public PropertyMethodType getType() {
    return type;
  }

  /**
   * @see de.ars.daojones.beans.model.IModifierContainer#getModifiers()
   */
  public Set<Modifier> getModifiers() {
    return Utilities.toModifiers( method.getModifiers() );
  }

  /**
   * @see de.ars.daojones.beans.model.IExceptionTrigger#getExceptionTypes()
   */
  public List<String> getExceptionTypes() {
    try {
      return Utilities.toExceptionTypes( method.getExceptionTypes() );
    } catch ( NotFoundException e ) {
      logger.log( Level.SEVERE, "Unable to read exception types of method "
          + method + "!", e );
      return new LinkedList<String>();
    }
  }

}
