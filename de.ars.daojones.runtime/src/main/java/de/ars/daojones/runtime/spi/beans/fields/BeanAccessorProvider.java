package de.ars.daojones.runtime.spi.beans.fields;

/**
 * Common interface for classes that provide a bean accessor.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface BeanAccessorProvider {

  /**
   * Returns the bean accessor.
   * 
   * @return the bean accessor
   */
  BeanAccessor getBeanAccessor();

}
