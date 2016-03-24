package de.ars.daojones.drivers.notes;

/**
 * This exception is thrown when a data handler for a given type cannot be
 * found.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class DataHandlerException extends Exception {

  private static final long serialVersionUID = 1L;
  private final DataHandler<?> handler;

  /**
   * Constructor.
   * 
   * @param handler
   *          the data handler
   */
  public DataHandlerException( final DataHandler<?> handler ) {
    super();
    this.handler = handler;
  }

  /**
   * Constructor.
   * 
   * @param handler
   *          the data handler
   * @param message
   *          the message
   */
  public DataHandlerException( final DataHandler<?> handler, final String message ) {
    super( message );
    this.handler = handler;
  }

  /**
   * Constructor.
   * 
   * @param handler
   *          the data handler
   * @param cause
   *          the cause
   */
  public DataHandlerException( final DataHandler<?> handler, final Throwable cause ) {
    super( cause );
    this.handler = handler;
  }

  /**
   * Constructor.
   * 
   * @param handler
   *          the data handler
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public DataHandlerException( final DataHandler<?> handler, final String message, final Throwable cause ) {
    super( message, cause );
    this.handler = handler;
  }

  /**
   * Returns the data handler.
   * 
   * @return the data handler
   */
  public DataHandler<?> getHandler() {
    return handler;
  }

}
