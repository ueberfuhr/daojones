package de.ars.daojones.runtime.query;

/**
 * This exception is thrown when resolving columns or tables by the
 * {@link ColumnResolver} failed.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ColumnResolvingException extends Exception {

  private static final long serialVersionUID = -3964818553385647336L;

  /**
   * @see Exception#Exception()
   */
  public ColumnResolvingException() {
    super();
  }

  /**
   * @see Exception#Exception(String)
   * @param message
   */
  public ColumnResolvingException( String message ) {
    super( message );
  }

  /**
   * @see Exception#Exception(Throwable)
   * @param cause
   */
  public ColumnResolvingException( Throwable cause ) {
    super( cause );
  }

  /**
   * @see Exception#Exception(String, Throwable)
   * @param message
   * @param cause
   */
  public ColumnResolvingException( String message, Throwable cause ) {
    super( message, cause );
  }

}
