package de.ars.daojones.beans.model.impl.jit;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

/**
 * A helper class providing utility methods.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 */
public class Utilities {

  private static final Logger logger = Logger.getLogger( Utilities.class
      .getName() );

  private static final Map<de.ars.daojones.beans.model.Modifier, Integer> modifierValues = new HashMap<de.ars.daojones.beans.model.Modifier, Integer>() {
    private static final long serialVersionUID = 1L;
    {
      put( de.ars.daojones.beans.model.Modifier.ABSTRACT, Modifier.ABSTRACT );
      put( de.ars.daojones.beans.model.Modifier.FINAL, Modifier.FINAL );
      put( de.ars.daojones.beans.model.Modifier.NATIVE, Modifier.NATIVE );
      put( de.ars.daojones.beans.model.Modifier.PRIVATE, Modifier.PRIVATE );
      put( de.ars.daojones.beans.model.Modifier.PROTECTED, Modifier.PROTECTED );
      put( de.ars.daojones.beans.model.Modifier.PUBLIC, Modifier.PUBLIC );
      put( de.ars.daojones.beans.model.Modifier.STATIC, Modifier.STATIC );
      put( de.ars.daojones.beans.model.Modifier.STRICTFP, Modifier.STRICT );
      put( de.ars.daojones.beans.model.Modifier.SYNCHRONIZED,
          Modifier.SYNCHRONIZED );
      put( de.ars.daojones.beans.model.Modifier.TRANSIENT, Modifier.TRANSIENT );
      put( de.ars.daojones.beans.model.Modifier.VOLATILE, Modifier.VOLATILE );
    }
  };

  /**
   * Transforms the set of modifiers to an int value.
   * 
   * @param flags
   *          the set of modifiers
   * @return the int value
   */
  public static int toModifiers( Set<de.ars.daojones.beans.model.Modifier> flags ) {
    int result = 0;
    if ( null != flags ) {
      for ( de.ars.daojones.beans.model.Modifier flag : flags ) {
        result = result | modifierValues.get( flag );
      }
    }
    return result;
  }

  /**
   * Parses an int value into a set of modifiers.
   * 
   * @param flags
   *          the int value
   * @return the set of modifiers
   */
  public static Set<de.ars.daojones.beans.model.Modifier> toModifiers( int flags ) {
    final Set<de.ars.daojones.beans.model.Modifier> result = new HashSet<de.ars.daojones.beans.model.Modifier>();
    if ( ( flags & Modifier.ABSTRACT ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.ABSTRACT );
    if ( ( flags & Modifier.FINAL ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.FINAL );
    if ( ( flags & Modifier.NATIVE ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.NATIVE );
    if ( ( flags & Modifier.PRIVATE ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.PRIVATE );
    if ( ( flags & Modifier.PROTECTED ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.PROTECTED );
    if ( ( flags & Modifier.PUBLIC ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.PUBLIC );
    if ( ( flags & Modifier.STATIC ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.STATIC );
    if ( ( flags & Modifier.STRICT ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.STRICTFP );
    if ( ( flags & Modifier.SYNCHRONIZED ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.SYNCHRONIZED );
    if ( ( flags & Modifier.TRANSIENT ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.TRANSIENT );
    if ( ( flags & Modifier.VOLATILE ) > 0 )
      result.add( de.ars.daojones.beans.model.Modifier.VOLATILE );
    return result;
  }

  /**
   * Returns the list of exception type names
   * 
   * @param exceptionTypes
   *          the exception types
   * @return the list of exception type names
   */
  public static List<String> toExceptionTypes( CtClass[] exceptionTypes ) {
    final List<String> result = new LinkedList<String>();
    final Comparator<CtClass> inheritationsComparator = new Comparator<CtClass>() {
      // TODO Java6-Migration
      // @Override
      public int compare( CtClass e1, CtClass e2 ) {
        if ( e1.getName().equals( e2.getName() ) )
          return 0;
        return e2.getClass().isAssignableFrom( e1.getClass() ) ? 1 : -1;
      }
    };
    final Set<CtClass> sortedExceptions = new TreeSet<CtClass>(
        inheritationsComparator );
    sortedExceptions.addAll( Arrays.asList( exceptionTypes ) );
    for ( CtClass e : sortedExceptions ) {
      result.add( e.getName() );
    }
    return result;
  }

  /**
   * Searches for an annotation in both getter and setter of a bean property.
   * This method first searches the read method, then searches the write method.
   * 
   * @param <T>
   *          the bean type
   * @param descriptor
   *          the {@link PropertyDescriptor}
   * @param c
   *          the bean class
   * @return the annotation or null, if not found
   */
  public static <T extends Annotation> T getAnnotation(
      PropertyDescriptor descriptor, Class<T> c ) {
    if ( null == descriptor || null == c )
      return null;
    T result = null;
    // Try to read annotation from getter
    if ( null != descriptor.getReadMethod() )
      result = descriptor.getReadMethod().getAnnotation( c );
    if ( null == result ) {
      if ( null != descriptor.getWriteMethod() )
        result = descriptor.getWriteMethod().getAnnotation( c );
    }
    return result;
  }

  /**
   * Transforms an array of class objects to an array of class names.
   * 
   * @param c
   *          the class objects
   * @return the class names
   */
  public static String[] toTypenames( CtClass[] c ) {
    final String[] result = new String[c.length];
    for ( int i = 0; i < c.length; i++ ) {
      result[i] = c[i].getName();
    }
    return result;
  }

  /**
   * Removes a flag from the flags.
   * 
   * @param flags
   *          the flags
   * @param flagToRemove
   *          the flag
   * @return the flags without the flag
   */
  public static int removeFlag( int flags, int flagToRemove ) {
    return ( flags | flagToRemove ) > 0 ? ( flags ^ flagToRemove ) : flags;
  }

  /**
   * Searches for the first occurrence of a method.
   * 
   * @param c
   *          the class
   * @param name
   *          the method name
   * @param parameterTypes
   *          the parameter types
   * @return the method
   */
  public static CtMethod findMethod( CtClass c, String name,
      String... parameterTypes ) {
    try {
      if ( null != c && !Object.class.getName().equals( c.getName() ) ) {
        for ( CtMethod m : c.getDeclaredMethods() ) {
          if ( name.equals( m.getName() )
              && Arrays.equals( parameterTypes, Utilities.toTypenames( m
                  .getParameterTypes() ) ) ) {
            return m;
          }
        }
        final CtMethod superMethod = findMethod( c.getSuperclass(), name,
            parameterTypes );
        if ( null != superMethod )
          return superMethod;
        final CtClass[] interfaces = c.getInterfaces();
        if ( null != interfaces ) {
          for ( CtClass i : interfaces ) {
            final CtMethod iMethod = findMethod( i, name, parameterTypes );
            if ( null != iMethod )
              return iMethod;
          }
        }
      }
    } catch ( NotFoundException e ) {
      logger.log( Level.WARNING, "Unable to analyze bean \"" + c + "\"!", e );
    }
    return null;
  }

}
