package de.ars.daojones.internal.runtime.connections;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.internal.runtime.utilities.ReflectionHelper;
import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.beans.identification.ApplicationDependentIdentificator;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.Accessor;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionMetaData;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.connections.events.DataAccessEvent;
import de.ars.daojones.runtime.connections.events.DataAccessEventProvider;
import de.ars.daojones.runtime.connections.events.DataAccessType;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.spi.beans.fields.AlreadyInjectingException;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessor;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorContext;
import de.ars.daojones.runtime.spi.database.BeanCreationException;
import de.ars.daojones.runtime.spi.database.CompositeSearchResult;
import de.ars.daojones.runtime.spi.database.ConnectionContext;
import de.ars.daojones.runtime.spi.database.DatabaseEntry;
import de.ars.daojones.runtime.spi.database.TransformingSearchResult;

/**
 * An accessor to a database to read, update, insert or delete objects.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 * @param <T>
 *          the type this accessor should handle
 */
public abstract class AbstractAccessor<T> extends DataAccessEventProvider<T> implements Accessor<T> {

  private static Messages logger = Messages.create( "connections.AbstractAccessor" );

  private final ConnectionContext<T> context;

  /**
   * Creates an instance
   * 
   * @param context
   *          the context
   */
  protected AbstractAccessor( final ConnectionContext<T> context ) {
    super();
    this.context = context;
  }

  @Override
  public BeanModel getBeanModel() {
    return getContext().getBeanModel();
  }

  /**
   * Returns the context.
   * 
   * @return the context
   */
  protected ConnectionContext<T> getContext() {
    return context;
  }

  /**
   * Creates a bean instance. This can be used by subclasses to create a bean.
   * 
   * @param c
   *          the bean class
   * @return the new bean
   * @throws BeanCreationException
   */
  protected T createInstance( final Class<? extends T> c ) throws BeanCreationException {
    try {
      final T t = ReflectionHelper.newInstance( c );
      return t;
    } catch ( final InstantiationException e ) {
      throw new BeanCreationException( e );
    }
  }

  private static interface Deletable {
    void delete() throws DataAccessException;
  }

  @Override
  public void delete( final Query query ) throws DataAccessException {
    // Event Handling
    final DataAccessEvent<T> event = new DataAccessEvent<T>( DataAccessType.DELETE, this.getConnection(), null );
    fireAccessBegins( event );
    try {
      // No injection, just query and delete
      final SearchResult<Deletable> deletables = handleQuery( query, new QueryHandler<Deletable, T>() {

        @Override
        public SearchResult<Deletable> handle( final AbstractAccessor<T> accessor, final Query query,
                final ConnectionAccess access ) throws DataAccessException {
          final Collection<BeanModel> beanModels = access.getBeanModels().values();
          final SearchResult<DatabaseEntry> entries = accessor.executeQuery( query,
                  beanModels.toArray( new BeanModel[beanModels.size()] ) );
          return new TransformingSearchResult<DatabaseEntry, Deletable>( entries ) {

            @Override
            protected Deletable convert( final DatabaseEntry t ) {
              return new Deletable() {

                @Override
                public void delete() throws DataAccessException {
                  t.delete();
                }
              };
            }
          };
        }

        @SuppressWarnings( "unchecked" )
        @Override
        public SearchResult<Deletable> handle( final Connection<T> connection, final Query query )
                throws DataAccessException {
          return new TransformingSearchResult<T, Deletable>( connection.findAll( query ) ) {

            @Override
            protected Deletable convert( final T t ) {
              return new Deletable() {

                @Override
                public void delete() throws DataAccessException {
                  connection.delete( t );
                }
              };
            }

          };
        }

      } );
      try {
        for ( final Deletable deletable : deletables ) {
          deletable.delete();
        }
      } finally {
        deletables.close();
      }
      // Event Handling
      fireAccessFinishedSuccessfully( event );
    } catch ( final DataAccessException e ) {
      // Event Handling
      fireAccessFinishedWithError( event, e );
      throw e;
    } catch ( final Exception e ) {
      // Event Handling
      fireAccessFinishedWithError( event, e );
      throw new DataAccessException( e );
    }
  }

