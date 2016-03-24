package de.ars.daojones.connections;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.ars.daojones.cache.Cache;
import de.ars.daojones.cache.CacheException;
import de.ars.daojones.cache.CacheFactory;
import de.ars.daojones.cache.CacheValue;
import de.ars.daojones.cache.DefaultCacheKey;
import de.ars.daojones.events.DataAccessListener;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.Identificator;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.query.SearchCriterion;

class CachedAccessor<T extends Dao> implements Accessor<T> {

  private static final Logger logger = Logger.getLogger( CachedAccessor.class
      .getName() );

  private static final class FindByIdKey implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Identificator id;
    private final Class<?>[] subclasses;

    public FindByIdKey( Identificator id, Class<?>[] subclasses ) {
      super();
      this.id = id;
      this.subclasses = subclasses;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
      result = prime * result + Arrays.hashCode( subclasses );
      return result;
    }

    @Override
    public boolean equals( Object obj ) {
      if ( this == obj )
        return true;
      if ( obj == null )
        return false;
      if ( getClass() != obj.getClass() )
        return false;
      FindByIdKey other = ( FindByIdKey ) obj;
      if ( id == null ) {
        if ( other.id != null )
          return false;
      } else if ( !id.equals( other.id ) )
        return false;
      if ( !Arrays.equals( subclasses, other.subclasses ) )
        return false;
      return true;
    }
  }

  private final Accessor<T> accessor;
  private final long cacheInterval;
  private final Cache<Identificator, T> objectByIdentificatorCache;
  private final Cache<FindByIdKey, T> findByIdentificatorCache;
  private final Cache<Object[], Collection<T>> findAllByQueryCache;
  private final Class<T> theGenericClass;

  @SuppressWarnings( "unchecked" )
  public CachedAccessor( final Accessor<T> accessor, Class<T> theGenericClass,
      final long cacheInterval, final CacheConfiguration cacheConfiguration )
      throws CacheException {
    super();
    this.accessor = accessor;
    this.cacheInterval = cacheInterval;
    this.theGenericClass = theGenericClass;
    // use cacheConfiguration;
    final CacheFactory cf = cacheConfiguration.getCacheFactory();
    this.objectByIdentificatorCache = cf.createCache( Identificator.class,
        theGenericClass );
    this.findByIdentificatorCache = cf.createCache( FindByIdKey.class,
        theGenericClass );
    this.findAllByQueryCache = ( Cache ) cf.createCache( Object[].class,
        Collection.class );
  }

  public Connection<T> getConnection() {
    return this.accessor.getConnection();
  }

  /*
   * ************************************* C A C H E D M E T H O D S *
   * ************************************
   */

  private T replaceCachedObject( T dao ) throws CacheException {
    if ( null == dao || null == dao.getDataObject() )
      return dao;
    final Identificator id = dao.getDataObject().getIdentificator();
    return CacheUtil.unwrap( objectByIdentificatorCache.put(
        new DefaultCacheKey<Identificator>( id ), new SimpleDaoCommand<T>(
            theGenericClass, cacheInterval, null, null, dao ) ) );
  }

  @SuppressWarnings( "unchecked" )
  private Collection<T> replaceCachedObjects( Collection<T> objects )
      throws CacheException {
    if ( null == objects )
      return null;
    Collection<T> result;
    try {
      result = objects.getClass().newInstance();
    } catch ( Throwable t ) {
      if ( logger.isLoggable( Level.WARNING ) )
        logger.log( Level.WARNING, "Could not re-create collection of type "
            + objects.getClass() + "! Use HashSet instead." );
      result = new HashSet<T>();
    }
    for ( final T dao : objects ) {
      if ( null == dao ) {
        result.add( null );
        continue;
      }
      result.add( replaceCachedObject( dao ) );
    }
    return result;
  }

  /**
   * @see de.ars.daojones.connections.Accessor#find(SearchCriterion...)
   * @deprecated Use {@link #find(Query)} instead.
   */
  @Deprecated
  public T find( SearchCriterion... criterions ) throws DataAccessException {
    return find( Query.create( criterions ) );
  }

  /**
   * @see de.ars.daojones.connections.Accessor#find()
   */
  public T find() throws DataAccessException {
    return find( Query.create() );
  }

  /**
   * @see de.ars.daojones.connections.Accessor#find(Query)
   */
  public T find( Query query ) throws DataAccessException {
    final Collection<T> result = findAll( query.withCountOfResults( 1 ) );
    return ( result.isEmpty() ? null : result.iterator().next() );
  }

  /**
   * @see de.ars.daojones.connections.Accessor#findAll()
   */
  public Collection<T> findAll() throws DataAccessException {
    return findAll( Query.create() );
  }

  /**
   * @see de.ars.daojones.connections.Accessor#findAll(SearchCriterion...)
   * @deprecated Use {@link #findAll(Query)} instead.
   */
  @Deprecated
  public Collection<T> findAll( SearchCriterion... criterions )
      throws DataAccessException {
    return findAll( Query.create( criterions ) );
  }

  private static void handleCacheException( CacheException e )
      throws DataAccessException {
    if ( logger.isLoggable( Level.SEVERE ) )
      logger.log( Level.SEVERE, "Error using the cache.", e );
  }

  /**
   * @see de.ars.daojones.connections.Accessor#findAll(Query)
   */
  public Collection<T> findAll( Query params ) throws DataAccessException {
    final FindAllCommand<T> command = new FindAllCommand<T>( theGenericClass,
        cacheInterval, params, accessor );
    try {
      final CachedQuery query = new CachedQuery( params );
      final CacheValue<Collection<T>> value = this.findAllByQueryCache.put(
          query, command );
      final Collection<T> unwrappedValue = CacheUtil.unwrap( value );
      final Collection<T> originalValues = command.isExecuted() ? replaceCachedObjects( unwrappedValue )
          : unwrappedValue;
      return Collections.unmodifiableCollection( originalValues );
    } catch ( CacheException e ) {
      handleCacheException( e );
      try {
        return Collections.unmodifiableCollection( CacheUtil.unwrap( command
            .call() ) );
      } catch ( DataAccessException e1 ) {
        throw e1;
      } catch ( Exception e1 ) {
        throw new DataAccessException( e1 );
      }
    }
  }

  public T findById( Identificator id, Class<? extends T>... subclasses )
      throws DataAccessException {
    final FindByIdCommand<T> command = new FindByIdCommand<T>( theGenericClass,
        cacheInterval, Query.create().withTypes( subclasses ), accessor, id );
    try {
      return replaceCachedObject( CacheUtil
          .unwrap( this.findByIdentificatorCache.put(
              new DefaultCacheKey<FindByIdKey>(
                  new FindByIdKey( id, subclasses ) ), command ) ) );
    } catch ( CacheException e ) {
      handleCacheException( e );
      try {
        return CacheUtil.unwrap( command.call() );
      } catch ( DataAccessException e1 ) {
        throw e1;
      } catch ( Exception e1 ) {
        throw new DataAccessException( e1 );
      }
    }
  }

  /*
   * ************************************* DELEGATES *
   * ************************************
   */

  public T create() throws DataAccessException {
    return accessor.create();
  }

  public void update( T t ) throws DataAccessException {
    accessor.update( t );
  }

  public boolean addDataAccessListener( DataAccessListener<T> listener ) {
    return this.accessor.addDataAccessListener( listener );
  }

  public boolean removeDataAccessListener( DataAccessListener<T> listener ) {
    return this.accessor.removeDataAccessListener( listener );
  }

  public void close() throws DataAccessException {
    this.accessor.close();
  }

  public void delete( T t ) throws DataAccessException {
    this.accessor.delete( t );
  }

  public Accessor<T> getUncachedAccessor() {
    return this.accessor;
  }

}