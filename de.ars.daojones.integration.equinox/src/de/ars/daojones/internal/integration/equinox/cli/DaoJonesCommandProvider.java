package de.ars.daojones.internal.integration.equinox.cli;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import de.ars.daojones.integration.equinox.DaoJonesPlugin;
import de.ars.daojones.runtime.cli.CommandContext;
import de.ars.daojones.runtime.cli.CommandExecutionException;
import de.ars.daojones.runtime.cli.CommandExecutor;
import de.ars.daojones.runtime.cli.CommandExecutorManager;
import de.ars.daojones.runtime.cli.PrintWriterCommandContext;

/**
 * A service component that provides commands to handle connections over the
 * OSGi console.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 2.0.0
 */
public class DaoJonesCommandProvider implements CommandProvider {

  public DaoJonesCommandProvider() {
    super();
  }

  /**
   * Displays all connections to the console.
   * 
   * @param ci
   *          the {@link CommandInterpreter}
   * @throws CommandExecutionException
   */
  public void _djShowConnections( final CommandInterpreter ci ) throws CommandExecutionException {
    DaoJonesCommandProvider.execute( "djShowConnections", ci );
  }

  /**
   * Displays the help information to the console.
   * 
   * @param ci
   *          the {@link CommandInterpreter}
   * @throws CommandExecutionException
   */
  public void _djHelp( final CommandInterpreter ci ) throws CommandExecutionException {
    DaoJonesCommandProvider.execute( "djHelp", ci );
  }

  /**
   * Tests a connection file.
   * 
   * @param ci
   *          the {@link CommandInterpreter}
   * @throws CommandExecutionException
   */
  public void _djTest( final CommandInterpreter ci ) throws CommandExecutionException {
    DaoJonesCommandProvider.execute( "djTest", ci );
  }

  private static String toCommand( final String cmd ) {
    return "dj" + Character.toUpperCase( cmd.charAt( 0 ) ) + ( cmd.length() > 1 ? cmd.substring( 1 ) : "" );
  }

  private static void execute( final String command, final CommandInterpreter ci ) throws CommandExecutionException {
    final CommandContext ctx = new CommandInterpreterContext( command, ci ) {
      @Override
      public String getCommand( final CommandExecutor executor ) {
        return DaoJonesCommandProvider.toCommand( super.getCommand( executor ) );
      }
    };
    CommandExecutorManager.execute( ctx );
  }

  @Override
  public String getHelp() {
    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    try {
      final PrintWriter out = new PrintWriter( bout );
      try {
        final CommandContext ctx = new PrintWriterCommandContext( DaoJonesPlugin.getDaoJonesContext(),
                CommandExecutorManager.HELP_COMMAND_NAME, new String[0], out ) {
          @Override
          public String getCommand( final CommandExecutor executor ) {
            return DaoJonesCommandProvider.toCommand( super.getCommand( executor ) );
          }
        };
        try {
          CommandExecutorManager.execute( ctx );
        } catch ( final CommandExecutionException e ) {
          e.printStackTrace( out );
        }
        return new String( bout.toByteArray() );
      } finally {
        out.close();
      }
    } finally {
      try {
        bout.close();
      } catch ( final IOException e ) {
        e.printStackTrace();
      }
    }
  }
}
