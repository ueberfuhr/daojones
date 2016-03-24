package de.ars.daojones.internal.runtime.connections;

import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionMetaData;
import de.ars.daojones.runtime.connections.ConnectionProvider;

/**
 * A wrapper for a connection that delegates to an internal delegate. Clients
 * may subclass this class.
 * 
 * @param <T>
 *          The DaoJones DAO bean that can be read or written with this
 *          connection.
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 1.1.0
 */
public class ConnectionWrapper<T> extends AccessorWrapper<T> implements Connection<T> {

  /**
   * Creates an instance.
   * 
   * @param connection
   *          the connection
   */
  public ConnectionWrapper( final Connection<T> connection ) {
    super( connection );
  }

  /**
   * Returns the delegate connection.
   * 
   * @return the delegate connection
   */
  @Override
  public Connection<T> getDelegate() {
    return ( Connection<T> ) super.getDelegate();
  }

  /**
   * @return
   * @see de.ars.daojones.runtime.connections.Connection#getModel()
   */
  @Override
  public ConnectionModel getModel() {
    return getDelegate().getModel();
  }

  /**
   * @return
   * @see de.ars.daojones.runtime.connections.Connection#getMetaData()
   */
  @Override
  public ConnectionMetaData<T> getMetaData() {
    return getDelegate().getMetaData();
  }

  /**
   * @return
   * @see de.ars.daojones.runtime.connections.Connection#getConnectionProvider()
   */
  @Override
  public ConnectionProvider getConnectionProvider() {
    return getDelegate().getConnectionProvider();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append( "ConnectionWrapper [connection=" ).append( getDelegate() ).append( "]" );
    return builder.toString();
  }

}
