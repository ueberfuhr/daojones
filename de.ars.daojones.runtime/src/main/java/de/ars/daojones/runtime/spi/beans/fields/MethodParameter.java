package de.ars.daojones.runtime.spi.beans.fields;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;

/**
 * A method parameter. The Reflection API does not provide method parameter
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the declaring type
 */
public interface MethodParameter extends Member, AnnotatedElement {

  /**
   * Returns the declaring method. If the parameter is declared by a
   * constructor, this method returns <tt>null</tt>.
   * 
   * @return the declaring method
   */
  java.lang.reflect.Method getDeclaringMethod();

  /**
   * Returns the declaring constructor. If the parameter is declared by a
   * method, this method returns <tt>null</tt>.
   * 
   * @return the declaring constructor
   */
  java.lang.reflect.Constructor<?> getDeclaringConstructor();

  /**
   * Returns the parameter index.
   * 
   * @return the parameter index
   */
  int getParameterIndex();

  /**
   * Returns the parameter type.
   * 
   * @return the parameter type
   */
  Class<?> getType();

}