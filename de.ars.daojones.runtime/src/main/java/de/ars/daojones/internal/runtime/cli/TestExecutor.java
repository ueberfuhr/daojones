package de.ars.daojones.internal.runtime.cli;

import java.io.File;
import java.net.URL;

import de.ars.daojones.runtime.cli.CommandContext;
import de.ars.daojones.runtime.cli.CommandExecutionException;
import de.ars.daojones.runtime.cli.CommandLineParameter;
import de.ars.daojones.runtime.cli.DaoJonesContextAwareExecutor;
import de.ars.daojones.runtime.configuration.provider.ConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.DaoJonesContextConfigurator;
import de.ars.daojones.runtime.configuration.provider.XmlConnectionConfigurationSource;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.context.DaoJonesContext;

/**
 * Executor that reads a connection file and
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public class TestExecutor extends DaoJonesContextAwareExecutor {

  private static final CommandLineParameter FILE = new CommandLineParameter( "config",
          "The name of the connection file containing the connections to test." );

  @Override
  public void execute( final CommandContext context ) throws CommandExecutionException {
    super.execute( context );
    try {
      context.println( "Executing test." );
      final String fileName = TestExecutor.FILE.getValue( context.getParameters() );
      if ( null == fileName ) {
        throw new CommandExecutionException( this, context, new IllegalArgumentException( "Parameter \""
                + TestExecutor.FILE.getName() + "\" not found." ) );
      }
      final File file = new File( fileName );
      context.println( "Reading file \"" + file.getAbsolutePath() + "\"." );
      final String application = TestExecutor.class.getName();
      final DaoJonesContext ctx = context.getDaoJonesContext();
      try {
        final URL url = file.toURI().toURL();
        final ConfigurationSource config = new XmlConnectionConfigurationSource( application, url );
        final DaoJonesContextConfigurator configurator = new DaoJonesContextConfigurator( ctx );
        configurator.configure( config );
        context.println( "Reading connection for Test bean." );
        final ConnectionProvider provider = ctx.getApplication( application );
        final Connection<TestBean> con = provider.getConnection( TestBean.class );
        context.println( "Executing dummy query." );
        con.find();
        context.println( "Successfully finished test." );
      } finally {
        ctx.getApplication( application ).close();
      }
    } catch ( final Exception e ) {
      context.printStackTrace( e );
    }
  }

  @Override
  public String getDescription() {
    return "Reads a connection file and tests the connection by executing a dummy query. "
            + "The connection must be declared as default connection.";
  }

  @Override
  public CommandLineParameter[] getParameters() {
    return new CommandLineParameter[] { TestExecutor.FILE };
  }

  @Override
  public String getName() {
    return "test";
  }

}
