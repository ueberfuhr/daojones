package de.ars.daojones.runtime.context;

/**
 * An exception that is thrown when injecting field values failed.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class InjectionException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Default Constructor.
   */
  public InjectionException() {
    super();
  }

  /**
   * Constructor.
   * 
   * @param message
   *          the message
   */
  public InjectionException( final String message ) {
    super( message );
  }

  /**
   * Constructor.
   * 
   * @param cause
   *          the cause
   */
  public InjectionException( final Throwable cause ) {
    super( cause );
  }

  /**
   * Constructor.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public InjectionException( final String message, final Throwable cause ) {
    super( message, cause );
  }

}
