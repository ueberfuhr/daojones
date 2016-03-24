package de.ars.daojones.internal.runtime.connections;

import de.ars.daojones.runtime.connections.Accessor;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.spi.cache.CacheValue;

class FindByIdCommand<T> extends AccessorCommand<T, T> {

  private static final long serialVersionUID = -8405770014287106059L;

  public FindByIdCommand( final ConnectionProvider connectionProvider,
      final Class<T> theGenericClass, final int timeout,
      final Query parameters, final Accessor<T> accessor, final Identificator id ) {
    super( connectionProvider, theGenericClass, timeout, parameters, accessor,
        id );
  }

  @SuppressWarnings( "unchecked" )
  public CacheValue<T> call() throws Exception {
    setExecuted( true );
    return CacheUtil.wrap(
        getAccessor().findById( ( Identificator ) getArguments()[0],
            ( Class[] ) getParameters().getBeanTypes() ), getTimeout() );
  }

}
