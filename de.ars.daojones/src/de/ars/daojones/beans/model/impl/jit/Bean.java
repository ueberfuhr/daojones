package de.ars.daojones.beans.model.impl.jit;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;
import de.ars.daojones.annotations.Abstract;
import de.ars.daojones.annotations.StrategyPolicy;
import de.ars.daojones.beans.model.IBean;
import de.ars.daojones.beans.model.IConstructor;
import de.ars.daojones.beans.model.IProperty;
import de.ars.daojones.beans.model.ITypeParameter;
import de.ars.daojones.beans.model.Modifier;

/**
 * JIT implementation of {@link IBean}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class Bean implements IBean {

  private static final Logger logger = Logger.getLogger( Bean.class.getName() );
  private final CtClass c;
  private final de.ars.daojones.beans.model.impl.reflect.Bean delegate;

  /**
   * Creates an instance.
   * 
   * @param c
   *          the ctClass
   * @param cc
   *          the reflection class
   */
  public Bean( CtClass c, Class<?> cc ) {
    super();
    this.c = c;
    this.delegate = new de.ars.daojones.beans.model.impl.reflect.Bean( cc );
  }

  /**
   * Returns the class.
   * 
   * @return the class
   */
  public CtClass getCtClass() {
    return this.c;
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#getConstructors()
   */
  public Collection<IConstructor> getConstructors() {
    final Collection<IConstructor> result = new HashSet<IConstructor>();
    for ( CtConstructor constructor : c.getConstructors() ) {
      final Constructor con = new Constructor( this, constructor );
      result.add( con );
    }
    return result;
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#getName()
   */
  public String getName() {
    return c.getSimpleName();
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#getPackageName()
   */
  public String getPackageName() {
    return c.getPackageName();
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#getProperties()
   */
  public Collection<IProperty> getProperties() {
    final Collection<IProperty> delegateProps = delegate.getProperties();
    final Collection<IProperty> result = new LinkedList<IProperty>();
    for ( IProperty delegate : delegateProps ) {
      result.add( new Property( this, delegate ) );
    }
    return result;
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#getTypeParameters()
   */
  public Collection<ITypeParameter> getTypeParameters() {
    // type parameters are typically not available at runtime
    return delegate.getTypeParameters();
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#isAbstract()
   */
  public boolean isAbstract() {
    try {
      return null != c.getAnnotation( Abstract.class );
    } catch ( Throwable t ) {
      logger.log( Level.WARNING, "Unable to analyze bean!", t );
      return false;
    }
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#isInterface()
   */
  public boolean isInterface() {
    return c.isInterface();
  }

  /**
   * @see de.ars.daojones.beans.model.IBean#isMethodImplemented(java.lang.String,
   *      java.lang.String[])
   */
  public boolean isMethodImplemented( String name, String... parameterTypes ) {
    return isImplemented( c, name, parameterTypes );
  }

  private static boolean isImplemented( CtClass c, String name,
      String... parameterTypes ) {
    try {
      if ( null != c && !c.isInterface()
          && !Object.class.getName().equals( c.getName() ) ) {
        for ( CtMethod m : c.getDeclaredMethods() ) {
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
    } catch ( NotFoundException e ) {
      logger.log( Level.WARNING, "Unable to analyze bean!", e );
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
    return delegate.getStrategy();
  }

}
