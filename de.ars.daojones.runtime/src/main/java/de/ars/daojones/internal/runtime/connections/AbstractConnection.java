package de.ars.daojones.internal.runtime.connections;

import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.spi.database.ConnectionContext;

/**
 * A common super class for a connection implementation. Drivers should create
 * subclasses from this supertype.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 * @param <T>
 *          the bean type
 */
public abstract class AbstractConnection<T> extends AbstractAccessor<T> implements Connection<T> {

  public AbstractConnection( final ConnectionContext<T> context ) {
    super( context );
  }

  @Override
  public ConnectionModel getModel() {
    return getContext().getConnectionModel();
  }

  @Override
  public ConnectionProvider getConnectionProvider() {
    return getContext().getConnectionProvider();
  }

  @Override
  protected final Connection<T> getConnection() {
    return this;
  }

}
