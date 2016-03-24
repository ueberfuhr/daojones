package de.ars.daojones.runtime.spi.database;

import java.security.GeneralSecurityException;

/**
 * A credential vault exception.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class CredentialVaultException extends GeneralSecurityException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a <code>CredentialVaultException</code> with no detail message.
   */
  public CredentialVaultException() {
    super();
  }

  /**
   * Creates a <code>CredentialVaultException</code> with the specified detail
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
  public CredentialVaultException( final String message, final Throwable cause ) {
    super( message, cause );
  }

  /**
   * Constructs a <code>CredentialVaultException</code> with the specified
   * detail message. A detail message is a String that describes this particular
   * exception.
   * 
   * @param msg
   *          the detail message.
   */
  public CredentialVaultException( final String msg ) {
    super( msg );
  }

  /**
   * Creates a <code>CredentialVaultException</code> with the specified cause
   * and a detail message of <tt>(cause==null ? null : cause.toString())</tt>
   * (which typically contains the class and detail message of <tt>cause</tt>).
   * 
   * @param cause
   *          the cause (which is saved for later retrieval by the
   *          {@link #getCause()} method). (A <tt>null</tt> value is permitted,
   *          and indicates that the cause is nonexistent or unknown.)
   * @since 1.5
   */
  public CredentialVaultException( final Throwable cause ) {
    super( cause );
  }

}
