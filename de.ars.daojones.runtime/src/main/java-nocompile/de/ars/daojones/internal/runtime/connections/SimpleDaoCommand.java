package de.ars.daojones.internal.runtime.connections;

import de.ars.daojones.runtime.connections.Accessor;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.spi.cache.CacheValue;

class SimpleDaoCommand<T> extends AccessorCommand<T, T> {

  private static final long serialVersionUID = -8926706166065648341L;

  public SimpleDaoCommand( final ConnectionProvider connectionProvider,
      final Class<T> theGenericClass, final int timeout,
      final Query parameters, final Accessor<T> accessor, final Dao dao ) {
    super( connectionProvider, theGenericClass, timeout, parameters, accessor,
        dao );
  }

  @SuppressWarnings( "unchecked" )
  public CacheValue<T> call() throws Exception {
    setExecuted( true );
    return CacheUtil.wrap( ( T ) getArguments()[0], getTimeout() );
  }

}
