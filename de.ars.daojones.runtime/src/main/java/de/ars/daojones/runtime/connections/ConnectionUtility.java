package de.ars.daojones.runtime.connections;

import de.ars.daojones.internal.runtime.connections.ConnectionWrapper;
import de.ars.daojones.runtime.spi.database.DriverProvider;

/**
 * A utility class with helper methods concerning connections.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public final class ConnectionUtility {

  private ConnectionUtility() {
    super();
  }

  /**
   * Returns the unwrapped connection.
   * 
   * @param connection
   *          the wrapping connection
   * @return the unwrapped connection
   */
  public static <T> Connection<T> unwrap( final Connection<T> connection ) {
    Connection<T> result = connection;
    while ( result instanceof ConnectionWrapper ) {
      result = ( ( ConnectionWrapper<T> ) result ).getDelegate();
    }
    return result;
  }

  /**
   * Returns the driver's connection.
   * 
   * @param connection
   *          the connection
   * @return the driver's connection
   */
  public static <T> de.ars.daojones.runtime.spi.database.Connection<T> getDriver( final Connection<T> connection ) {
    final DriverProvider<T> driverProvider = ConnectionUtility.getDriverProvider( connection );
    return null != driverProvider ? driverProvider.getDriver() : null;
  }

  /**
   * Returns the driver provider.
   * 
   * @param con
   *          connection
   * @return the driver provider
   */
  @SuppressWarnings( "unchecked" )
  public static <T> DriverProvider<T> getDriverProvider( final Connection<T> con ) {
    final Connection<T> c1 = ConnectionUtility.unwrap( con );
    if ( c1 instanceof DriverProvider ) {
      return ( DriverProvider<T> ) c1;
    } else {
      return null;
    }
  }

}
