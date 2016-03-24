package de.ars.daojones.internal.runtime.cache;

import java.io.IOException;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import de.ars.daojones.internal.runtime.utilities.SerializationHelper;
import de.ars.daojones.runtime.spi.cache.Cache;
import de.ars.daojones.runtime.spi.cache.CacheAdapter;
import de.ars.daojones.runtime.spi.cache.CacheContext;
import de.ars.daojones.runtime.spi.cache.CacheException;
import de.ars.daojones.runtime.spi.cache.CacheFactory;
import de.ars.daojones.runtime.spi.cache.CacheKey;
import de.ars.daojones.runtime.spi.cache.CacheValue;
import de.ars.daojones.runtime.spi.cache.DefaultCacheValue;

public class MemoryCacheFactory implements CacheFactory, Serializable {

  private static final String TIMEOUT_PROPERTY = "timeout";
  private static final int DEFAULT_TIMEOUT = 600;
  private static final long serialVersionUID = 1L;

  private class TimestampedKey<K> {
    private final CacheKey<K> delegate;
    private final long timestamp = System.currentTimeMillis();

    public TimestampedKey( final CacheKey<K> delegate ) {
      super();
      this.delegate = delegate;
    }

    public long getTimestamp() {
      return timestamp;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + ( ( delegate == null ) ? 0 : delegate.hashCode() );
      return result;
    }

    @Override
    public boolean equals( final Object obj ) {
      if ( this == obj ) {
        return true;
      }
      if ( obj == null ) {
        return false;
      }
      if ( getClass() != obj.getClass() ) {
        return false;
      }
      @SuppressWarnings( "unchecked" )
      final TimestampedKey<K> other = ( TimestampedKey<K> ) obj;
      if ( !getOuterType().equals( other.getOuterType() ) ) {
        return false;
      }
      if ( delegate == null ) {
        if ( other.delegate != null ) {
          return false;
        }
      } else if ( !delegate.equals( other.delegate ) ) {
        return false;
      }
      return true;
    }

    private MemoryCacheFactory getOuterType() {
      return MemoryCacheFactory.this;
    }

    @Override
    public String toString() {
      final StringBuilder builder = new StringBuilder();
      builder.append( "CacheKey [delegate=" ).append( delegate ).append( ", timestamp=" ).append( timestamp )
              .append( "]" );
      return builder.toString();
    }

  }

  @Override
  public <K, V> Cache<K, V> createCache( final CacheContext<K, V> context ) throws CacheException {
    final String timeout$ = context.getProperties().getProperty( MemoryCacheFactory.TIMEOUT_PROPERTY );
    try {
      final long timeout = Math.max( null != timeout$ ? NumberFormat.getNumberInstance().parse( timeout$ ).intValue()
              : MemoryCacheFactory.DEFAULT_TIMEOUT, 0 ) * 1000l;
      return new CacheAdapter<K, V>() {
        private final Map<TimestampedKey<K>, byte[]> map = new HashMap<TimestampedKey<K>, byte[]>();
        private final Map<TimestampedKey<K>, TimestampedKey<K>> map2 = new HashMap<TimestampedKey<K>, TimestampedKey<K>>();

        @SuppressWarnings( "unchecked" )
        @Override
        public CacheValue<V> execute( final CacheKey<K> key, final Callable<V> callable ) throws CacheException {
          // Serialize objects to get rid of transient fields
          try {
            final CacheValue<V> result;
            synchronized ( map ) {
              final TimestampedKey<K> tKey = new TimestampedKey<K>( key );
              final TimestampedKey<K> currentKey = map2.get( tKey );
              if ( currentKey == null || ( tKey.getTimestamp() - currentKey.getTimestamp() ) > timeout ) {
                map.remove( tKey );
                map2.remove( tKey );
                result = super.execute( key, callable );
                map.put( tKey, SerializationHelper.serialize( result.getData() ) );
                map2.put( tKey, tKey );
              } else {
                result = new DefaultCacheValue<V>( ( V ) SerializationHelper.deserialize( map.get( tKey ) ) );
              }
            }
            return result;
          } catch ( final IOException e ) {
            throw new CacheException( e );
          } catch ( final ClassNotFoundException e ) {
            throw new CacheException( e );
          }
        }

      };
    } catch ( final ParseException e ) {
      throw new CacheException( e );
    }
  }
}
