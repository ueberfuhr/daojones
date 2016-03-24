package de.ars.daojones.connections;

import de.ars.daojones.cache.CacheValue;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.Identificator;
import de.ars.daojones.runtime.query.Query;

class FindByIdCommand<T extends Dao> extends AccessorCommand<T, T> {

  private static final long serialVersionUID = -8405770014287106059L;

  public FindByIdCommand( Class<T> theGenericClass, long cacheInterval,
      Query parameters, Accessor<T> accessor, Identificator id ) {
    super( theGenericClass, cacheInterval, parameters, accessor, id );
  }

  @SuppressWarnings( "unchecked" )
  public CacheValue<T> call() throws Exception {
    setExecuted( true );
    return CacheUtil.wrap( getAccessor().findById(
        ( Identificator ) getArguments()[0],
        ( Class[] ) getParameters().getBeanTypes() ), getCacheInterval() );
  }

}
