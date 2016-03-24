package de.ars.daojones.runtime.test.spi.database;

import de.ars.daojones.internal.runtime.test.junit.TestContext;
import de.ars.daojones.internal.runtime.test.junit.TestContext.TestSetup;
import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModelImpl;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.spi.database.ConnectionFactory;
import de.ars.daojones.runtime.test.TestConnectionFactory;

/**
 * A connection factory model for a test driver. Test drivers should extend from
 * this super class.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public abstract class AbstractTestConnectionFactoryModel extends ConnectionFactoryModelImpl {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   * 
   * @param id
   *          the id
   * @param clazz
   *          the implementation class
   */
  public AbstractTestConnectionFactoryModel( final String id, final Class<? extends TestConnectionFactory> clazz ) {
    super( id, clazz );
  }

  @Override
  protected TestConnectionFactory createInstance( final Class<? extends ConnectionFactory> clazz )
          throws ConfigurationException {
    final TestConnectionFactory result = ( TestConnectionFactory ) super.createInstance( clazz );
    final TestSetup setup = TestContext.getTestSetup();
    if ( null != setup ) {
      setup.configure( result );
    }
    return result;
  }

}
