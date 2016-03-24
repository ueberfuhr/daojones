package de.ars.daojones.internal.integration.eclipse.preferences.pages;

import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping.DataSourceType;
import de.ars.daojones.runtime.configuration.connections.Connection;
import de.ars.daojones.runtime.configuration.context.BeanModelImpl;
import de.ars.daojones.runtime.configuration.context.ConnectionModelImpl;
import de.ars.daojones.runtime.configuration.context.DaoJonesContextConfiguration;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.Application;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.query.Negation;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.query.Tautology;

/**
 * A utility class providing helper methods for testing connections.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 */
public final class ConnectionTester {

  /**
   * Just a dao for testing connections.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH
   */
  public static class TestBean {
  }

  private ConnectionTester() {
    super();
  }

  /**
   * Opens a connection and searches for a test bean.
   * 
   * @param model
   *          the connection
   * @throws DataAccessException
   * @throws ConfigurationException
   */
  public static void testConnection( final DaoJonesContext ctx, final Connection model ) throws DataAccessException,
          ConfigurationException {
    final DaoJonesContextConfiguration config = ctx.getConfiguration();
    final String application = ConnectionTester.class.getName() + System.currentTimeMillis();
    // Register bean model
    final Bean testBean = new Bean();
    testBean.setType( TestBean.class.getName() );
    final DatabaseTypeMapping dtm = new DatabaseTypeMapping();
    dtm.setName( "TestTable" );
    dtm.setType( DataSourceType.TABLE );
    testBean.setTypeMapping( dtm );
    final BeanModelImpl testBeanModel = new BeanModelImpl( application, testBean );
    config.getBeanModelManager().register( testBeanModel );
    // Copy connection
    final Connection testConnection = new Connection();
    testConnection.setId( "test-connection" );
    testConnection.setCredential( model.getCredential() );
    testConnection.setDatabase( model.getDatabase() );
    testConnection.setType( model.getType() );
    // no cache, max results = 1, no default, only the TestDao is read
    testConnection.setCache( null );
    testConnection.setMaximumResults( 1 );
    testConnection.setDefault( false );
    testConnection.getAssignedBeanTypes().add( TestBean.class.getName() );
    final ConnectionModelImpl testConnectionModel = new ConnectionModelImpl( application, testConnection );
    // Register connection model
    config.getConnectionModelManager().register( testConnectionModel );

    final Application app = ctx.getApplication( application );
    try {
      // get connection instance(s)
      final de.ars.daojones.runtime.connections.Connection<TestBean> con = app.getConnection( TestBean.class );
      try {
        con.find( Query.create().only( Negation.not( new Tautology() ) ) );
      } finally {
        con.close();
      }
    } finally {
      app.close();
    }
  }
}
