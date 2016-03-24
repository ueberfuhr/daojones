package de.ars.daojones.runtime.test;

import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModel;

/**
 * Implementors of database drivers can provide a database test driver by
 * implementing this interface. The JUnit runner will then replace the original
 * driver by the test driver.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface TestConnectionFactoryModel extends ConnectionFactoryModel {

  /**
   * Returns the id of the driver that is replaced by this driver.
   * 
   * @return the id of the driver
   */
  String getOriginalId();

}
