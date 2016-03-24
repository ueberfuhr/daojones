package de.ars.daojones.internal.runtime.test;

import de.ars.daojones.internal.runtime.test.utilities.Messages;
import de.ars.daojones.runtime.test.TestConnectionFactory;
import de.ars.daojones.runtime.test.spi.database.AbstractTestConnectionFactoryModel;

/**
 * A connection factory model for the test driver.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class DefaultTestConnectionFactoryModel extends AbstractTestConnectionFactoryModel {

  /**
   * The id of the driver.
   */
  public static final String ID = "de.ars.daojones.drivers.test"; //$NON-NLS-1$

  private static final long serialVersionUID = 1L;

  private static final Messages nls = Messages.create( "driver.TestConnectionFactoryModel" ); //$NON-NLS-1$

  public DefaultTestConnectionFactoryModel() {
    super( DefaultTestConnectionFactoryModel.ID, TestConnectionFactory.class );
  }

  private final String name = DefaultTestConnectionFactoryModel.nls.get( "driver.name" ); //$NON-NLS-1$
  private final String description = DefaultTestConnectionFactoryModel.nls.get( "driver.description" ); //$NON-NLS-1$

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

}
