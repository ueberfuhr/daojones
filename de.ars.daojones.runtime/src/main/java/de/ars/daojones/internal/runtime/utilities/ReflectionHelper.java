package de.ars.daojones.internal.runtime.utilities;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class ReflectionHelper {

  private static final Map<String, Class<?>> primitiveTypesByName = new HashMap<String, Class<?>>();
  private static final Map<Class<?>, Class<?>> primitiveToReference = new HashMap<Class<?>, Class<?>>();
  private static final Map<Class<?>, Object> primitiveNullValues = new HashMap<Class<?>, Object>();
  private static final Map<Class<?>, Class<?>> primitiveArrays = new HashMap<Class<?>, Class<?>>();

  static {

    ReflectionHelper.primitiveTypesByName.put( Boolean.TYPE.getName(), Boolean.TYPE );
    ReflectionHelper.primitiveTypesByName.put( Short.TYPE.getName(), Short.TYPE );
    ReflectionHelper.primitiveTypesByName.put( Integer.TYPE.getName(), Integer.TYPE );
    ReflectionHelper.primitiveTypesByName.put( Long.TYPE.getName(), Long.TYPE );
    ReflectionHelper.primitiveTypesByName.put( Character.TYPE.getName(), Character.TYPE );
    ReflectionHelper.primitiveTypesByName.put( Float.TYPE.getName(), Float.TYPE );
    ReflectionHelper.primitiveTypesByName.put( Double.TYPE.getName(), Double.TYPE );

    ReflectionHelper.primitiveToReference.put( Integer.TYPE, Integer.class );
    ReflectionHelper.primitiveToReference.put( Long.TYPE, Long.class );
    ReflectionHelper.primitiveToReference.put( Short.TYPE, Short.class );
    ReflectionHelper.primitiveToReference.put( Byte.TYPE, Byte.class );
    ReflectionHelper.primitiveToReference.put( Double.TYPE, Double.class );
    ReflectionHelper.primitiveToReference.put( Float.TYPE, Float.class );
    ReflectionHelper.primitiveToReference.put( Boolean.TYPE, Boolean.class );
    ReflectionHelper.primitiveToReference.put( Character.TYPE, Character.class );

    ReflectionHelper.primitiveNullValues.put( Integer.TYPE, Integer.valueOf( 0 ) );
    ReflectionHelper.primitiveNullValues.put( Long.TYPE, Long.valueOf( 0l ) );
    ReflectionHelper.primitiveNullValues.put( Short.TYPE, Short.valueOf( ( short ) 0 ) );
    ReflectionHelper.primitiveNullValues.put( Byte.TYPE, Byte.valueOf( ( byte ) 0 ) );
    ReflectionHelper.primitiveNullValues.put( Double.TYPE, Double.valueOf( 0d ) );
    ReflectionHelper.primitiveNullValues.put( Float.TYPE, Float.valueOf( 0f ) );
    ReflectionHelper.primitiveNullValues.put( Boolean.TYPE, Boolean.FALSE );
    ReflectionHelper.primitiveNullValues.put( Character.TYPE, Character.valueOf( ( char ) 0 ) );

    ReflectionHelper.primitiveArrays.put( Integer.TYPE, int[].class );
    ReflectionHelper.primitiveArrays.put( Long.TYPE, long[].class );
    ReflectionHelper.primitiveArrays.put( Short.TYPE, short[].class );
    ReflectionHelper.primitiveArrays.put( Byte.TYPE, byte[].class );
    ReflectionHelper.primitiveArrays.put( Double.TYPE, double[].class );
    ReflectionHelper.primitiveArrays.put( Float.TYPE, float[].class );
    ReflectionHelper.primitiveArrays.put( Boolean.TYPE, boolean[].class );
    ReflectionHelper.primitiveArrays.put( Character.TYPE, char[].class );

  }

  private ReflectionHelper() {
    super();
  }

  public static boolean isPrimitiveTypeName( final String name ) {
    return ReflectionHelper.primitiveTypesByName.containsKey( name );
  }

  public static Class<?> getPrimitiveType( final String name ) {
    return ReflectionHelper.primitiveTypesByName.get( name );
  }

  public static Class<?> getReferenceType( final Class<?> c ) {
    return c.isPrimitive() ? ReflectionHelper.primitiveToReference.get( c ) : c;
  }

  @SuppressWarnings( "unchecked" )
  public static <T> T getNullValue( final Class<T> c ) {
    // null for reference types
    return ( T ) ReflectionHelper.primitiveNullValues.get( c );
  }

  public static Class<?> findClass( final Class<?> beanClass, final String className ) {
    final ThreadLocal<Class<?>> result = new ThreadLocal<Class<?>>();
    if ( null != beanClass && null != className ) {
      new TypeHierarchyVisitor() {

        @Override
        protected boolean visit( final Class<?> c ) {
          if ( beanClass.getName().equals( className ) ) {
            result.set( c );
            return false;
          } else {
            return true;
          }
        }
      }.accept( beanClass );
    }
    return result.get();
  }

  public static Class<?> findClass( final String name, final ClassLoader cl ) throws ClassNotFoundException {
    if ( ReflectionHelper.isPrimitiveTypeName( name ) ) {
      return ReflectionHelper.getPrimitiveType( name );
    } else {
      return Class.forName( name, true, cl );
    }
  }

  public static Class<?> toArrayType( final Class<?> c ) throws ClassNotFoundException {
    // Only for reference types
    final String arrayTypeName = "[L" + c.getName() + ";";
    final Class<?> result = c.isPrimitive() ? ReflectionHelper.primitiveArrays.get( c ) : Class.forName( arrayTypeName,
            false, c.getClassLoader() );
    if ( null == result ) {
      throw new ClassNotFoundException( arrayTypeName );
    } else {
      return result;
    }
  }

  @SuppressWarnings( "unchecked" )
  public static <T> T[] createArray( final Class<T> componentType, final int length ) {
    return ( T[] ) Array.newInstance( componentType, length );
  }

  public static <A extends AccessibleObject, R> R runWithAccessible( final A accessibleObject,
          final AccessAction<A, R> action ) throws Exception {
    final boolean accessible = accessibleObject.isAccessible();
    if ( !accessible ) {
      accessibleObject.setAccessible( true );
    }
    try {
      return action.access( accessibleObject );
    } finally {
      if ( !accessible ) {
        accessibleObject.setAccessible( false );
      }
    }
  }

  public static interface AccessAction<A, R> {

    public R access( A a ) throws Exception;

  }

  public static final class Parameter {
    private final Class<?> type;
    private final Object value;

    public <T, V extends T> Parameter( final Class<T> type, final V value ) {
      super();
      this.type = type;
      this.value = value;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public Parameter( final Object value ) {
      this( ( Class ) value.getClass(), value );
    }

    public Class<?> getType() {
      return type;
    }

    public Object getValue() {
      return value;
    }

  }

  public static <T> T newInstance( final Class<T> c, final Parameter... params ) throws InstantiationException {
    //    final Class<?> declaringClass = c.getDeclaringClass();
    //    if ( null != declaringClass ) {
    //      Parameter outerParam;
    //      if ( Modifier.isStatic( c.getModifiers() ) ) {
    //        outerParam = new Parameter( Class.class, declaringClass );
    //      } else {
    //        outerParam = new Parameter( declaringClass, null /*no context object!*/);
    //      }
    //      final Parameter[] newParams = new Parameter[params.length + 1];
    //      newParams[0] = outerParam;
    //      System.arraycopy( params, 0, newParams, 1, params.length );
    //      return ReflectionHelper._newInstance( c, newParams );
    //    } else {
    return ReflectionHelper.newInstanceInternal( c, params );
    //    }
  }

  @SuppressWarnings( "unchecked" )
  private static <T> Constructor<T> findConstructor( final Class<T> c, final Class<?>[] paramTypes )
          throws SecurityException, NoSuchMethodException {
    // Works better for private constructors, inner types, ...
    for ( final Constructor<?> constructor : c.getDeclaredConstructors() ) {
      if ( Arrays.equals( constructor.getParameterTypes(), paramTypes ) ) {
        return ( Constructor<T> ) constructor;
      }
    }
    // Should throw an exception
    return c.getDeclaredConstructor( paramTypes );
  }

  private static <T> T newInstanceInternal( final Class<T> c, final Parameter... params ) throws InstantiationException {
    final Class<?>[] types = new Class<?>[params.length];
    final Object[] values = new Object[params.length];
    for ( int i = 0; i < params.length; i++ ) {
      final Parameter p = params[i];
      types[i] = p.getType();
      values[i] = p.getValue();
    }
    try {
      final Constructor<T> constructor = ReflectionHelper.findConstructor( c, types );
      return ReflectionHelper.runWithAccessible( constructor, new AccessAction<Constructor<T>, T>() {

        @Override
        public T access( final Constructor<T> a ) throws Exception {
          return constructor.newInstance( values );
        }
      } );
    } catch ( final InstantiationException e ) {
      throw e;
    } catch ( final Exception e ) {
      final InstantiationException result = new InstantiationException();
      result.initCause( e );
      throw result;
    }
  }
}
