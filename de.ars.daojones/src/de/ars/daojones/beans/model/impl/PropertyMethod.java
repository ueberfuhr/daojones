package de.ars.daojones.beans.model.impl;

import java.util.List;
import java.util.Set;

import de.ars.daojones.beans.model.IExceptionTrigger;
import de.ars.daojones.beans.model.IPropertyMethod;
import de.ars.daojones.beans.model.Modifier;
import de.ars.daojones.beans.model.PropertyMethodType;

/**
 * Default implementation for {@link IPropertyMethod}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class PropertyMethod implements IPropertyMethod {

  private Set<Modifier> modifiers;
  private PropertyMethodType type;
  private Property property;

  private List<String> exceptionTypes;

  /**
   * @see de.ars.daojones.beans.model.IPropertyMethod#getType()
   */
  public PropertyMethodType getType() {
    return type;
  }

  /**
   * Sets the type of the property method.
   * 
   * @param type
   *          the type
   */
  public void setType( PropertyMethodType type ) {
    this.type = type;
  }

  /**
   * @see de.ars.daojones.beans.model.IPropertyMethod#getProperty()
   */
  public Property getProperty() {
    return property;
  }

  /**
   * Sets the property.
   * 
   * @param property
   *          the property
   */
  public void setProperty( Property property ) {
    this.property = property;
  }

  /**
   * Returns the modifiers.
   * 
   * @return the modifiers
   * @see IPropertyMethod#getModifiers()
   * @see Modifier
   */
  public Set<Modifier> getModifiers() {
    return modifiers;
  }

  /**
   * Sets the modifiers.
   * 
   * @param modifiers
   * @see Modifier
   */
  public void setModifiers( Set<Modifier> modifiers ) {
    this.modifiers = modifiers;
  }

  /**
   * Returns the exception types.
   * 
   * @return the exception types
   * @see IExceptionTrigger#getExceptionTypes()
   */
  public List<String> getExceptionTypes() {
    return exceptionTypes;
  }

  /**
   * Sets the exception types.
   * 
   * @param exceptionTypes
   *          the exception types
   * @see IExceptionTrigger#getExceptionTypes()
   */
  public void setExceptionTypes( List<String> exceptionTypes ) {
    this.exceptionTypes = exceptionTypes;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ( ( modifiers == null ) ? 0 : modifiers.hashCode() );
    return result;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object obj ) {
    if ( this == obj )
      return true;
    if ( obj == null )
      return false;
    if ( getClass() != obj.getClass() )
      return false;
    PropertyMethod other = ( PropertyMethod ) obj;
    if ( modifiers == null ) {
      if ( other.modifiers != null )
        return false;
    } else if ( !modifiers.equals( other.modifiers ) )
      return false;
    return true;
  }

}
