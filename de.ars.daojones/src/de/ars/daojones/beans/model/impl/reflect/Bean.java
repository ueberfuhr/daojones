package de.ars.daojones.beans.model.impl.reflect;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import de.ars.daojones.annotations.Abstract;
import de.ars.daojones.annotations.AccessStrategy;
import de.ars.daojones.annotations.StrategyPolicy;
import de.ars.daojones.beans.model.IBean;
import de.ars.daojones.beans.model.IConstructor;
import de.ars.daojones.beans.model.IProperty;
import de.ars.daojones.beans.model.ITypeParameter;
import de.ars.daojones.beans.model.Modifier;

/**
 * Implementation of {@link IBean} using Java reflection.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class Bean implements IBean {

  private final Class<?> c;

  /**
   * Creates an instance.
   * 
   * @param c
   *          the class object
   */
  public Bean( Class<?> c ) {
    super();
    this.c = c;
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#getConstructors()
   */
  public Collection<IConstructor> getConstructors() {
    final Collection<IConstructor> result = new HashSet<IConstructor>();
    for ( java.lang.reflect.Constructor<?> constructor : c.getConstructors() ) {
      final Constructor con = new Constructor( this, constructor );
      result.add( con );
    }
    return result;
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#getName()
   */
  public String getName() {
    return this.c.getSimpleName();
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#getPackageName()
   */
  public String getPackageName() {
    return this.c.getPackage().getName();
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#getProperties()
   */
  public Collection<IProperty> getProperties() {
    final Collection<PropertyDescriptor> properties = Utilities
        .getPersistentFields( c );
    final Collection<IProperty> result = new HashSet<IProperty>();
    for ( PropertyDescriptor p : properties ) {
      result.add( new Property( this, p ) );
    }
    return result;
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#getTypeParameters()
   */
  public Collection<ITypeParameter> getTypeParameters() {
    final Collection<ITypeParameter> result = new LinkedList<ITypeParameter>();
    for ( TypeVariable<?> t : c.getTypeParameters() ) {
      result.add( new TypeParameter( t ) );
    }
    return result;
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#isAbstract()
   */
  public boolean isAbstract() {
    return null != this.c.getAnnotation( Abstract.class );
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#isInterface()
   */
  public boolean isInterface() {
    return this.c.isInterface();
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#isMethodImplemented(java.lang.String,
   *      java.lang.String[])
   */
  public boolean isMethodImplemented( String name, String... parameterTypes ) {
    return isImplemented( c, name, parameterTypes );
  }

  private static boolean isImplemented( Class<?> c, String name,
      String... parameterTypes ) {
    if ( null != c && !c.isInterface()
        && !Object.class.getName().equals( c.getName() ) ) {
      for ( Method m : c.getDeclaredMethods() ) {
        if ( name.equals( m.getName() )
            && Arrays.equals( parameterTypes, Utilities.toTypenames( m
                .getParameterTypes() ) ) ) {
          if ( !java.lang.reflect.Modifier.isAbstract( m.getModifiers() ) ) {
            return true;
          }
          break;
        }
      }
      return isImplemented( c.getSuperclass(), name, parameterTypes );
    }
    return false;
  }

  /**
   * @see de.ars.daojones.beans.model.IModifierContainer#getModifiers()
   */
  public Set<Modifier> getModifiers() {
    final Set<Modifier> result = Utilities.toModifiers( c.getModifiers() );
    result.remove( Modifier.ABSTRACT );
    if ( isAbstract() )
      result.add( Modifier.ABSTRACT );
    return result;
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#getStrategy()
   */
  public StrategyPolicy getStrategy() {
    final AccessStrategy strategy = c.getAnnotation( AccessStrategy.class );
    return null != strategy ? strategy.value() : null;
  }

}
