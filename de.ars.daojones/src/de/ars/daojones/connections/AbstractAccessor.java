package de.ars.daojones.connections;

import static de.ars.daojones.LoggerConstants.DEBUG;
import static de.ars.daojones.LoggerConstants.getLogger;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.ars.daojones.annotations.model.DataSourceInfo;
import de.ars.daojones.events.DataAccessEvent;
import de.ars.daojones.events.DataAccessEventProvider;
import de.ars.daojones.events.DataAccessType;
import de.ars.daojones.runtime.BeanCreationException;
import de.ars.daojones.runtime.BeanFactory;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.DataObject;
import de.ars.daojones.runtime.Identificator;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.query.SearchCriterion;

/**
 * An accessor to a database to read, update, insert or delete objects.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 *          the type this accessor should handle
 */
public abstract class AbstractAccessor<T extends Dao> extends
    DataAccessEventProvider<T> implements Accessor<T> {

  /**
   * The class object of the bean type.
   */
  protected final Class<T> theGenericClass;
  private final String applicationContextId;

  /**
   * Creates an instance
   * 
   * @param theGenericClass
   *          the class this accessor should handle
   * @param ctx
   *          the {@link ApplicationContext}
   */
  protected AbstractAccessor( final Class<T> theGenericClass,
      final ApplicationContext ctx ) {
    super();
    this.theGenericClass = theGenericClass;
    this.applicationContextId = ctx.getId();
  }

  /**
   * Returns the ID of the {@link ApplicationContext}.
   * 
   * @return the ID of the {@link ApplicationContext}
   */
  protected String getApplicationContextId() {
    return this.applicationContextId;
  }

  /*
   * *********************************** C R E A T I N G M E T H O D S *
   * **********************************
   */

  /**
   * Creates a bean using {@link BeanFactory}. This can be used by subclasses to
   * create a bean.
   * 
   * @param c
   *          the bean class
   * @param dataObject
   *          the dataobject
   * @return the new bean
   * @throws BeanCreationException
   */
  protected T create( Class<? extends T> c, DataObject dataObject )
      throws BeanCreationException {
    final T t = BeanFactory.createBean( c, dataObject,
        ApplicationContextFactory.getInstance().getApplicationContext(
            applicationContextId ) );
    return t;
  }

  /**
   * @see de.ars.daojones.connections.Accessor#create()
   */
  public T create() throws DataAccessException {
    if ( !getConnection().getMetaData().isCreateAllowed() )
      throw new DataAccessException( "CREATE operations are not allowed!" );
    final DataAccessEvent<T> event = new DataAccessEvent<T>(
        DataAccessType.CREATE, this.getConnection() );
    try {
      try {
        fireAccessBegins( event );
        final T t = BeanFactory.createBean( theGenericClass, /*
                                                                                            																 * createDataObject(
                                                                                            																 * theGenericClass
                                                                                            																 * )
                                                                                            																 */
        null, ApplicationContextFactory.getInstance().getApplicationContext(
            applicationContextId ) );
        t.onCreate();
        fireAccessFinishedSuccessfully( event );
        return t;
      } catch ( BeanCreationException e ) {
        throw new DataAccessException( e );
      }
    } catch ( DataAccessException e ) {
      fireAccessFinishedWithError( event, e );
      throw e;
    }
  }

  /**
   * @see de.ars.daojones.connections.Accessor#delete(de.ars.daojones.runtime.Dao)
   */
  @SuppressWarnings("unchecked")
  public void delete( T t ) throws DataAccessException {
    // break generics because of Sun's compiler bug
    final ConnectionMetaData metadata = getConnection().getMetaData();
    if ( !metadata.isDeleteAllowed( t ) )
      throw new DataAccessException( "DELETE operations are not allowed!" );
    if ( null == t.getDataObject() || t.isNew() )
      return;
    final DataAccessEvent<T> event = new DataAccessEvent<T>(
        DataAccessType.DELETE, this.getConnection() );
    try {
      fireAccessBegins( event );
      t.onDelete();
      doEventDelete( t );
      fireAccessFinishedSuccessfully( event );
    } catch ( DataAccessException e ) {
      fireAccessFinishedWithError( event, e );
      throw e;
    }
  }

  /**
   * Deletes the object from the database.
   * 
   * @param t
   *          the object
   * @throws DataAccessException
   */
  protected abstract void doEventDelete( T t ) throws DataAccessException;

  /**
   * Creates a dataobject for a special class. This method is called during the
   * save event, when a data object does not exist.
   * 
   * @param c
   *          the bean class
   * @return the new data object
   * @throws DataAccessException
   */
  protected abstract DataObject createDataObject( Class<T> c )
      throws DataAccessException;

  /*
   * *********************************** U P D A T I N G M E T H O D S *
   * **********************************
   */

  /**
   * @see de.ars.daojones.connections.Accessor#update(de.ars.daojones.runtime.Dao)
   */
  @SuppressWarnings( "unchecked" )
  public void update( T t ) throws DataAccessException {
    try {
      if ( !t.isNew() ) {
        // break generics because of Sun's compiler bug
        final ConnectionMetaData metadata = getConnection().getMetaData();
        if ( !metadata.isUpdateAllowed( t ) )
          throw new DataAccessException( "UPDATE operations are not allowed!" );
      }
      if ( null == t.getDataObject() )
        t.setDataObject( createDataObject( ( Class<T> ) t.getClass() ) );
      final DataAccessEvent<T> event = new DataAccessEvent<T>(
          DataAccessType.WRITE, this.getConnection() );
      try {
        fireAccessBegins( event );
        t.onUpdate();
        doEventUpdate( t );
        fireAccessFinishedSuccessfully( event );
      } catch ( DataAccessException e ) {
        fireAccessFinishedWithError( event, e );
        throw e;
      }
    } catch ( SecurityException e ) {
      throw new DataAccessException( e );
    } catch ( IllegalArgumentException e ) {
      throw new DataAccessException( e );
    }
  }

  /**
   * Saves a dao.
   * 
   * @param dao
   *          the dao
   * @throws DataAccessException
   */
  protected abstract void doEventUpdate( Dao dao ) throws DataAccessException;

  /*
   * ********************************* F I N D I N G M E T H O D S *
   * ********************************
   */
  /**
   * @see de.ars.daojones.connections.Accessor#findAll()
   */
  public Collection<T> findAll() throws DataAccessException {
    return findAll( Query.create() );
  }

  /**
   * @see de.ars.daojones.connections.Accessor#findAll(SearchCriterion...)
   */
  @Deprecated
  // TODO remove
  public Collection<T> findAll( SearchCriterion... criteria )
      throws DataAccessException {
    return findAll( Query.create( criteria ) );
  }

  /**
   * @see de.ars.daojones.connections.Accessor#findAll(Query)
   */
  @SuppressWarnings( "unchecked" )
  public Collection<T> findAll( Query query ) throws DataAccessException {
    final DataAccessEvent<T> event = new DataAccessEvent<T>(
        DataAccessType.READ, this.getConnection() );
    fireAccessBegins( event );
    try {
      final Collection<T> result = new HashSet<T>();
      /*
       * TODO
       * Benötigt wird:
       *  - Subclasses
       *  - Map von DataSourceInfos zum Abfragen
       */
      final Set<Class<?>> beanTypes = new HashSet<Class<?>>( Arrays
          .asList( query.getBeanTypes() ) );
      beanTypes.add( theGenericClass );
      final Map<DataSourceInfo, Class<? extends T>> dataSources = ( Map ) AccessorHelper
          .getDataSources( beanTypes.toArray( new Class[beanTypes.size()] ) );
      final Map<Connection, Collection<DataSourceInfo>> dataSourcesByConnection = new HashMap<Connection, Collection<DataSourceInfo>>();
      for ( Map.Entry<DataSourceInfo, Class<? extends T>> entry : dataSources
          .entrySet() ) {
        final Connection<? extends T> con = entry.getValue().getName().equals(
            theGenericClass.getName() ) ? this.getConnection() : Connection
            .get( this.getConnection().getContext(), entry.getValue() );
        Collection<DataSourceInfo> sources = dataSourcesByConnection.get( con );
        if ( null == sources ) {
          sources = new HashSet<DataSourceInfo>();
          dataSourcesByConnection.put( con, sources );
        }
        sources.add( entry.getKey() );
      }
      for ( Map.Entry<Connection, Collection<DataSourceInfo>> entry : dataSourcesByConnection
          .entrySet() ) {
        Accessor accessor = entry.getKey().getAccessor();
        if ( accessor.getConnection().equals( this.getConnection() ) )
          accessor = this;
        if ( accessor instanceof AbstractAccessor ) {
          final Map<DataSourceInfo, Class<? extends T>> dataSourcesForThisConnection = new HashMap<DataSourceInfo, Class<? extends T>>();
          for ( DataSourceInfo info : entry.getValue() )
            dataSourcesForThisConnection.put( info, dataSources.get( info ) );
          getLogger().log(
              DEBUG,
              "Asking connection "
                  + accessor.getConnection().getConnectionData() + " for "
                  + dataSourcesForThisConnection + " from " + dataSources );
          result.addAll( ( ( AbstractAccessor<T> ) accessor ).findAll(
              dataSourcesForThisConnection, query.getMaxCountOfResults(), query
                  .getCriterion() ) );
        } else {
          result.addAll( accessor.findAll( query ) );
        }
      }
      fireAccessFinishedSuccessfully( event );
      return result;
    } catch ( DataAccessException e ) {
      fireAccessFinishedWithError( event, e );
      throw e;
    }
  }

  /**
   * Creates a query and searches the database. There are multiple queries to be
   * built based on the datasources by class.
   * 
   * @param dataSources
   *          the datasources that are mapped to this connection.
   * @param maxCountOfResults
   *          the maximum count of results to be fetched or a number <1 for
   *          unlimited access
   * @param criterion
   *          the search criterion
   * @return the collection of results
   * @throws DataAccessException
   *           if accessing the database failed
   */
  protected abstract Collection<T> findAll(
      Map<DataSourceInfo, Class<? extends T>> dataSources,
      int maxCountOfResults, SearchCriterion criterion )
      throws DataAccessException;

  /**
   * @see de.ars.daojones.connections.Accessor#find()
   */
  // TODO remove
  public T find() throws DataAccessException {
    return find( Query.create() );
  }

  /**
   * @see de.ars.daojones.connections.Accessor#find(SearchCriterion...)
   */
  @Deprecated
  // TODO remove
  public T find( SearchCriterion... criteria ) throws DataAccessException {
    return find( Query.create( criteria ) );
  }

  /**
   * @see de.ars.daojones.connections.Accessor#find(Query)
   */
  public T find( Query query ) throws DataAccessException {
    final Collection<T> result = findAll( query.withCountOfResults( 1 ) );
    return ( result.isEmpty() ? null : result.iterator().next() );
  }

  /**
   * @see de.ars.daojones.connections.Accessor#findById(Identificator, Class...)
   */
  @SuppressWarnings( "unchecked" )
  public T findById( Identificator id, Class<? extends T>... subclasses )
      throws DataAccessException {
    if ( subclasses == null || subclasses.length == 0 ) {
      subclasses = new Class[] { theGenericClass };
    }
    final DataAccessEvent<T> event = new DataAccessEvent<T>(
        DataAccessType.READ, this.getConnection() );
    T result = null;
    try {
      fireAccessBegins( event );
      final DataObject dataObject = findDataObjectById( id );
      if ( null != dataObject ) {
        final DataSourceInfo dataSource = getDataSource( dataObject );
        Class<? extends T> beanType = null;
        if ( null != dataSource ) {
          for ( Class<? extends T> cc : subclasses ) {
            if ( !AccessorHelper.isAbstract( cc )
                && dataSource.equals( AccessorHelper.getDataSource( cc ) )
                && ( null == beanType && theGenericClass.isAssignableFrom( cc ) || cc
                    .getName().equals( theGenericClass.getName() ) ) ) {
              beanType = cc;
            }
          }
          if ( null != beanType ) {
            try {
              result = ( T ) BeanFactory.createBean( beanType, dataObject,
                  ApplicationContextFactory.getInstance()
                      .getApplicationContext( applicationContextId ) );
            } catch ( BeanCreationException e ) {
              throw new DataAccessException( e );
            }
          }
        }
      }
      fireAccessFinishedSuccessfully( event );
      return result;
    } catch ( DataAccessException e ) {
      fireAccessFinishedWithError( event, e );
      throw e;
    }
  }

  /**
   * Searches for a dataobject by an identificator
   * 
   * @param id
   *          the identificator
   * @return the dataobject
   * @throws DataAccessException
   */
  protected abstract DataObject findDataObjectById( Identificator id )
      throws DataAccessException;

  /**
   * Returns the datasource of an identificator or null, if the identificator
   * does not contain any information about a datasource. Searching the database
   * is not necessary and should be avoided.
   * 
   * @param dataObject
   *          the identificator
   * @return the datasource
   * @throws DataAccessException
   */
  protected abstract DataSourceInfo getDataSource( DataObject dataObject )
      throws DataAccessException;

  /**
   * @see Accessor#close()
   */
  @Override
  public void close() throws DataAccessException {
    super.close();
  }
}
