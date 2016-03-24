package de.ars.daojones.internal.cache.ws;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.ibm.websphere.cache.DistributedMap;

import de.ars.daojones.runtime.spi.cache.Cache;
import de.ars.daojones.runtime.spi.cache.CacheContext;
import de.ars.daojones.runtime.spi.cache.CacheException;
import de.ars.daojones.runtime.spi.cache.CacheKey;
import de.ars.daojones.runtime.spi.cache.CacheValue;
import de.ars.daojones.runtime.spi.cache.DefaultCacheValue;
import de.ars.daojones.runtime.spi.cache.ExpiratingCacheValue;

/**
 * A cache implementation that uses the object cache.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <K>
 *          the key type
 * @param <V>
 *          the value type
 */
class ObjectCache<K, V> implements Cache<K, V> {

  private static final long SECONDS_FACTOR = 1000L;
  /**
   * The name of the instance property.
   */
  public static final String PROPERTY_CACHE_INSTANCE = "instance";
  /**
   * The name of the timeout property.
   */
  public static final String PROPERTY_TIMEOUT = "timeout";

  private static final Messages bundle = Messages.create( "ObjectCache" );

  private static final Map<Long, Map<Long, Object>> LOCKS = new HashMap<Long, Map<Long, Object>>();

  private final DistributedMap map;
  private final Integer timeout; // seconds
  private final Map<Long, Object> lockMap;

  /**
   * Creates an instance.
   * 
   * @param context
   *          the cache context
   * @throws NullPointerException
   *           if the distributed map is <tt>null</tt>
   * @throws CacheException
   *           if initializing the cache fails
   */
  public ObjectCache( final CacheContext<K, V> context ) throws NullPointerException, CacheException {
    super();
    final String cacheInstance = CacheHelper.getProperty( context, ObjectCache.PROPERTY_CACHE_INSTANCE,
            "services/cache/daojones/" + context.getApplication() + "/" + context.getConnectionId() );
    final String timeoutString = CacheHelper.getProperty( context, ObjectCache.PROPERTY_TIMEOUT, null );
    try {
      this.timeout = null != timeoutString ? NumberFormat.getInstance().parse( timeoutString ).intValue() : null;
      // JNDI Lookup
      final InitialContext ic = new InitialContext();
      try {
        this.map = ( DistributedMap ) ic.lookup( cacheInstance );
      } finally {
        ic.close();
      }
    } catch ( final NamingException e ) {
      throw new CacheException( e );
    } catch ( final ParseException e ) {
      throw new CacheException( ObjectCache.bundle.get( "error.timeout.format", context.getConnectionId(),
              timeoutString ), e );
    }
    if ( null == map ) {
      throw new NullPointerException( ObjectCache.bundle.get( "error.instance.notfound", cacheInstance ) );
    }
    // Initialize lock object
    final long key = map.hashCode();
    synchronized ( ObjectCache.LOCKS ) {
      Map<Long, Object> tmp = ObjectCache.LOCKS.get( key );
      if ( null == tmp ) {
        tmp = new HashMap<Long, Object>();
        ObjectCache.LOCKS.put( key, tmp );
      }
      this.lockMap = tmp;
    }
  }

  private Object getLock( final CacheKey<?> cacheKey ) {
    final long key = cacheKey.hashCode();
    synchronized ( lockMap ) {
      Object result = lockMap.get( key );
      if ( null == result ) {
        result = new Object();
        lockMap.put( key, result );
      }
      return result;
    }
  }

  /**
   * Returns the map.
   * 
   * @return the map
   */
  protected DistributedMap getMap() {
    return map;
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public CacheValue<V> execute( final CacheKey<K> key, final Callable<V> callable ) throws CacheException {
    synchronized ( getLock( key ) ) {
      final Object value = getMap().get( key );
      CacheValue<V> result;
      if ( value instanceof CacheValue ) {
        result = ( CacheValue<V> ) value;
        if ( !result.isValid() ) {
          getMap().remove( key );
          result = null;
        }
      } else {
        result = null;
      }
      if ( null == result ) {
        try {
          final V v = callable.call();
          result = new DefaultCacheValue<V>( v );
          if ( null != timeout ) {
            final long expirationDate = System.currentTimeMillis() + timeout * ObjectCache.SECONDS_FACTOR;
            result = new ExpiratingCacheValue<V>( result, expirationDate );
          }
        } catch ( final CacheException e ) {
          throw e;
        } catch ( final Exception e ) {
          throw new CacheException( e );
        }
        getMap().put( key, result );
      }
      return result;
    }
  }
}
