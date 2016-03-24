package de.ars.daojones.connections;

import de.ars.daojones.cache.CacheValue;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.query.Query;

class SimpleDaoCommand<T extends Dao> extends AccessorCommand<T, T> {

  private static final long serialVersionUID = -8926706166065648341L;

  public SimpleDaoCommand( Class<T> theGenericClass, long cacheInterval,
      Query parameters, Accessor<T> accessor, Dao dao ) {
    super( theGenericClass, cacheInterval, parameters, accessor, dao );
  }

  @SuppressWarnings( "unchecked" )
  public CacheValue<T> call() throws Exception {
    setExecuted( true );
    return CacheUtil.wrap( ( T ) getArguments()[0], getCacheInterval() );
  }

}
