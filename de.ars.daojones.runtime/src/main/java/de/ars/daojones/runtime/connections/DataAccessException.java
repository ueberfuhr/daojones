package de.ars.daojones.runtime.connections;

import java.io.IOException;

/**
 * An exception that occurs when accessing the database failed.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 1.2
 */
public class DataAccessException extends IOException {

  private static final long serialVersionUID = 5621364630433706411L;

  /**
   * Creates an instance.
   */
  public DataAccessException() {
    super();
  }

  /**
   * Creates an instance.
   * 
   * @param message
   *          the message
   */
  public DataAccessException( final String message ) {
    super( message );
  }

  /**
   * Creates an instance.
   * 
   * @param cause
   *          the nested exception
   */
  public DataAccessException( final Throwable cause ) {
    super( cause );
  }

  /**
   * Creates an instance.
   * 
   * @param message
   *          the message
   * @param cause
   *          the nested exception
   */
  public DataAccessException( final String message, final Throwable cause ) {
    super( message, cause );
  }

}
