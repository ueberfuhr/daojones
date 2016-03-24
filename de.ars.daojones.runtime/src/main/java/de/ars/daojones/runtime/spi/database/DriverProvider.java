package de.ars.daojones.runtime.spi.database;

import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessor;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorContext;

/**
 * A provider for driver-dependent utility methods.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 * @param <T>
 *          the bean type
 */
public interface DriverProvider<T> {

  /**
   * Returns the driver.
   * 
   * @return the driver
   */
  Connection<T> getDriver();

  /**
   * Returns the bean accessor.
   * 
   * @return the bean accessor
   */
  BeanAccessor getBeanAccessor();

  /**
   * Returns the bean class.
   * 
   * @param model
   *          the bean model
   * @return the bean class
   * @throws ClassNotFoundException
   *           if the bean class could not be loaded
   */
  Class<? extends T> getBeanClass( final BeanModel model ) throws ClassNotFoundException;

  /**
   * Creates the bean accessor context-
   * 
   * @param t
   *          the database entry
   * @param type
   *          the bean type
   * @return the bean accessor context
   */
  BeanAccessorContext<T> createBeanAccessorContext( final DatabaseEntry t, final Class<? extends T> type );
}
