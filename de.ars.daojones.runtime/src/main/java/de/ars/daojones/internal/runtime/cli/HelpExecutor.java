package de.ars.daojones.internal.runtime.cli;

import de.ars.daojones.runtime.cli.CommandContext;
import de.ars.daojones.runtime.cli.CommandExecutionException;
import de.ars.daojones.runtime.cli.CommandExecutor;
import de.ars.daojones.runtime.cli.CommandExecutorManager;
import de.ars.daojones.runtime.cli.CommandLineParameter;

public class HelpExecutor implements CommandExecutor {

  public static final String NAME = "help";

  @Override
  public void execute( final CommandContext context ) throws CommandExecutionException {
    context.print( "---DaoJones Runtime---" );
    for ( final CommandExecutor e : CommandExecutorManager.findExecutors() ) {
      if ( !this.getName().equals( e.getName() ) ) {
        context.println();
        context.print( "\t" );
        context.print( context.getCommand( e ) );
        for ( final CommandLineParameter param : e.getParameters() ) {
          context.print( " " );
          context.print( param.getName() );
          context.print( "=<" + param.getName() + ">" );
        }
        context.print( " - " );
        context.print( e.getDescription() );
        // -----------
        for ( final CommandLineParameter param : e.getParameters() ) {
          context.println();
          context.print( "\t\t\t" );
          context.print( "<" + param.getName() + ">" );
          context.print( " - " );
          context.print( param.getDescription() );
        }
      }
    }
    context.println();
    context.println();
  }

  @Override
  public String getName() {
    return HelpExecutor.NAME;
  }

  @Override
  public String getDescription() {
    return "Prints the help to all commands.";
  }

  @Override
  public CommandLineParameter[] getParameters() {
    return new CommandLineParameter[0];
  }

}
