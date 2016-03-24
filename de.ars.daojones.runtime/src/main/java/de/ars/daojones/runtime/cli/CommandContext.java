package de.ars.daojones.runtime.cli;

import de.ars.daojones.runtime.context.DaoJonesContext;

/**
 * The context information of a command execution.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public interface CommandContext {

  /**
   * Returns the context.
   * 
   * @return the context
   */
  public DaoJonesContext getDaoJonesContext();

  /**
   * Returns the command of the given executor.
   * 
   * @param executor
   *          the executor
   * @return the command within the command line
   */
  public String getCommand( CommandExecutor executor );

  /**
   * Returns the command of the current execution.
   * 
   * @return the command of the current execution
   */
  public String getCommand();

  /**
   * Returns the parameters of the current execution. To read out a command line
   * parameter, use the {@link CommandLineParameter#getValue(String...)} method
   * with the result of this method as a parameter.
   * 
   * @return the parameters of the current execution
   * @see CommandLineParameter#getValue(String...)
   */
  public String[] getParameters();

  /**
   * Prints an object to the outputstream
   * 
   * @param o
   *          the object to be printed
   */
  public void print( Object o );

  /**
   * Prints an empty line to the outputstream
   */
  public void println();

  /**
   * Prints an object to the output medium (appended with newline character).
   * <p>
   * If running on the target environment the user is prompted with '--more' if
   * more than the configured number of lines have been printed without user
   * prompt. That way the user of the program has control over the scrolling.
   * <p>
   * For this to work properly you should not embedded "\n" etc. into the
   * string.
   * 
   * @param o
   *          the object to be printed
   */
  public void println( Object o );

  /**
   * Print a stack trace including nested exceptions.
   * 
   * @param t
   *          The offending exception
   */
  public void printStackTrace( Throwable t );

}
