package de.ars.daojones.internal.runtime.connections;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.query.Query;

class CacheableSearchResult<T> implements SearchResult<T>, Serializable {

  private static final long serialVersionUID = 1L;

  /*
   * Strategy 1: Read out whole result and cache the object array
   * --> Scalability is lost, because all results are read out before returning search result!

   * Strategy 2: Read out objects only if needed
   * --> Cacheability is lost, because we cannot cache the original search result (contains references to the database)
   * Strategy 3: Read out objects if needed, cache them in an array, cache query too
   * --> if more objects are read out after restoring from cache, the query must be repeated
   * --> Problem: Result is cached first (and serialized) before reading the objects
   * --> DO NOT USE
   * 
   * --> USE STRATEGY 1!
   */

  private final Query query;
  private final T[] cache;
  // not part of the cache!
  private transient SearchResult<T> delegate;
  private transient CachedConnectionImpl<T> connection;
  // Flag for Strategy 1
  private final boolean eager;

  @SuppressWarnings( "unchecked" )
  public CacheableSearchResult( final Class<T> beanType, final Query query, final SearchResult<T> delegate,
          final boolean eager ) throws DataAccessException {
    super();
    this.query = query;
    this.delegate = delegate;
    this.cache = ( T[] ) Array.newInstance( beanType, delegate.size() );
    this.eager = eager;
    if ( eager ) {
      delegate.getAsList().toArray( cache );
    }
  }

  void restoreConnection( final CachedConnectionImpl<T> connection ) {
    this.connection = connection;
  }

  private void restoreDelegate() throws DataAccessException {
    // use superfindall to avoid cache!
    if ( null == delegate ) {
      if ( null == connection ) {
        // Should not occur!
        throw new DataAccessException(
                "No connection set for cached search result! Please inform the DaoJones Development Team!" );
      } else {
        delegate = connection.superFindAll( query );
      }
    }
  }

  private T read( final int index ) throws DataAccessException {
    T result = cache[index];
    if ( !eager && null == result ) {
      restoreDelegate();
      result = delegate.getAsList( index, index ).get( 0 );
      cache[index] = result;
    }
    return result;
  }

  @Override
  public int size() {
    return cache.length;
  }

  @Override
  public Iterator<T> iterator() {
    try {
      return getAsList().iterator();
    } catch ( final DataAccessException e ) {
      throw new RuntimeException( e );
    }
  }

  @Override
  public List<T> getAsList() throws DataAccessException {
    // read all
    if ( !eager ) {
      for ( int i = 0; i < cache.length; i++ ) {
        read( i );
      }
    }
    return Collections.unmodifiableList( Arrays.asList( cache ) );
  }

  @Override
  public List<T> getAsList( final int beginIndex, final int endIndex ) throws DataAccessException,
          IndexOutOfBoundsException {
    // read all
    if ( !eager ) {
      for ( int i = beginIndex; i <= endIndex; i++ ) {
        read( i );
      }
    }
    return Collections.unmodifiableList( Arrays.asList( cache ).subList( beginIndex, endIndex ) );
  }

  @Override
  public void close() throws DataAccessException {
    if ( null != delegate ) {
      delegate.close();
      delegate = null;
    }
    // do not delete array or query, they must be cached!
  }
}