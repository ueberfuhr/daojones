package de.ars.daojones.beans.model.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import de.ars.daojones.beans.model.IBean;
import de.ars.daojones.beans.model.IConstructor;
import de.ars.daojones.beans.model.IExceptionTrigger;
import de.ars.daojones.beans.model.IModifierContainer;
import de.ars.daojones.beans.model.Modifier;

/**
 * Default implementation for {@link IConstructor}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class Constructor implements IConstructor {

  private IBean bean;
  private String[] parameterTypes;
  private Set<Modifier> modifiers;
  private List<String> exceptionTypes;

  /**
   * Returns the bean.
   * 
   * @return the bean
   * @see IConstructor#getBean()
   */
//TODO Java6-Migration
  //  @Override
  public IBean getBean() {
    return bean;
  }

  /**
   * Sets the bean.
   * 
   * @param bean
   *          the bean
   * @see IConstructor#getBean()
   */
  public void setBean( IBean bean ) {
    this.bean = bean;
  }

  /**
   * Returns the parameters.
   * 
   * @return the parameters
   * @see IConstructor#getParameterTypes()
   */
//TODO Java6-Migration
  //  @Override
  public String[] getParameterTypes() {
    return parameterTypes;
  }

  /**
   * Sets the parameters.
   * 
   * @param parameterTypes
   *          the parameters
   * @see IConstructor#getParameterTypes()
   */
  public void setParameterTypes( String[] parameterTypes ) {
    this.parameterTypes = parameterTypes;
  }

  /**
   * Returns the modifiers.
   * 
   * @return the modifiers
   * @see IModifierContainer#getModifiers()
   */
//TODO Java6-Migration
  //  @Override
  public Set<Modifier> getModifiers() {
    return modifiers;
  }

  /**
   * Sets the modifiers.
   * 
   * @param modifiers
   *          the modifiers
   * @see IModifierContainer#getModifiers()
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
    result = prime * result + ( ( bean == null ) ? 0 : bean.hashCode() );
    result = prime * result + Arrays.hashCode( parameterTypes );
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
    Constructor other = ( Constructor ) obj;
    if ( bean == null ) {
      if ( other.bean != null )
        return false;
    } else if ( !bean.equals( other.bean ) )
      return false;
    if ( !Arrays.equals( parameterTypes, other.parameterTypes ) )
      return false;
    return true;
  }

}
