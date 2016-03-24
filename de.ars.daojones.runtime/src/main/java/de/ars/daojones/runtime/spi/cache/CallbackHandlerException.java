package de.ars.daojones.runtime.spi.cache;

import java.security.GeneralSecurityException;

/**
 * An exception that occurs when creating a callback handler failed.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class CallbackHandlerException extends GeneralSecurityException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a CallbackHandlerException with no detail message. A detail
   * message is a String that describes this particular exception.
   */
  public CallbackHandlerException() {
    super();
  }

  /**
   * Constructs a CallbackHandlerException with the specified detail message. A
   * detail message is a String that describes this particular exception.
   * 
   * @param msg
   *          the detail message.
   */
  public CallbackHandlerException( final String msg ) {
    super( msg );
  }

  /**
   * Creates a <code>CallbackHandlerException</code> with the specified detail
   * message and cause.
   * 
   * @param message
   *          the detail message (which is saved for later retrieval by the
   *          {@link #getMessage()} method).
   * @param cause
   *          the cause (which is saved for later retrieval by the
   *          {@link #getCause()} method). (A <tt>null</tt> value is permitted,
   *          and indicates that the cause is nonexistent or unknown.)
   */
  public CallbackHandlerException( final String message, final Throwable cause ) {
    super( message, cause );
  }

  /**
   * Creates a <code>CallbackHandlerException</code> with the specified cause
   * and a detail message of <tt>(cause==null ? null : cause.toString())</tt>
   * (which typically contains the class and detail message of <tt>cause</tt>).
   * 
   * @param cause
   *          the cause (which is saved for later retrieval by the
   *          {@link #getCause()} method). (A <tt>null</tt> value is permitted,
   *          and indicates that the cause is nonexistent or unknown.)
   */
  public CallbackHandlerException( final Throwable cause ) {
    super( cause );
  }

}
