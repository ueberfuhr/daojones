package de.ars.daojones.internal.runtime.connections;

import java.io.Serializable;
import java.util.concurrent.Callable;

import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.connections.ConnectionMetaData;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.spi.cache.Cache;
import de.ars.daojones.runtime.spi.cache.CacheException;
import de.ars.daojones.runtime.spi.cache.CacheKey;
import de.ars.daojones.runtime.spi.cache.CacheValue;
import de.ars.daojones.runtime.spi.cache.CompositeCacheKey;
import de.ars.daojones.runtime.spi.database.Connection;
import de.ars.daojones.runtime.spi.database.ConnectionContext;
import de.ars.daojones.runtime.spi.database.DatabaseEntry;

public class CachedConnectionImpl<T> extends ConnectionImpl<T> {

  private final CacheBuilder cacheBuilder;

  public CachedConnectionImpl( final ConnectionContext<T> context, final Connection<T> driver,
          final CacheBuilder cacheBuilder ) {
    super( context, driver );
    this.cacheBuilder = cacheBuilder;
  }

  protected CacheBuilder getCacheBuilder() {
    return cacheBuilder;
  }

  /*
   * OKAY, LET'S BEGIN HERE WITH THE CONCEPT:
   * ========================================
   * 
   * The following caches are used:
   *  - <Application, Bean Identificator (Serializable), Type> --> Bean
   *  - <Application, Query> --> Beans
   */

  /*
   * FIRST, TYPE DEFINITIONS
   *  - application id might be necessary in case of shared caches!
   */

  private Serializable toId( final String application, final Object id ) {
    if ( null == id ) {
      return null;
    } else if ( id instanceof Identificator ) {
      return ( ( Identificator ) id ).getId( application );
    } else if ( id instanceof Serializable ) {
      return ( Serializable ) id;
    } else {
      return id.toString();
    }
  }

  private IdKey createIdKey( final Object id ) {
    return new IdKey( getContext().getApplicationId(), toId( getContext().getApplicationId(), id ), getContext()
            .getBeanType().getName() );
  }

  // Key class to identify a bean
  // class must be declared as static to avoid compiler problems (Maven)
  private static class IdKey extends CompositeCacheKey {

    private static final long serialVersionUID = 1L;

    public IdKey( final String application, final Serializable id, final String type ) {
      super( application, id, type );
    }

    @Override
    public String toString() {
      final Serializable[] data = getData();
      final Serializable application = data[0];
      final Serializable id = data[1];
      final Serializable type = data[2];
      final StringBuilder builder = new StringBuilder();
      builder.append( "ID Key [application=" ).append( application ).append( ", id=" ).append( id ).append( ", type=" )
              .append( type ).append( "]" );
      return builder.toString();
    }
  }

  private static class QueryKey extends CompositeCacheKey {

    private static final long serialVersionUID = 1L;

    public QueryKey( final String application, final Query query ) {
      super( application, query );
    }

    @Override
    public String toString() {
      final Serializable[] data = getData();
      final Serializable application = data[0];
      final Serializable query = data[1];
      return "QUERY Key [application=" + application + ", query=" + query + "]";
    }

  }

  /*
   * SECOND, CACHE DEFINITIONS
   */

  private final Object idCacheLock = new Object();
  private Cache<IdKey, T> idCache;

  private Cache<IdKey, T> getIdCache() throws CacheException {
    synchronized ( idCacheLock ) {
      if ( null == idCache ) {
        idCache = getCacheBuilder().createCache( IdKey.class, getContext().getBeanType() );
      }
    }
    return idCache;
  }

  private final Object queryCacheLock = new Object();
  private Cache<QueryKey, CacheableSearchResult<T>> queryCache;

  @SuppressWarnings( { "unchecked", "rawtypes" } )
  private Cache<QueryKey, CacheableSearchResult<T>> getQueryCache() throws CacheException {
    synchronized ( queryCacheLock ) {
      if ( null == queryCache ) {
        queryCache = ( Cache ) getCacheBuilder().createCache( QueryKey.class, CacheableSearchResult.class );
      }
    }
    return queryCache;
  }

  /*
   * THIRD, CACHE USAGE
   */

  private T superFindById( final Object id ) throws DataAccessException {
    return super.findById( id );
  }

  @SuppressWarnings( { "rawtypes", "unchecked" } )
  private T accessIdCache( final Object id, final Callable<T> command ) throws DataAccessException {
    try {
      final Cache<IdKey, T> idCache = getIdCache();
      final IdKey key = createIdKey( id );
      final CacheValue<T> value = idCache.execute( ( CacheKey ) key, command );
      return value.getData();
    } catch ( final CacheException e ) {
      throw new DataAccessException( e );
    }
  }

  @Override
  public T findById( final Object id ) throws DataAccessException {
    return accessIdCache( id, new Callable<T>() {
      @Override
      public T call() throws Exception {
        return superFindById( id );
      }

    } );
  }

  private T superConvert( final DatabaseEntry t, final Class<? extends T> type ) {
    return super.convert( t, type );
  }

  @Override
  protected T convert( final DatabaseEntry t, final Class<? extends T> type ) {
    try {
      return accessIdCache( toId( getContext().getApplicationId(), t.getIdentificator() ), new Callable<T>() {

        @Override
        public T call() throws Exception {
          return superConvert( t, type );
        }
      } );
    } catch ( final DataAccessException e ) {
      throw new RuntimeException( e );
    }
  }

  @SuppressWarnings( { "rawtypes", "unchecked" } )
  private SearchResult<T> accessQueryCache( final Query query, final Callable<CacheableSearchResult<T>> command )
          throws DataAccessException {
    try {
      final Cache<QueryKey, CacheableSearchResult<T>> queryCache = getQueryCache();
      final QueryKey key = new QueryKey( getContext().getApplicationId(), query );
      final CacheValue<CacheableSearchResult<T>> value = queryCache.execute( ( CacheKey ) key, command );
      final CacheableSearchResult data = value.getData();
      data.restoreConnection( this );
      return data;
    } catch ( final CacheException e ) {
      throw new DataAccessException( e );
    }
  }

  SearchResult<T> superFindAll( final Query query ) throws DataAccessException {
    return super.findAll( query );
  }

  @Override
  public SearchResult<T> findAll( final Query query ) throws DataAccessException {
    return accessQueryCache( query, new Callable<CacheableSearchResult<T>>() {

      @Override
      public CacheableSearchResult<T> call() throws Exception {
        return new CacheableSearchResult<T>( getContext().getBeanType(), query, superFindAll( query ), true );
      }
    } );
  }

  @Override
  protected void doEventCloseConnection() throws DataAccessException {
    // TODO close Cache?
    super.doEventCloseConnection();
  }

  /*
   * For metadata: 
   *  - not allowed beans within a set?
   *  - <Beans> --> <SingleMetadata>
   *  - if a single is not allowed -> all others are not allowed
   */
  @Override
  public ConnectionMetaData<T> getMetaData() {
    // TODO Cache Metadata queries
    return super.getMetaData();
  }

}
