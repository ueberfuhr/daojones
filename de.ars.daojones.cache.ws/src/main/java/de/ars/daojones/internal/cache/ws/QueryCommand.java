package de.ars.daojones.internal.cache.ws;

import java.util.concurrent.Callable;
import java.util.logging.Level;

import com.ibm.websphere.command.CacheableCommandImpl;

import de.ars.daojones.runtime.spi.cache.CacheKey;
import de.ars.daojones.runtime.spi.cache.CacheValue;
import de.ars.daojones.runtime.spi.cache.DefaultCacheValue;

/**
 * A query command using WebSphere Command Cache.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <K>
 *          the key type
 * @param <V>
 *          the value type
 */
public class QueryCommand<K, V> extends CacheableCommandImpl {

  private static final long serialVersionUID = 1L;
  private static final Messages bundle = Messages.create( "QueryCommand" );

  private CacheKey<K> key;
  private CacheValue<V> value;
  // not stored into cache!
  private transient Callable<V> callable;

  /**
   * Returns the value.
   * 
   * @return the value
   */
  public CacheValue<V> getValue() {
    return value;
  }

  /**
   * Sets the key.
   * 
   * @param key
   *          the key
   */
  public void setKey( final CacheKey<K> key ) {
    this.key = key;
  }

  /**
   * Returns the key.
   * 
   * @return the key
   */
  public CacheKey<K> getKey() {
    return key;
  }

  /**
   * Returns the cache id.
   * 
   * @return the cache id
   */
  public long getCacheId() {
    try {
      return getKey().hashCode();
    } catch ( final Exception t ) {
      QueryCommand.bundle.log( Level.WARNING, "cache.id.error", QueryCommand.class.getName() );
      return -1;
    }
  }

  /**
   * Sets the callable.
   * 
   * @param callable
   *          the callable
   */
  public void setCallable( final Callable<V> callable ) {
    this.callable = callable;
  }

  @Override
  public boolean isReadyToCallExecute() {
    return null != key && null != callable;
  }

  @Override
  public void performExecute() throws Exception {
    final V result = callable.call();
    this.value = new DefaultCacheValue<V>( result );
  }

  @SuppressWarnings( "rawtypes" )
  @Override
  public boolean equals( final Object obj ) {
    return obj instanceof QueryCommand && Long.valueOf( getCacheId() ).equals( ( ( QueryCommand ) obj ).getCacheId() );
  }

  @Override
  public int hashCode() {
    return Long.valueOf( getCacheId() ).hashCode();
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer();
    if ( null != getValue() ) {
      sb.append( getValue().getData() );
      if ( !getValue().isValid() ) {
        sb.append( " (INVALID)" );
      }
      sb.append( ", found by " );
    }
    sb.append( getKey() );
    return sb.toString();
  }

}
