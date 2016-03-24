package de.ars.daojones.beans.model.impl.reflect;

import static de.ars.daojones.LoggerConstants.ERROR;
import static de.ars.daojones.LoggerConstants.getLogger;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.ars.daojones.annotations.Column;

/**
 * A helper class providing utility methods.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 */
public class Utilities {

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
  public static List<String> toExceptionTypes( Class<?>[] exceptionTypes ) {
    final List<String> result = new LinkedList<String>();
    final Comparator<Class<?>> inheritationsComparator = new Comparator<Class<?>>() {
      // TODO Java6-Migration
      // @Override
      public int compare( Class<?> e1, Class<?> e2 ) {
        if ( e1.getName().equals( e2.getName() ) )
          return 0;
        return e2.isAssignableFrom( e1 ) ? 1 : -1;
      }
    };
    final Set<Class<?>> sortedExceptions = new TreeSet<Class<?>>(
        inheritationsComparator );
    sortedExceptions.addAll( Arrays.asList( exceptionTypes ) );
    for ( Class<?> e : sortedExceptions ) {
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
   * Returns the annotation of the field of the bean.
   * 
   * @param c
   *          the bean class
   * @param fieldName
   *          the name of the field
   * @param annotationClass
   *          the annotation class to be searched for
   * @param <T>
   *          the type parameter
   * @return the instance of the annotation
   */
  public static <T extends Annotation> T getAnnotation( Class<Object> c,
      String fieldName, Class<T> annotationClass ) {
    return getAnnotation( getPersistentFields0( c ).get( fieldName ),
        annotationClass );
  }

  /**
   * Returns true, if the bean property is persisted by DaoJones. A property is
   * persistent if it is annotated with {@link Column}.
   * 
   * @param descriptor
   *          the {@link PropertyDescriptor}
   * @return true, if the bean property is persisted by DaoJones
   */
  public static boolean isPersistent( PropertyDescriptor descriptor ) {
    return getAnnotation( descriptor, Column.class ) != null;
  }

  /**
   * Merges the overwritten method to the main map.
   * 
   * @param main
   *          the main map
   * @param overwritten
   *          the overwritten method
   * @throws IntrospectionException
   */
  private static void merge( Map<String, PropertyDescriptor> main,
      PropertyDescriptor overwritten ) throws IntrospectionException {
    if ( main.containsKey( overwritten.getName() ) ) {
      final PropertyDescriptor pMain = main.get( overwritten.getName() );
      final String propertyName = pMain.getName();
      final Method readMethod = ( null != overwritten.getReadMethod() ? overwritten
          : pMain ).getReadMethod();
      final Method writeMethod = ( null != overwritten.getWriteMethod() ? overwritten
          : pMain ).getWriteMethod();
      main.put( propertyName, new PropertyDescriptor( propertyName, readMethod,
          writeMethod ) );
    } else {
      main.put( overwritten.getName(), overwritten );
    }
  }

  /**
   * Merges the overwritten methods to the main map.
   * 
   * @param main
   *          the main map
   * @param overwritten
   *          the overwritten methods
   * @throws IntrospectionException
   */
  private static void merge( Map<String, PropertyDescriptor> main,
      Map<String, PropertyDescriptor> overwritten )
      throws IntrospectionException {
    for ( Map.Entry<String, PropertyDescriptor> entry : overwritten.entrySet() ) {
      merge( main, entry.getValue() );
    }
  }

  private static boolean hasNamePrefix( Method m, String prefix ) {
    return m.getName().startsWith( prefix )
        && m.getName().length() > prefix.length();
  }

  private static boolean isDefaultGetter( Method m ) {
    return hasNamePrefix( m, "get" ) && !Void.TYPE.equals( m.getReturnType() )
        && m.getParameterTypes().length == 0;
  }

  private static boolean isBooleanGetter( Method m ) {
    return hasNamePrefix( m, "is" ) && Boolean.TYPE.equals( m.getReturnType() )
        && m.getParameterTypes().length == 0;
  }

  private static boolean isSetter( Method m ) {
    return hasNamePrefix( m, "set" ) && Void.TYPE.equals( m.getReturnType() )
        && m.getParameterTypes().length == 1;
  }

  private static String toFirstLowerCase( String text ) {
    return ( null != text ? text.substring( 0, 1 ).toLowerCase()
        + ( text.length() > 1 ? text.substring( 1 ) : "" ) : null );
  }

  private static Map<String, PropertyDescriptor> getPropertiesForThisClass(
      Class<?> c ) throws IntrospectionException {
    final Map<String, PropertyDescriptor> result = new TreeMap<String, PropertyDescriptor>();
    for ( Method m : c.getDeclaredMethods() ) {
      final int flags = m.getModifiers();
      if ( !Modifier.isPrivate( flags ) && !Modifier.isStatic( flags ) ) {
        if ( isDefaultGetter( m ) ) {
          final String propertyName = toFirstLowerCase( m.getName().substring(
              3 ) );
          final PropertyDescriptor desc = new PropertyDescriptor( propertyName,
              m, null );
          merge( result, desc );
        } else if ( isBooleanGetter( m ) ) {
          final String propertyName = toFirstLowerCase( m.getName().substring(
              2 ) );
          final PropertyDescriptor desc = new PropertyDescriptor( propertyName,
              m, null );
          merge( result, desc );
        } else if ( isSetter( m ) ) {
          final String propertyName = toFirstLowerCase( m.getName().substring(
              3 ) );
          final PropertyDescriptor desc = new PropertyDescriptor( propertyName,
              null, m );
          merge( result, desc );
        }
      }
    }
    return result;
  }

  /**
   * Returns a map of properties of a class by their name. This method searches
   * in subclasses and interfaces.
   * 
   * @param c
   *          the class
   * @return the map of properties
   * @throws IntrospectionException
   */
  private static Map<String, PropertyDescriptor> getProperties( Class<?> c )
      throws IntrospectionException {
    /*
     * We cannot use
     *  - Introspector.getBeanInfo( c ).getPropertyDescriptors()
     *  - c.getMethods()
     * because they only fetch public members. We need non-private members!
     */
    final Map<String, PropertyDescriptor> result = new TreeMap<String, PropertyDescriptor>();
    if ( null != c && !c.isAssignableFrom( Object.class ) ) {
      // first, take super properties
      result.putAll( getProperties( c.getSuperclass() ) );
      // overwrite super properties by interface properties
      for ( Class<?> i : c.getInterfaces() ) {
        merge( result, getProperties( i ) );
      }
      // overwrite all properties by own properties
      merge( result, getPropertiesForThisClass( c ) );
    }
    return result;
  }

  /**
   * Returns all persistent properties of a bean class. A property is persistent
   * if it is annotated with {@link Column}.
   * 
   * @see #isPersistent(PropertyDescriptor)
   * @param c
   *          the bean class
   * @return the collection of persistent properties
   */
  public static Collection<PropertyDescriptor> getPersistentFields( Class<?> c ) {
    return getPersistentFields0( c ).values();
  }

  private static Map<String, PropertyDescriptor> getPersistentFields0(
      Class<?> c ) {
    final ConcurrentMap<String, PropertyDescriptor> result = new ConcurrentHashMap<String, PropertyDescriptor>();
    if ( null != c && !c.isAssignableFrom( Object.class ) ) {
      try {
        for ( PropertyDescriptor descriptor : getProperties( c ).values() ) {
          if ( isPersistent( descriptor ) )
            result.put( descriptor.getName(), descriptor );
        }
      } catch ( IntrospectionException e ) {
        getLogger().log( ERROR, "Error on reading class information.", e );
      }
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
  public static String[] toTypenames( Class<?>[] c ) {
    final String[] result = new String[c.length];
    for ( int i = 0; i < c.length; i++ ) {
      result[i] = c[i].getName();
    }
    return result;
  }

  /**
   * Returns the human-readable, source-code conform, fully-qualified name of a
   * type.
   * 
   * @param type
   *          the type
   * @return the type name
   */
  public static String getTypeName( Class<?> type ) {
    if ( null != type ) {
      if ( type.isArray() ) {
        return getTypeName( type.getComponentType() ) + "[]";
      } else {
        return type.getName();
      }
    } else {
      return null;
    }
  }

}
