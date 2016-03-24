package de.ars.daojones.runtime.configuration.provider;

/**
 * An exception that is thrown when accessing the configuration fails.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public class ConfigurationException extends Exception {

  private static final long serialVersionUID = 1L;

  public ConfigurationException() {
    super();
  }

  public ConfigurationException( final String message, final Throwable t ) {
    super( message, t );
  }

  public ConfigurationException( final String message ) {
    super( message );
  }

  public ConfigurationException( final Throwable t ) {
    super( t );
  }

}
