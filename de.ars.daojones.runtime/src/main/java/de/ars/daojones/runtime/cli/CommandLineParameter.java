package de.ars.daojones.runtime.cli;

/**
 * A parameter description for the command line.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public class CommandLineParameter {

  private final String name;
  private final String description;

  /**
   * Creates an instance.
   * 
   * @param name
   *          the command name
   * @param description
   *          the command description
   */
  public CommandLineParameter( final String name, final String description ) {
    super();
    this.name = name;
    this.description = description;
  }

  /**
   * Returns the command name.
   * 
   * @return the command name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the command description.
   * 
   * @return the command description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the value of the parameter.
   * 
   * @param args
   *          the command line parameters
   * @return the value of the parameter
   */
  public String getValue( final String... args ) {
    final String prefix = getName() + "=";
    for ( final String arg : args ) {
      if ( arg.startsWith( prefix ) ) {
        return arg.substring( prefix.length() );
      }
    }
    return null;
  }
}
