package de.ars.daojones.runtime.cli;

import java.io.PrintWriter;

import de.ars.daojones.runtime.context.DaoJonesContext;

/**
 * A command context that prints output to a print writer.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public class PrintWriterCommandContext extends AbstractCommandContext {

  private final PrintWriter out;

  public PrintWriterCommandContext( final DaoJonesContext context, final String command, final String[] arguments,
          final PrintWriter out ) {
    super( context, command, arguments );
    this.out = out;
  }

  @Override
  public void print( final Object o ) {
    out.print( o );
    out.flush();
  }

  @Override
  public void println() {
    out.println();
    out.flush();
  }

  @Override
  public void println( final Object o ) {
    out.println( o );
    out.flush();
  }

  @Override
  public void printStackTrace( final Throwable t ) {
    t.printStackTrace( out );
    out.flush();
  }

}
