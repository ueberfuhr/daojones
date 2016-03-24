package de.ars.daojones.runtime.cli;

import java.util.Iterator;
import java.util.ServiceLoader;

import de.ars.daojones.internal.runtime.cli.HelpExecutor;

/**
 * A manager class that reads command executors.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public abstract class CommandExecutorManager {

  /**
   * The name of the command that prints the help.
   */
  public static final String HELP_COMMAND_NAME = HelpExecutor.NAME;

  private CommandExecutorManager() {
    super();
  }

  private static CommandExecutor findExecutor( final CommandContext context ) {
    if ( null != context.getCommand() ) {
      for ( final CommandExecutor e : CommandExecutorManager.findExecutors() ) {
        if ( context.getCommand().equalsIgnoreCase( context.getCommand( e ) ) ) {
          return e;
        }
      }
    }
    return new HelpExecutor();
  }

  /**
   * Executes the current command.
   * 
   * @param context
   *          the current command's context
   * @throws CommandExecutionException
   *           if an error occured during command execution
   */
  public static void execute( final CommandContext context ) throws CommandExecutionException {
    final CommandExecutor e = CommandExecutorManager.findExecutor( context );
    e.execute( context );
  }

  /**
   * Finds all executors.
   * 
   * @return the executors
   */
  public static Iterable<CommandExecutor> findExecutors() {
    return new Iterable<CommandExecutor>() {

      @Override
      public Iterator<CommandExecutor> iterator() {
        final ServiceLoader<CommandExecutor> sl = ServiceLoader.load( CommandExecutor.class );
        return sl.iterator();
      }
    };
  }
}