  // public because of usage in InjectionContextFactory
  public abstract DatabaseEntry doFindById( final Object id ) throws DataAccessException;

  protected abstract DatabaseEntry doCreate( BeanModel model ) throws DataAccessException;

  @SuppressWarnings( "unchecked" )
  protected Class<? extends T> getBeanClass( final BeanModel model ) throws ClassNotFoundException {
    return ( Class<? extends T> ) getContext().getBeanType().getClassLoader().loadClass( model.getBean().getType() );
  }

  @Override
  public T findById( final Object id ) throws DataAccessException {
    final DatabaseEntry entry;
    if ( id instanceof ApplicationDependentIdentificator ) {
      entry = doFindById( ( ( ApplicationDependentIdentificator ) id ).get( getContext().getApplicationId() ) );
    } else {
      entry = doFindById( id );
    }
    if ( null != entry ) {
      try {
        final Class<? extends T> type = getBeanClass( entry.getBeanModel() );
        final T result = convert( entry, type );
        return result;
      } catch ( final ClassNotFoundException e ) {
        throw new DataAccessException( e );
      }
    } else {
      return null;
    }
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public void delete( final T... beans ) throws DataAccessException {
    if ( null != beans && beans.length > 0 ) {
      final ConnectionMetaData<T> metadata = getConnection().getMetaData();
      if ( !metadata.isDeleteAllowed( beans ) ) {
        throw new DataAccessException( AbstractAccessor.logger.get( "error.delete.forbidden" ) );
      }
      final DataAccessEvent<T> event = new DataAccessEvent<T>( DataAccessType.DELETE, this, beans );
      try {
        fireAccessBegins( event );
        final BeanModelManager bmm = getContext().getBeanModelManager();
        final String applicationId = getContext().getApplicationId();
        for ( final T bean : beans ) {
          try {
            final BeanModel model = bmm.getEffectiveModel( applicationId, bean.getClass() );
            final Identificator id = getBeanAccessor().getIdentificator( model, bean );
            final DatabaseEntry dbEntry = doFindById( id );
            if ( null != dbEntry ) {
              dbEntry.delete();
              // Inject new identificator
              AbstractAccessor.this.getBeanAccessor()
                      .reinjectAfterStore(
                              AbstractAccessor.this.createBeanAccessorContext( dbEntry,
                                      ( Class<? extends T> ) bean.getClass() ), bean );
            }
          } catch ( final FieldAccessException e ) {
            throw new DataAccessException( e );
          } catch ( final ConfigurationException e ) {
            throw new DataAccessException( e );
          } catch ( final AlreadyInjectingException e ) {
            // Should not occur
            throw new DataAccessException( e );
          }
        }
        fireAccessFinishedSuccessfully( event );
      } catch ( final DataAccessException e ) {
        fireAccessFinishedWithError( event, e );
        throw e;
      }
    }
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public void update( final T... beans ) throws DataAccessException {
    if ( null != beans && beans.length > 0 ) {
      try {
        final ConnectionMetaData<T> metadata = getConnection().getMetaData();
        if ( !metadata.isUpdateAllowed( beans ) ) {
          throw new DataAccessException( AbstractAccessor.logger.get( "error.update.forbidden" ) );
        }
        final DataAccessEvent<T> event = new DataAccessEvent<T>( DataAccessType.WRITE, this.getConnection(), beans );
        try {
          fireAccessBegins( event );
          final BeanModelManager bmm = getContext().getBeanModelManager();
          final String applicationId = getContext().getApplicationId();
          for ( final T bean : beans ) {
            try {
              final BeanModel model = bmm.getEffectiveModel( applicationId, bean.getClass() );
              final Identificator id = getBeanAccessor().getIdentificator( model, bean );
              final DatabaseEntry dbEntry;
              if ( null != id ) {
                final Serializable appInternalId = id.getId( applicationId );
                dbEntry = doFindById( appInternalId );
                if ( null == dbEntry ) {
                  throw new DataAccessException( AbstractAccessor.logger.get( "error.update.idnotfound", appInternalId ) );
                }
              } else {
                // Create entry
                dbEntry = doCreate( model );
              }
              // Update fields!
              final BeanAccessorContext<T> bac = createBeanAccessorContext( dbEntry,
                      ( Class<? extends T> ) bean.getClass() );
              getBeanAccessor().store( bac, bean );
              // Store entry
              dbEntry.store();
              // Inject new identificator
              AbstractAccessor.this.getBeanAccessor()
                      .reinjectAfterStore(
                              AbstractAccessor.this.createBeanAccessorContext( dbEntry,
                                      ( Class<? extends T> ) bean.getClass() ), bean );
            } catch ( final FieldAccessException e ) {
              throw new DataAccessException( e );
            } catch ( final ConfigurationException e ) {
              throw new DataAccessException( e );
            } catch ( final AlreadyInjectingException e ) {
              // Should not occur
              throw new DataAccessException( e );
            }
          }
          // Event Handling
          fireAccessFinishedSuccessfully( event );
        } catch ( final DataAccessException e ) {
          fireAccessFinishedWithError( event, e );
          throw e;
        }
      } catch ( final SecurityException e ) {
        throw new DataAccessException( e );
      } catch ( final IllegalArgumentException e ) {
        throw new DataAccessException( e );
      }
    }
  }

  // BeanAccessor mitgeben und die Möglichkeit, BeanAccessor-Context mitzugeben
  // oder Callback zum Handhaben der Inject

  /*
   * **************************
   *  F I N D   M E T H O D S *
   * **************************
   */

  /*
   
   We have the bean models, connection models.
   The query execution must be done by the driver, results must be iterable.
   The driver must read the next result and assign all fields -> read the database/document/view
   Multiple connections result in multiple results, that have to be united.
   driver must provide a method to access the database
   
   -> To beachten:
    -> Query parameters and settings
    -> Converter
    -> SC Handling by the driver
   
   
   
   
   
   */

  private class ConnectionAccess {
    private final Map<Class<? extends T>, BeanModel> beanModels = new HashMap<Class<? extends T>, BeanModel>();
    private final ConnectionModel connectionModel;

    public ConnectionAccess( final ConnectionModel connectionModel ) {
      super();
      this.connectionModel = connectionModel;
    }

    public Map<Class<? extends T>, BeanModel> getBeanModels() {
      return beanModels;
    }

    public ConnectionModel getConnectionModel() {
      return connectionModel;
    }

  }

  /**
   * Executes the query.
   * 
   * @param query
   *          the query
   * @param beanModels
   *          the bean models
   * @return the search result
   * @throws DataAccessException
   */
  protected abstract SearchResult<DatabaseEntry> executeQuery( final Query query, final BeanModel... beanModels )
          throws DataAccessException;

  private SearchResult<T> findAllWithThisConnection( final Query query, final ConnectionAccess access )
          throws DataAccessException {
    final Map<Class<? extends T>, BeanModel> beanModels = access.getBeanModels();
    // create inverse map
    final Map<BeanModel, Class<? extends T>> beanTypes = new HashMap<BeanModel, Class<? extends T>>();
    for ( final Map.Entry<Class<? extends T>, BeanModel> entry : beanModels.entrySet() ) {
      beanTypes.put( entry.getValue(), entry.getKey() );
    }
    final Collection<BeanModel> queryModels = access.getBeanModels().values();
    final SearchResult<DatabaseEntry> result = executeQuery( query,
            queryModels.toArray( new BeanModel[queryModels.size()] ) );
    // assign to the fields, invoke the constructors, methods ...
    // invoke the converters
    return new TransformingSearchResult<DatabaseEntry, T>( result ) {

      /*
       * Converts the search result from the database driver to an object of type T.
       */
      @Override
      protected T convert( final DatabaseEntry t ) {
        final BeanModel beanModel = t.getBeanModel();
        final Class<? extends T> type = beanTypes.get( beanModel );
        return AbstractAccessor.this.convert( t, type );
      }

    };
  }

  /**
   * Creates the context for a bean accessor operation.
   * 
   * @param t
   *          the database entry
   * @param type
   *          the bean type
   * @return the bean accessor context
   */
  @SuppressWarnings( "unchecked" )
  protected BeanAccessorContext<T> createBeanAccessorContext( final DatabaseEntry t, final Class<? extends T> type ) {
    return new BeanAccessorContext<T>( ( Class<T> ) type, t.getBeanModel(), getContext().getConnectionProvider(), t,
            getContext().getBeanModelManager() );
  }

  /**
   * Returns the bean accessor.
   * 
   * @return the bean accessor
   */
  protected BeanAccessor getBeanAccessor() {
    return getContext().getBeanAccessorProvider().getBeanAccessor();
  }

  /**
   * Converts a database entry to a bean instance.
   * 
   * @param t
   *          the database entry
   * @param type
   *          the type
   * @return the bean instance
   */
  protected T convert( final DatabaseEntry t, final Class<? extends T> type ) {
    try {
      // Create instance
      final T result = getBeanAccessor().createBeanInstance( createBeanAccessorContext( t, type ) );
      return result;
    } catch ( final RuntimeException e ) {
      throw e;
    } catch ( final Exception e ) {
      throw new RuntimeException( e );
    }
  }

  private abstract class QueryHandler<X, Y> {

    public abstract SearchResult<X> handle( AbstractAccessor<T> accessor, final Query query,
            final ConnectionAccess access ) throws DataAccessException;

    public abstract SearchResult<X> handle( Connection<Y> connection, Query query ) throws DataAccessException;

  }

  @SuppressWarnings( { "unchecked", "rawtypes" } )
  private <X> SearchResult<X> handleQuery( final Query query, final QueryHandler<X, T> handler )
          throws DataAccessException, ConfigurationException {

    final String applicationId = getContext().getApplicationId();
    final Class<T> beanType = getContext().getBeanType();

    // Read all sub types of the bean that have to be searched within the database
    final Class<?>[] beanTypesFromQuery = query.getBeanTypes();
    final Set<Class<? extends T>> beanTypes = new HashSet<Class<? extends T>>();
    if ( beanTypesFromQuery == Query.BEANTYPES_DEFAULT ) {
      // If not specified by Query object -> Scan classpath for subtypes
      beanTypes.add( beanType );
      // find sub types
      // TODO find sub types by using bean model manager
      // beanTypes.addAll( ClasspathHelper.getSubtypes( beanType ) );
    } else {
      // check super types
      for ( final Class<?> type : beanTypesFromQuery ) {
        if ( beanType.isAssignableFrom( type ) ) {
          beanTypes.add( ( Class<? extends T> ) type );
        } else {
          throw new DataAccessException( AbstractAccessor.logger.get( "error.type.nosubtype", type.getName(),
                  beanType.getName() ) );
        }
      }
    }

    // Get effective bean models (TODO Caching)
    // Per Connection Model -> Connection, Bean Types and their models
    final Map<ConnectionModel, ConnectionAccess> connections = new HashMap<ConnectionModel, ConnectionAccess>();
    for ( final Class<? extends T> type : beanTypes ) {
      if ( !type.isInterface() && !Modifier.isAbstract( type.getModifiers() ) ) {
        final BeanModel beanModel = getContext().getBeanModelManager().getEffectiveModel( applicationId, type );
        if ( null != beanModel ) {
          final ConnectionModel connectionModel = getContext().getConnectionProvider().getConnectionModel( type );
          if ( null != connectionModel ) {
            ConnectionAccess connectionAccess = connections.get( connectionModel );
            if ( null == connectionAccess ) {
              connectionAccess = new ConnectionAccess( connectionModel );
              connections.put( connectionModel, connectionAccess );
            }
            connectionAccess.getBeanModels().put( type, beanModel );
          }
        }
      }
    }

    // Now we are ready to execute queries per connection (connection has driver)
    // counter for the maximum number of results
    final Query queryNow = query.clone();
    // we only need to invoke queries when the maximum count of results was not reached yet
    int maxCountOfResults = queryNow.getMaxCountOfResults();
    final SearchResult<X>[] results = new SearchResult[connections.size()];
    int idx = 0;
    for ( final ConnectionAccess access : connections.values() ) {
      final ConnectionModel connectionModel = access.getConnectionModel();
      // get bean types
      final Set<Class<? extends T>> types = access.beanModels.keySet();
      // create query object for bean types
      queryNow.only( types.toArray( new Class[types.size()] ) );
      // limit count of results
      queryNow.only( maxCountOfResults );
      final SearchResult<X> resultNow;
      // we have to decide
      if ( connectionModel.equals( getContext().getConnectionModel() ) ) {
        // for this connection, invoke findAllWithThisConnection(...) directly
        resultNow = handler.handle( this, queryNow, access );
      } else {
        // else find connection by using the connection provider
        final ConnectionProvider cp = getContext().getConnectionProvider();
        // we use the first bean type, because all bean types share the same connection model
        final Connection<?> connection = cp.getConnection( queryNow.getBeanTypes()[0] );
        if ( connection instanceof AbstractAccessor ) {
          // invoke the findAllWithThisConnection(...) on the connection
          final AbstractAccessor<T> accessor = ( de.ars.daojones.internal.runtime.connections.AbstractAccessor<T> ) connection;
          resultNow = handler.handle( accessor, queryNow, access );
        } else {
          // special case: connection is not sub class of this class
          // we need to invoke findall on the connection again
          resultNow = handler.handle( ( Connection<T> ) connection, queryNow );
        }
      }
      // collection search results
      results[idx++] = resultNow;
      // decrease counter and break loop
      maxCountOfResults -= resultNow.size();
      if ( maxCountOfResults <= 0 ) {
        break;
      }
    }
    // unite results
    return new CompositeSearchResult( query.getMaxCountOfResults(), results );
  }

  /**
   * @see de.ars.daojones.runtime.connections.Accessor#findAll(Query)
   */
  @Override
  public SearchResult<T> findAll( final Query query ) throws DataAccessException {
    // Event Handling
    final DataAccessEvent<T> event = new DataAccessEvent<T>( DataAccessType.READ, this.getConnection(), null );
    fireAccessBegins( event );
    try {
      final SearchResult<T> result = handleQuery( query, new QueryHandler<T, T>() {

        @Override
        public SearchResult<T> handle( final AbstractAccessor<T> accessor, final Query query,
                final ConnectionAccess access ) throws DataAccessException {
          return accessor.findAllWithThisConnection( query, access );
        }

        @Override
        public SearchResult<T> handle( final Connection<T> connection, final Query query ) throws DataAccessException {
          return connection.findAll( query );
        }

      } );
      // Event Handling
      fireAccessFinishedSuccessfully( event );
      // return result
      return result;
    } catch ( final DataAccessException e ) {
      // Event Handling
      fireAccessFinishedWithError( event, e );
      throw e;
    } catch ( final Exception e ) {
      // Event Handling
      fireAccessFinishedWithError( event, e );
      throw new DataAccessException( e );
    }
  }

  @Override
  public T find() throws DataAccessException {
    return find( Query.create() );
  }

  @Override
  public T find( final Query query ) throws DataAccessException {
    final SearchResult<T> result = findAll( query.only( 1 ) );
    return ( result.size() > 0 ? result.iterator().next() : null );
  }

  /**
   * @see Accessor#close()
   */
  @Override
  public void close() throws DataAccessException {
    super.close();
  }

}
