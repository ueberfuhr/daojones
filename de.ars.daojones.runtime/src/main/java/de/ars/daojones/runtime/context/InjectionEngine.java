package de.ars.daojones.runtime.context;

import de.ars.daojones.runtime.beans.fields.FieldAccessException;

/**
 * An object that injects values into a bean.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface InjectionEngine {
  /**
   * Creates an instance of a bean type by invoking the constructor. If there
   * isn't any constructor model, the default constructor is invoked.<br/>
   * <br/>
   * <b>Please note:</b> This method invokes the full injection of constructor
   * parameters, fields and method parameters.
   * 
   * @param <T>
   *          the bean type
   * @param beanClass
   *          the bean class
   * @param context
   *          the injection context
   * @throws InjectionException
   * @return the bean instance
   * @see InjectionContextFactory
   */
  <T> T createBeanInstance( Class<T> beanClass, InjectionContext context ) throws InjectionException;

  /**
   * Injects field and method parameter values into a bean. At first, fields are
   * injected, then methods are invoked.<br/>
   * <br/>
   * <b>Please note:</b> It is not possible to inject constructor parameters to
   * a bean that was already initialized.
   * 
   * @param context
   *          the bean accessor context
   * @param bean
   *          the bean
   * @throws FieldAccessException
   *           if accessing or converting a field fails
   * @throws InjectionException
   * @see InjectionContextFactory
   */
  void inject( Object bean, InjectionContext context ) throws InjectionException;

}
