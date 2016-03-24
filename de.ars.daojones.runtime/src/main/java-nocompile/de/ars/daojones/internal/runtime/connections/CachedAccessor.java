package de.ars.daojones.internal.runtime.connections;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.ars.daojones.runtime.connections.Accessor;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.connections.configuration.CacheProperty;
import de.ars.daojones.runtime.connections.events.DataAccessListener;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.spi.cache.Cache;
import de.ars.daojones.runtime.spi.cache.CacheException;
import de.ars.daojones.runtime.spi.cache.CacheFactory;
import de.ars.daojones.runtime.spi.cache.CacheFactory.CachePropertiesImpl;
import de.ars.daojones.runtime.spi.cache.CacheValue;
import de.ars.daojones.runtime.spi.cache.DefaultCacheKey;

public class CachedAccessor<T> implements Accessor<T> {

  private static final Logger logger = Logger.getLogger( CachedAccessor.class.getName() );

  private static final class FindByIdKey implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Identificator id;
    private final Class<?>[] subclasses;

    public FindByIdKey( final Identificator id, final Class<?>[] subclasses ) {
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
      final FindByIdKey other = ( FindByIdKey ) obj;
      if ( id == null ) {
        if ( other.id != null ) {
          return false;
        }
      } else if ( !id.equals( other.id ) ) {
        return false;
      }
      if ( !Arrays.equals( subclasses, other.subclasses ) ) {
        return false;
      }
      return true;
    }
  }

  private final Accessor<T> accessor;
  private final int cacheTimeout;
  private final Cache<Identificator, T> objectByIdentificatorCache;
  private final Cache<FindByIdKey, T> findByIdentificatorCache;
  private final Cache<Object[], Collection<T>> findAllByQueryCache;
  private final Class<T> theGenericClass;

  @SuppressWarnings( { "unchecked", "rawtypes" } )
  public CachedAccessor( final Accessor<T> accessor, final int cacheTimeout, final CacheFactory cacheFactory )
          throws CacheException {
    super();
    this.accessor = accessor;
    this.cacheTimeout = cacheTimeout;
    // use cacheConfiguration;
    final Properties p = CachedAccessor.createProperties( getConnection().getModel().getCache().getProperties() );
    this.objectByIdentificatorCache = cacheFactory.createCache( new CachePropertiesImpl( getConnection()
            .getApplicationId(), getConnection().getModel().getId().getConnectionId(), Identificator.class,
            theGenericClass, p ) );
    this.findByIdentificatorCache = cacheFactory.createCache( new CachePropertiesImpl( getConnection()
            .getApplicationId(), getConnection().getModel().getId().getConnectionId(), FindByIdKey.class,
            theGenericClass, p ) );
    this.findAllByQueryCache = cacheFactory.createCache( new CachePropertiesImpl( getConnection().getApplicationId(),
            getConnection().getModel().getId().getConnectionId(), Object[].class, Collection.class, p ) );
  }

  private static Properties createProperties( final Collection<CacheProperty> props ) {
    final Properties result = new Properties();
    if ( null != props ) {
      for ( final CacheProperty cp : props ) {
        result.setProperty( cp.getName(), cp.getValue() );
      }
    }
    return result;
  }

  public Connection<T> getConnection() {
    return this.accessor.getConnection();
  }

  private ConnectionProvider getConnectionProvider() {
    return getConnection().getConnectionProvider();
  }

  /* ************************************* 
   *      C A C H E D M E T H O D S      *
   * *************************************/

  private T replaceCachedObject( final T dao ) throws CacheException {
    if ( null == dao || null == dao.getDataObject() ) {
      return dao;
    }
    final Identificator id = dao.getDataObject().getIdentificator();
    try {
      return CacheUtil.restore( CacheUtil.unwrap( objectByIdentificatorCache.execute(
              new DefaultCacheKey<Identificator>( id ), new SimpleDaoCommand<T>( getConnectionProvider(),
                      theGenericClass, cacheTimeout, null, null, dao ) ) ), getConnectionProvider() );
    } catch ( final DataAccessException e ) {
      throw new CacheException( e );
    }
  }

  @SuppressWarnings( "unchecked" )
  private Collection<T> replaceCachedObjects( final Collection<T> objects ) throws CacheException {
    if ( null == objects ) {
      return null;
    }
    Collection<T> result;
    try {
      result = objects.getClass().newInstance();
    } catch ( final Throwable t ) {
      if ( CachedAccessor.logger.isLoggable( Level.WARNING ) ) {
        CachedAccessor.logger.log( Level.WARNING, "Could not re-create collection of type " + objects.getClass()
                + "! Use HashSet instead." );
      }
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
   * @see de.ars.daojones.runtime.connections.Accessor#find()
   */
  @Override
  public T find() throws DataAccessException {
    return find( Query.create() );
  }

  /**
   * @see de.ars.daojones.runtime.connections.Accessor#find(Query)
   */
  @Override
  public T find( final Query query ) throws DataAccessException {
    final Collection<T> result = findAll( query.withCountOfResults( 1 ) );
    return ( result.isEmpty() ? null : result.iterator().next() );
  }

  /**
   * @see de.ars.daojones.runtime.connections.Accessor#findAll()
   */
  public Collection<T> findAll() throws DataAccessException {
    return findAll( Query.create() );
  }

  private static void handleCacheException( final CacheException e ) throws DataAccessException {
    if ( CachedAccessor.logger.isLoggable( Level.SEVERE ) ) {
      CachedAccessor.logger.log( Level.SEVERE, "Error using the cache.", e );
    }
  }

  /**
   * @see de.ars.daojones.runtime.connections.Accessor#findAll(Query)
   */
  @Override
  public Collection<T> findAll( final Query params ) throws DataAccessException {
    final FindAllCommand<T> command = new FindAllCommand<T>( getConnectionProvider(), theGenericClass, cacheTimeout,
            params, accessor );
    try {
      final CachedQuery query = new CachedQuery( params );
      final CacheValue<Collection<T>> value = this.findAllByQueryCache.execute( query, command );
      final Collection<T> unwrappedValue = CacheUtil.unwrap( value );
      final Collection<T> originalValues = command.isExecuted() ? replaceCachedObjects( unwrappedValue )
              : unwrappedValue;
      return Collections.unmodifiableCollection( CacheUtil.restore( originalValues, getConnectionProvider() ) );
    } catch ( final CacheException e ) {
      CachedAccessor.handleCacheException( e );
      try {
        return Collections.unmodifiableCollection( CacheUtil.restore( CacheUtil.unwrap( command.call() ),
                getConnectionProvider() ) );
      } catch ( final DataAccessException e1 ) {
        throw e1;
      } catch ( final Exception e1 ) {
        throw new DataAccessException( e1 );
      }
    }
  }

  public T findById( final Identificator id, final Class<? extends T>... subclasses ) throws DataAccessException {
    final FindByIdCommand<T> command = new FindByIdCommand<T>( getConnectionProvider(), theGenericClass, cacheTimeout,
            Query.create().withTypes( subclasses ), accessor, id );
    try {
      return replaceCachedObject( CacheUtil.restore( CacheUtil.unwrap( this.findByIdentificatorCache.execute(
              new DefaultCacheKey<FindByIdKey>( new FindByIdKey( id, subclasses ) ), command ) ),
              getConnectionProvider() ) );
    } catch ( final CacheException e ) {
      CachedAccessor.handleCacheException( e );
      try {
        return CacheUtil.unwrap( command.call() );
      } catch ( final DataAccessException e1 ) {
        throw e1;
      } catch ( final Exception e1 ) {
        throw new DataAccessException( e1 );
      }
    }
  }

  /*
   * ************************************* DELEGATES *
   * ************************************
   */

  public T create( final ConnectionProvider provider ) throws DataAccessException {
    return accessor.create( provider );
  }

  public void update( final ConnectionProvider provider, final T t ) throws DataAccessException {
    accessor.update( provider, t );
  }

  @Override
  public boolean addDataAccessListener( final DataAccessListener<T> listener ) {
    return this.accessor.addDataAccessListener( listener );
  }

  @Override
  public boolean removeDataAccessListener( final DataAccessListener<T> listener ) {
    return this.accessor.removeDataAccessListener( listener );
  }

  @Override
  public void close() throws DataAccessException {
    this.accessor.close();
  }

  public void delete( final ConnectionProvider provider, final T t ) throws DataAccessException {
    this.accessor.delete( provider, t );
  }

  public Accessor<T> getUncachedAccessor() {
    return this.accessor;
  }

}