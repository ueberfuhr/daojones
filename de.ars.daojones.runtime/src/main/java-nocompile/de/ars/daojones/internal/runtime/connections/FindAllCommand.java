package de.ars.daojones.internal.runtime.connections;

import java.util.Collection;

import de.ars.daojones.runtime.connections.Accessor;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.spi.cache.CacheValue;

class FindAllCommand<T> extends AccessorCommand<Collection<T>, T> {

  private static final long serialVersionUID = 6123680006473323740L;

  public FindAllCommand( final ConnectionProvider connectionProvider,
      final Class<T> theGenericClass, final int timeout,
      final Query parameters, final Accessor<T> accessor,
      final Object... arguments ) {
    super( connectionProvider, theGenericClass, timeout, parameters, accessor,
        arguments );
  }

  public CacheValue<Collection<T>> call() throws Exception {
    setExecuted( true );
    return CacheUtil.wrap( getAccessor().findAll( getParameters() ),
        getTimeout() );
  }

}
