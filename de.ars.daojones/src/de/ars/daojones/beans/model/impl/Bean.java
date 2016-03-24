package de.ars.daojones.beans.model.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.ars.daojones.annotations.StrategyPolicy;
import de.ars.daojones.beans.model.IBean;
import de.ars.daojones.beans.model.IConstructor;
import de.ars.daojones.beans.model.IProperty;
import de.ars.daojones.beans.model.ITypeParameter;
import de.ars.daojones.beans.model.Modifier;

/**
 * Default implementation fpr {@link IBean}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class Bean implements IBean {

  private final Set<Method> implementedMethods = new HashSet<Method>();
  private String packageName;
  private String name;
  private Collection<IConstructor> constructors;
  private Collection<IProperty> properties;
  private boolean _abstract;
  private boolean _interface;
  private Set<Modifier> modifiers;
  private Collection<ITypeParameter> typeParameters;
  private StrategyPolicy strategy;

  private static class Method {
    private final String name;
    private final String[] parameterTypes;

    public Method( String name, String[] parameterTypes ) {
      super();
      this.name = name;
      this.parameterTypes = parameterTypes;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
      result = prime * result + Arrays.hashCode( parameterTypes );
      return result;
    }

    @Override
    public boolean equals( Object obj ) {
      if ( this == obj )
        return true;
      if ( obj == null )
        return false;
      if ( getClass() != obj.getClass() )
        return false;
      Method other = ( Method ) obj;
      if ( name == null ) {
        if ( other.name != null )
          return false;
      } else if ( !name.equals( other.name ) )
        return false;
      if ( !Arrays.equals( parameterTypes, other.parameterTypes ) )
        return false;
      return true;
    }

  }

  /**
   * Returns the package name.
   * 
   * @return the package name
   */
  // TODO Java6-Migration
  // @Override
  public String getPackageName() {
    return packageName;
  }

  /**
   * Sets the package name.
   * 
   * @param packageName
   *          the package name
   */
  public void setPackageName( String packageName ) {
    this.packageName = packageName;
  }

  /**
   * Returns the name.
   * 
   * @return the name
   */
  // TODO Java6-Migration
  // @Override
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   * 
   * @param name
   *          the name
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * Returns the type parameters.
   * 
   * @see de.ars.daojones.beans.model.IBean#getTypeParameters()
   */
  public Collection<ITypeParameter> getTypeParameters() {
    return typeParameters;
  }

  /**
   * Sets the type parameters.
   * 
   * @param typeParameters
   *          the type parameters
   * @see de.ars.daojones.beans.model.IBean#getTypeParameters()
   */
  public void setTypeParameters( Collection<ITypeParameter> typeParameters ) {
    this.typeParameters = typeParameters;
  }

  /**
   * Returns the constructors.
   * 
   * @see IBean#getConstructors()
   */
  // TODO Java6-Migration
  // @Override
  public Collection<IConstructor> getConstructors() {
    return constructors;
  }

  /**
   * Sets the constructors.
   * 
   * @param constructors
   *          the constructors
   * @see IBean#getConstructors()
   */
  public void setConstructors( Collection<IConstructor> constructors ) {
    this.constructors = constructors;
  }

  /**
   * Returns the properties.
   * 
   * @return the properties
   */
  // TODO Java6-Migration
  // @Override
  public Collection<IProperty> getProperties() {
    return properties;
  }

  /**
   * Sets the properties.
   * 
   * @param properties
   *          the properties
   */
  public void setProperties( Collection<IProperty> properties ) {
    this.properties = properties;
  }

  /**
   * Returns a flag indicating whether the bean class is an interface or not.
   * 
   * @return true, if the class is an interface.
   */
  public boolean isInterface() {
    return _interface;
  }

  /**
   * Sets a flag indicating whether the bean class is an interface or not.
   * 
   * @param _interface
   *          true, if the class is an interface.
   */
  public void setInterface( boolean _interface ) {
    this._interface = _interface;
  }

  /**
   * Returns true if the bean to be generated is abstract.
   * 
   * @return true if the bean to be generated is abstract
   */
  // TODO Java6-Migration
  // @Override
  public boolean isAbstract() {
    return _abstract;
  }

  /**
   * Sets true if the bean to be generated is abstract.
   * 
   * @param _abstract
   *          true if the bean to be generated is abstract
   */
  public void setAbstract( boolean _abstract ) {
    this._abstract = _abstract;
  }

  /**
   * Returns the modifiers of the class.
   * 
   * @return the modifiers of the class
   */
  // TODO Java6-Migration
  // @Override
  public Set<Modifier> getModifiers() {
    return modifiers;
  }

  /**
   * Sets the modifiers of the class.
   * 
   * @param modifiers
   *          the modifiers of the class
   */
  public void setModifiers( Set<Modifier> modifiers ) {
    this.modifiers = modifiers;
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#isMethodImplemented(java.lang.String,
   *      java.lang.String[])
   */
  public boolean isMethodImplemented( String name, String... parameterTypes ) {
    return this.implementedMethods
        .contains( new Method( name, parameterTypes ) );
  }

  /**
   * Sets a method as implemented.
   * 
   * @param implemented
   *          a flag indicating whether the method is implemented or not
   * @param name
   *          the name of the method
   * @param parameterTypes
   *          the parameter type names
   */
  public void setMethodImplemented( boolean implemented, String name,
      String... parameterTypes ) {
    final Method m = new Method( name, parameterTypes );
    if ( implemented ) {
      this.implementedMethods.add( m );
    } else {
      this.implementedMethods.remove( m );
    }
  }

  /**
   * Returns the access strategy.
   * 
   * @return the access strategy
   */
  public StrategyPolicy getStrategy() {
    return strategy;
  }

  /**
   * Sets the access strategy.
   * 
   * @param strategy
   *          the access strategy
   */
  public void setStrategy( StrategyPolicy strategy ) {
    this.strategy = strategy;
  }

}
