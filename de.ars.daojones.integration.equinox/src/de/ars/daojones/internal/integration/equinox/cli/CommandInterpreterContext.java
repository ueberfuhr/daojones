package de.ars.daojones.internal.integration.equinox.cli;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.osgi.framework.console.CommandInterpreter;

import de.ars.daojones.integration.equinox.DaoJonesPlugin;
import de.ars.daojones.runtime.cli.AbstractCommandContext;

public class CommandInterpreterContext extends AbstractCommandContext {

  private final CommandInterpreter interpreter;

  public CommandInterpreterContext( final String command, final CommandInterpreter interpreter ) {
    super( DaoJonesPlugin.getDaoJonesContext(), command, CommandInterpreterContext.getArguments( interpreter ) );
    this.interpreter = interpreter;
  }

  private static String[] getArguments( final CommandInterpreter ci ) {
    final List<String> args = new LinkedList<String>();
    String arg;
    while ( null != ( arg = ci.nextArgument() ) ) {
      args.add( arg );
    }
    return args.toArray( new String[args.size()] );
  }

  @Override
  public void println( final Object o ) {
    this.interpreter.println( o );
  }

  @Override
  public void println() {
    this.interpreter.println();
  }

  @Override
  public void printStackTrace( final Throwable t ) {
    this.interpreter.printStackTrace( t );
  }

  @Override
  public void print( final Object o ) {
    this.interpreter.print( o );
  }

}
