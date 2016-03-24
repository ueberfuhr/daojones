package de.ars.daojones.runtime.cli;

/**
 * An exception during command execution.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public class CommandExecutionException extends Exception {

  private static final long serialVersionUID = 1L;

  private final CommandExecutor executor;
  private final CommandContext context;

  /**
   * Constructor.
   * 
   * @param executor
   *          the command executor
   * @param context
   *          the command context
   */
  public CommandExecutionException( final CommandExecutor executor, final CommandContext context ) {
    super();
    this.executor = executor;
    this.context = context;
  }

  /**
   * Constructor.
   * 
   * @param executor
   *          the command executor
   * @param context
   *          the command context
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public CommandExecutionException( final CommandExecutor executor, final CommandContext context, final String message,
          final Throwable cause ) {
    super( message, cause );
    this.executor = executor;
    this.context = context;
  }

  /**
   * Constructor.
   * 
   * @param executor
   *          the command executor
   * @param context
   *          the command context
   * @param message
   *          the message
   */
  public CommandExecutionException( final CommandExecutor executor, final CommandContext context, final String message ) {
    super( message );
    this.executor = executor;
    this.context = context;
  }

  /**
   * Constructor.
   * 
   * @param executor
   *          the command executor
   * @param context
   *          the command context
   * @param cause
   *          the cause
   */
  public CommandExecutionException( final CommandExecutor executor, final CommandContext context, final Throwable cause ) {
    super( cause );
    this.executor = executor;
    this.context = context;
  }

  /**
   * Returns the command executor.
   * 
   * @return the command executor
   */
  public CommandExecutor getExecutor() {
    return executor;
  }

  /**
   * Returns the command context
   * 
   * @return the command context
   */
  public CommandContext getContext() {
    return context;
  }

}
