package de.ars.daojones.connections;

import java.util.Collection;

import de.ars.daojones.cache.CacheValue;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.query.Query;

class FindAllCommand<T extends Dao> extends AccessorCommand<Collection<T>, T> {

  private static final long serialVersionUID = 6123680006473323740L;

  public FindAllCommand( Class<T> theGenericClass, long cacheInterval,
      Query parameters, Accessor<T> accessor, Object... arguments ) {
    super( theGenericClass, cacheInterval, parameters, accessor, arguments );
  }

  public CacheValue<Collection<T>> call() throws Exception {
    setExecuted( true );
    return CacheUtil.wrap( getAccessor().findAll( getParameters() ),
        getCacheInterval() );
  }

}
