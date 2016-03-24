package de.ars.daojones.internal.cache.ws;

import java.io.IOException;
import java.io.NotSerializableException;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.ibm.websphere.cache.DistributedMap;
import com.ibm.websphere.cache.DynamicCacheAccessor;
import com.ibm.websphere.command.CommandException;

import de.ars.daojones.runtime.spi.cache.Cache;
import de.ars.daojones.runtime.spi.cache.CacheException;
import de.ars.daojones.runtime.spi.cache.CacheKey;
import de.ars.daojones.runtime.spi.cache.CacheValue;
import de.ars.daojones.runtime.spi.cache.DefaultCacheValue;

/**
 * An implementation of {@link Cache} using Websphere Command Cache. The command
 * cache has the restriction that using a single {@link QueryCommand} class
 * results in using a single cache instance. So only using the base cache is
 * supported.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <K>
 *          the key type
 * @param <V>
 *          the value type
 */
class CommandCache<K, V> implements Cache<K, V> {

  private static final Messages bundle = Messages.create( "CommandCache" );

  @Override
  public CacheValue<V> execute( final CacheKey<K> key, final Callable<V> callable ) throws CacheException {
    // check the DynaCache to be enabled
    if ( !DynamicCacheAccessor.isCachingEnabled() || !DynamicCacheAccessor.isServletCachingEnabled() ) {
      CommandCache.bundle.log( Level.WARNING, "dynacache.disabled" );
      try {
        final V result = callable.call();
        return new DefaultCacheValue<V>( result );
      } catch ( final CacheException e ) {
        throw e;
      } catch ( final Exception e ) {
        throw new CacheException( e );
      }
    }
    CommandCache.checkSerialization( key, "dynacache.keyNotSerializable" );
    final QueryCommand<K, V> cmd = new QueryCommand<K, V>();
    try {
      cmd.setKey( key );
      cmd.setCallable( callable );
      cmd.execute();
      CacheValue<V> value = cmd.getValue();
      if ( value != null && !value.isValid() ) {
        CommandCache.bundle.log( Level.FINER, "dynacache.invalid", key );
        final InitialContext context = new InitialContext();
        final DistributedMap cache = ( DistributedMap ) context.lookup( "services/cache/distributedmap" );
        if ( null != cache ) {
          cache.invalidate( cmd.getClass().getName() + ":getCacheId=" + cmd.getCacheId(), true );
        }
        final QueryCommand<K, V> cmd2 = new QueryCommand<K, V>();
        cmd2.setKey( key );
        cmd2.setCallable( callable );
        cmd2.execute();
        value = cmd2.getValue();
      }
      return value;
    } catch ( final CommandException e ) {
      CommandCache.checkSerialization( cmd.getValue(), "dynacache.valueNotSerializable" );
      throw new CacheException( CommandCache.bundle.get( "dynacache.error" ),
              null != e.getException() ? e.getException() : e );
    } catch ( final NamingException e ) {
      throw new CacheException( CommandCache.bundle.get( "dynacache.error" ), e );
    }
  }

  private static void checkSerialization( final Object s, final String message ) throws CacheException {
    try {
      final Object s2 = CacheHelper.deserialize( CacheHelper.serialize( s ) );
      CommandCache.bundle.log( Level.FINER, "dynacache.serializable", s, s2 );
    } catch ( final NotSerializableException e ) {
      throw new CacheException( CommandCache.bundle.get( message, s.getClass().getName() ), e );
    } catch ( final IOException e ) {
      throw new CacheException( CommandCache.bundle.get( message, s.getClass().getName() ), e );
    } catch ( final ClassNotFoundException e ) {
      throw new CacheException( CommandCache.bundle.get( message, s.getClass().getName() ), e );
    }
  }

}
