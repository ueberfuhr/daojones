package de.ars.daojones.runtime.cli;

/**
 * An executor of a command.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public interface CommandExecutor {

  /**
   * Executes the command.
   * 
   * @param context
   *          the interface to the command line
   * @throws CommandExecutionException
   *           if an exception occurs during command execution.
   */
  public void execute( CommandContext context ) throws CommandExecutionException;

  /**
   * Returns the name of this executor that is the name of this command.
   * 
   * @return the name
   */
  public String getName();

  /**
   * Returns a description of this executor.
   * 
   * @return the executor
   */
  public String getDescription();

  /**
   * Returns the supported command line parameters.
   * 
   * @return the command line parameters
   */
  public CommandLineParameter[] getParameters();

}
