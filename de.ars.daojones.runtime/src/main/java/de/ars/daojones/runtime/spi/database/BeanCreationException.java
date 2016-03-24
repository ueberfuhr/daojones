package de.ars.daojones.runtime.spi.database;

/**
 * An exception that occurs when instantiating a DaoJones bean failed.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public class BeanCreationException extends Exception {

  private static final long serialVersionUID = 8832499422143792519L;

  /**
   * Creates an instance.
   */
  public BeanCreationException() {
    super();
  }

  /**
   * Creates an instance.
   * 
   * @param message
   *          the message
   */
  public BeanCreationException( final String message ) {
    super( message );
  }

  /**
   * Creates an instance.
   * 
   * @param cause
   *          the nested exception
   */
  public BeanCreationException( final Throwable cause ) {
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
  public BeanCreationException( final String message, final Throwable cause ) {
    super( message, cause );
  }

}
