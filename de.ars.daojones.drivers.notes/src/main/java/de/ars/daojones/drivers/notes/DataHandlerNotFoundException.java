package de.ars.daojones.drivers.notes;

/**
 * This exception is thrown when a data handler for a given type cannot be
 * found.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class DataHandlerNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;
  private final DataHandlerProvider provider;
  private final Class<?> type;

  /**
   * Constructor.
   * 
   * @param provider
   *          the data handler provider
   * @param type
   *          the data type
   */
  public DataHandlerNotFoundException( final DataHandlerProvider provider, final Class<?> type ) {
    super();
    this.provider = provider;
    this.type = type;
  }

  /**
   * Constructor.
   * 
   * @param provider
   *          the data handler provider
   * @param type
   *          the data type
   * @param message
   *          the message
   */
  public DataHandlerNotFoundException( final DataHandlerProvider provider, final Class<?> type, final String message ) {
    super( message );
    this.provider = provider;
    this.type = type;
  }

  /**
   * Constructor.
   * 
   * @param provider
   *          the data handler provider
   * @param type
   *          the data type
   * @param cause
   *          the cause
   */
  public DataHandlerNotFoundException( final DataHandlerProvider provider, final Class<?> type, final Throwable cause ) {
    super( cause );
    this.provider = provider;
    this.type = type;
  }

  /**
   * Constructor.
   * 
   * @param provider
   *          the data handler provider
   * @param type
   *          the data type
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public DataHandlerNotFoundException( final DataHandlerProvider provider, final Class<?> type, final String message,
          final Throwable cause ) {
    super( message, cause );
    this.provider = provider;
    this.type = type;
  }

  /**
   * Returns the data handler provider.
   * 
   * @return the provider the data handler provider
   */
  public DataHandlerProvider getProvider() {
    return provider;
  }

  /**
   * Returns the data type.
   * 
   * @return the data type
   */
  public Class<?> getType() {
    return type;
  }

}
