package de.ars.daojones.internal.cache.ws;

import java.util.logging.Level;

import de.ars.daojones.runtime.spi.cache.Cache;
import de.ars.daojones.runtime.spi.cache.CacheContext;
import de.ars.daojones.runtime.spi.cache.CacheException;
import de.ars.daojones.runtime.spi.cache.CacheFactory;

/**
 * Cache factory implementation that uses Websphere Dynacache. By default, the
 * object cache is used unless you specify the cache property <tt>
 * {@value PROPERTY_TYPE}</tt> with a value of <tt>{@value TYPE_COMMAND}
 * </tt>. In this case, the command cache is used.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class DynacacheFactory implements CacheFactory {

  /**
   * The name of the type property.
   */
  public static final String PROPERTY_TYPE = "type";
  /**
   * The value of the type property to use the command cache.
   */
  public static final String TYPE_COMMAND = "command";
  /**
   * The value of the type property to use the object cache.
   */
  public static final String TYPE_OBJECT = "object";

  private static final Messages bundle = Messages.create( "DynacacheFactory" );

  @Override
  public <K, V> Cache<K, V> createCache( final CacheContext<K, V> context ) throws CacheException {
    final String cacheType = CacheHelper.getProperty( context, DynacacheFactory.PROPERTY_TYPE,
            DynacacheFactory.TYPE_OBJECT );
    DynacacheFactory.bundle.log( Level.INFO, "cache.create", context.getApplication(), context.getConnectionId(),
            cacheType );
    if ( DynacacheFactory.TYPE_COMMAND.equalsIgnoreCase( cacheType ) ) {
      return new CommandCache<K, V>();
    } else {
      return new ObjectCache<K, V>( context );
    }
  }

}
