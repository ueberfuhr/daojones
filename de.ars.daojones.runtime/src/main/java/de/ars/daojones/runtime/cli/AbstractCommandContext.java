package de.ars.daojones.runtime.cli;

import de.ars.daojones.runtime.context.DaoJonesContext;

/**
 * An abstract implementation of the command context providing common
 * functionality.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public abstract class AbstractCommandContext implements CommandContext {

  private final DaoJonesContext context;
  private final String command;
  private final String[] arguments;

  /**
   * Creates an instance.
   * 
   * @param context
   *          the context
   * @param command
   *          the command
   * @param arguments
   *          the arguments
   */
  public AbstractCommandContext( final DaoJonesContext context, final String command, final String[] arguments ) {
    super();
    this.context = context;
    this.command = command;
    this.arguments = arguments;
  }

  @Override
  public DaoJonesContext getDaoJonesContext() {
    return context;
  }

  @Override
  public String getCommand() {
    return command;
  }

  @Override
  public String[] getParameters() {
    return arguments;
  }

  @Override
  public String getCommand( final CommandExecutor executor ) {
    return executor.getName();
  }

}
