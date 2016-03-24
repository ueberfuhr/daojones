package de.ars.daojones.runtime.spi.beans.fields;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

class MethodParameterImpl implements MethodParameter {

  private final java.lang.reflect.Method declaringMethod;
  private final java.lang.reflect.Constructor<?> declaringConstructor;
  private final int index;

  public MethodParameterImpl( final java.lang.reflect.Method declaringMethod, final int index ) {
    this( declaringMethod, null, index );
  }

  public MethodParameterImpl( final Constructor<?> declaringConstructor, final int index ) {
    this( null, declaringConstructor, index );
  }

  private MethodParameterImpl( final java.lang.reflect.Method declaringMethod,
          final Constructor<?> declaringConstructor, final int index ) {
    super();
    this.declaringMethod = declaringMethod;
    this.declaringConstructor = declaringConstructor;
    this.index = index;
  }

  @Override
  public Class<?> getDeclaringClass() {
    if ( null != declaringMethod ) {
      return declaringMethod.getDeclaringClass();
    } else {
      return declaringConstructor.getDeclaringClass();
    }
  }

  @Override
  public int getModifiers() {
    return 0;
  }

  @Override
  public String getName() {
    return "arg" + index;
  }

  @Override
  public boolean isSynthetic() {
    return false;
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public <A extends Annotation> A getAnnotation( final Class<A> annotationType ) {
    for ( final Annotation a : getAnnotations() ) {
      if ( annotationType.isAssignableFrom( a.getClass() ) ) {
        return ( A ) a;
      }
    }
    return null;
  }

  @Override
  public Annotation[] getAnnotations() {
    if ( null != declaringMethod ) {
      return declaringMethod.getParameterAnnotations()[index];
    } else {
      return declaringConstructor.getParameterAnnotations()[index];
    }
  }

  @Override
  public Annotation[] getDeclaredAnnotations() {
    return getAnnotations();
  }

  @Override
  public boolean isAnnotationPresent( final Class<? extends Annotation> annotationType ) {
    return null != getAnnotation( annotationType );
  }

  @Override
  public java.lang.reflect.Method getDeclaringMethod() {
    return declaringMethod;
  }

  @Override
  public Constructor<?> getDeclaringConstructor() {
    return declaringConstructor;
  }

  @Override
  public int getParameterIndex() {
    return index;
  }

  @Override
  public Class<?> getType() {
    if ( null != declaringMethod ) {
      return declaringMethod.getParameterTypes()[index];
    } else {
      return declaringConstructor.getParameterTypes()[index];
    }
  }

}