package de.ars.daojones.runtime.spi.database;

/**
 * An exception that is thrown when building a connection from the configuration
 * fails.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 1.0
 */
public class ConnectionBuildException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * @see Exception#Exception()
   */
  public ConnectionBuildException() {
    super();
  }

  /**
   * @see Exception#Exception(String)
   */
  public ConnectionBuildException( final String message ) {
    super( message );
  }

  /**
   * @see Exception#Exception(Throwable)
   */
  public ConnectionBuildException( final Throwable cause ) {
    super( cause );
  }

  /**
   * @see Exception#Exception(String, Throwable)
   */
  public ConnectionBuildException( final String message, final Throwable cause ) {
    super( message, cause );
  }

}
