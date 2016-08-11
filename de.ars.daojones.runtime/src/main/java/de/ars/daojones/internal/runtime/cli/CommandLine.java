package de.ars.daojones.internal.runtime.cli;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import de.ars.daojones.runtime.cli.CommandContext;
import de.ars.daojones.runtime.cli.CommandExecutorManager;
import de.ars.daojones.runtime.cli.PrintWriterCommandContext;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.context.DaoJonesContextFactory;

/**
 * The command-line interface for DaoJones.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public class CommandLine {

  public static void main( final String[] args ) throws Exception {
    final Charset cs = Charset.defaultCharset();
    final String command = args.length > 0 ? args[1] : CommandExecutorManager.HELP_COMMAND_NAME;
    final DaoJonesContextFactory dcf = new DaoJonesContextFactory();
    final DaoJonesContext dc = dcf.createContext();
    try {
      final PrintWriter pw = new PrintWriter( new OutputStreamWriter( System.out, cs ) );
      try {
        final CommandContext context = new PrintWriterCommandContext( dc, command, args, pw );
        CommandExecutorManager.execute( context );
      } finally {
        pw.close();
      }
    } finally {
      dc.close();
    }

  }
}
