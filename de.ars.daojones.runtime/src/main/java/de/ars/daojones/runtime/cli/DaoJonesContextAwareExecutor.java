package de.ars.daojones.runtime.cli;

import de.ars.daojones.internal.runtime.utilities.Messages;

/**
 * A super class for all command executors that need to access the DaoJones
 * Context. If the command is executed, it throws an exception when no DaoJones
 * Context is available.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public abstract class DaoJonesContextAwareExecutor implements CommandExecutor {

  private static final Messages nls = Messages.create( "cli.DaoJonesContextAwareExecutor" );

  @Override
  public void execute( final CommandContext context ) throws CommandExecutionException {
    if ( null == context.getDaoJonesContext() ) {
      throw new CommandExecutionException( this, context, DaoJonesContextAwareExecutor.nls.get( "error.nocontext" ) );
    }
  }

}
