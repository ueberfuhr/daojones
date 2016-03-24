package de.ars.daojones.internal.runtime.connections;

import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.cache.Cache;
import de.ars.daojones.runtime.spi.cache.CacheValue;
import de.ars.daojones.runtime.spi.cache.DefaultCacheValue;
import de.ars.daojones.runtime.spi.cache.ExpiratingCacheValue;

/**
 * A helper class providing methods for using the {@link Cache} API.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
abstract class CacheUtil {

  public static <X> X unwrap( final CacheValue<X> value ) {
    return null == value ? null : value.getData();
  }

  public static <X> X restore( final X dao, final ConnectionProvider provider ) throws DataAccessException {
    if ( null != dao ) {
      BeanFactory.restore( dao, provider );
    }
    return dao;
  }

  public static <X> CacheValue<X> wrap( final X value, final int timeout ) {
    if ( null == value ) {
      return null;
    }
    CacheValue<X> result = new DefaultCacheValue<X>( value );
    if ( timeout > 0 ) {
      result = new ExpiratingCacheValue<X>( result, System.currentTimeMillis() + timeout * 1000l );
    }
    return result;
  }

}
