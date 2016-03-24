package de.ars.daojones.internal.runtime.configuration.provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import javassist.bytecode.MethodInfo;

import org.reflections.ReflectionUtils;
import org.reflections.ReflectionsException;
import org.reflections.scanners.AbstractScanner;

class MethodParametersAnnotationsScanner extends AbstractScanner {

  private static final String CONSTRUCTOR_NAME = "<init>";

  @SuppressWarnings( "unchecked" )
  @Override
  public void scan( final Object cls ) {
    getMetadataAdapter().getClassName( cls );
    final String className = getMetadataAdapter().getClassName( cls );

    final List<MethodInfo> methods = getMetadataAdapter().getMethods( cls );
    for ( final MethodInfo method : methods ) {
      final List<String> parameters = getMetadataAdapter().getParameterNames( method );
      for ( int parameterIndex = 0; parameterIndex < parameters.size(); parameterIndex++ ) {
        final List<String> parameterAnnotations = getMetadataAdapter().getParameterAnnotationNames( method,
                parameterIndex );
        for ( final String parameterAnnotation : parameterAnnotations ) {
          if ( acceptResult( parameterAnnotation ) ) {
            final String methodName = method.isConstructor() ? MethodParametersAnnotationsScanner.CONSTRUCTOR_NAME
                    : method.getName();
            final StringBuffer sb = new StringBuffer();
            sb.append( className ).append( ':' ).append( methodName ).append( ':' );
            for ( final String p : parameters ) {
              sb.append( p ).append( ':' );
            }
            sb.append( Integer.toString( parameterIndex ) );
            getStore().put( parameterAnnotation, sb.toString() );
          }
        }
      }
    }
  }

  /**
   * An object that identifies a method parameter.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
   * @since 2.0
   */
  public static class Parameter {
    private final Constructor<?> constructor;
    private final Method method;
    private final int parameterIndex;

    private Parameter( final Method method, final int parameterIndex ) {
      super();
      constructor = null;
      this.method = method;
      this.parameterIndex = parameterIndex;
    }

    private Parameter( final Constructor<?> constructor, final int parameterIndex ) {
      super();
      this.constructor = constructor;
      method = null;
      this.parameterIndex = parameterIndex;
    }

    /**
     * The constructor.
     * 
     * @return the constructor
     */
    protected Constructor<?> getConstructor() {
      return constructor;
    }

    /**
     * The method.
     * 
     * @return the method
     */
    protected Method getMethod() {
      return method;
    }

    protected boolean isConstructor() {
      return null != constructor;
    }

    /**
     * The index of the parameter.
     * 
     * @return the index of the parameter
     */
    protected int getParameterIndex() {
      return parameterIndex;
    }

    @Override
    public String toString() {
      final StringBuilder builder = new StringBuilder();
      builder.append( "Parameter [method=" ).append( method ).append( ", parameterIndex=" ).append( parameterIndex )
              .append( "]" );
      return builder.toString();
    }

  }

  /**
   * Parses the descriptor and returns the object that identifies a method
   * parameter.
   * 
   * @param descriptor
   *          the descriptor
   * @param dependenciesClassLoader
   *          the classloader that resolves dependencies
   * @return the parameter
   */
  public static Parameter getParameterFromDescriptor( final String descriptor, final ClassLoader dependenciesClassLoader ) {
    final String[] parts = descriptor.split( ":" );
    //todo create field md
    final String className = parts[0];
    final Class<?> c = ReflectionUtils.forName( className, dependenciesClassLoader );
    final String methodName = parts[1];
    final Class<?>[] parameterTypes = new Class[parts.length - 3];
    for ( int i = 2; i < parts.length - 1; i++ ) {
      parameterTypes[i - 2] = ReflectionUtils.forName( parts[i] );
    }
    try {
      final int parameterIndex = Integer.valueOf( parts[parts.length - 1] );
      if ( MethodParametersAnnotationsScanner.CONSTRUCTOR_NAME.equals( methodName ) ) {
        final Constructor<?> constructor = c.getDeclaredConstructor( parameterTypes );
        return new Parameter( constructor, parameterIndex );
      } else {
        final Method method = c.getDeclaredMethod( methodName, parameterTypes );
        return new Parameter( method, parameterIndex );
      }
    } catch ( final NoSuchMethodException e ) {
      throw new ReflectionsException( "Can't resolve method parameter for \"" + descriptor + "\"!", e );
    }

  }
}