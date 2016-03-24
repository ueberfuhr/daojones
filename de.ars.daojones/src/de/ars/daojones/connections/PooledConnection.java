package de.ars.daojones.connections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.ars.daojones.events.DataAccessListener;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.Identificator;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.query.SearchCriterion;

import static de.ars.daojones.LoggerConstants.*;

class PooledConnection<T extends Dao> extends Connection<T> implements
    ConnectionWrapper<T> {

  private static final Logger logger = Logger.getLogger( PooledConnection.class
      .getName() );

  private final List<Connection<T>> connectionPool = Collections
      .synchronizedList( new ArrayList<Connection<T>>() );
  private final Map<Integer, Integer> useCountsByIndex = Collections
      .synchronizedMap( new HashMap<Integer, Integer>() );
  private final Map<Integer, Boolean> isUsedByIndex = Collections
      .synchronizedMap( new HashMap<Integer, Boolean>() );
  private final ConnectionFactory connectionFactory;
  private final ConnectionData connectionData;
  private final int minCount;
  private final int maxCount;
  private final Class<T> theGenericClass;
  private final ConnectionMetaData<T> metaData;

  /**
   * @param ctx
   * @param theGenericClass
   * @param connectionFactory
   * @param connectionData
   * @param minCount
   * @param maxCount
   */
  public PooledConnection( final ApplicationContext ctx,
      final Class<T> theGenericClass,
      final ConnectionFactory connectionFactory,
      final ConnectionData connectionData, final int minCount,
      final int maxCount ) {
    super( ctx );
    this.connectionFactory = connectionFactory;
    this.connectionData = connectionData;
    this.minCount = minCount;
    this.maxCount = maxCount;
    this.theGenericClass = theGenericClass;
    initConnections();
    this.metaData = getConnection().getMetaData();
  }

  @Override
  public ConnectionMetaData<T> getMetaData() {
    return this.metaData;
  }

  @Override
  public ConnectionData getConnectionData() {
    return this.connectionData;
  }

  private synchronized Connection<T> createConnection() {
    final Connection<T> con = connectionFactory.createConnection( super
        .getContext(), theGenericClass, connectionData );
    connectionPool.add( con );
    useCountsByIndex.put( connectionPool.size() - 1, 0 );
    return con;
  }

  private void initConnections() {
    for ( int i = 0; i < minCount; i++ )
      createConnection();
  }

  private int roundRobinIndex = 0;

  private synchronized int getFreeConnection() {
    // search for free connection
    for ( int i = 0; i < connectionPool.size(); i++ ) {
      final Boolean isUsed = isUsedByIndex.get( i );
      if ( null == isUsed || !isUsed.booleanValue() ) {
        getLogger().log(
            DEBUG,
            "Connection " + theGenericClass.getSimpleName() + "#" + ( i + 1 )
                + " wurde zur Verfügung gestellt." );
        isUsedByIndex.put( i, true );
        return i;
      }
    }
    ;
    // no free connection
    int index;
    if ( connectionPool.size() < maxCount ) {
      createConnection();
      index = connectionPool.size() - 1;
    } else {
      index = roundRobinIndex++;
      roundRobinIndex = roundRobinIndex % connectionPool.size();
    }
    getLogger().log(
        DEBUG,
        "Connection " + theGenericClass.getName() + "#" + ( index + 1 )
            + " wurde zur Verfügung gestellt." );
    isUsedByIndex.put( index, true );
    return index;
  }

  /* ******************************************************
   *  C O N N E C T I O N   I M P L E M E N T A T I O N   *
   ****************************************************** */

  @Override
  public Connection<T> getConnection() {
    final int index = getFreeConnection();
    return connectionPool.get( index );
  }

  @Override
  public synchronized Accessor<T> getAccessor() {
    final int index = getFreeConnection();
    final Connection<T> connection = connectionPool.get( index );
    return new Accessor<T>() {
      final Accessor<T> internalAccessor = connection.getAccessor();

      public synchronized void close() throws DataAccessException {
        for ( Connection<T> con : connectionPool )
          con.close();
        connectionPool.clear();
        isUsedByIndex.clear();
        useCountsByIndex.clear();
      }

      public Connection<T> getConnection() {
        return connection;
      }

      @Override
      protected void finalize() throws Throwable {
        logger.log( Level.FINE, "Connection #" + ( index + 1 )
            + " wurde freigegeben." );
        isUsedByIndex.remove( index );
        super.finalize();
      }

      public T create() throws DataAccessException {
        return internalAccessor.create();
      }

      public T find() throws DataAccessException {
        return internalAccessor.find();
      }

      public T find( Query query ) throws DataAccessException {
        return internalAccessor.find( query );
      }

      @Deprecated
      public T find( SearchCriterion... criterions ) throws DataAccessException {
        return internalAccessor.find( criterions );
      }

      public Collection<T> findAll() throws DataAccessException {
        return internalAccessor.findAll();
      }

      public Collection<T> findAll( Query query ) throws DataAccessException {
        return internalAccessor.findAll( query );
      }

      @Deprecated
      public Collection<T> findAll( SearchCriterion... criterions )
          throws DataAccessException {
        return internalAccessor.findAll( criterions );
      }

      public T findById( Identificator id, Class<? extends T>... subclasses ) throws DataAccessException {
        return internalAccessor.findById( id, subclasses );
      }

      public void update( T t ) throws DataAccessException {
        internalAccessor.update( t );
      }

      public boolean addDataAccessListener( DataAccessListener<T> listener ) {
        return this.internalAccessor.addDataAccessListener( listener );
      }

      public boolean removeDataAccessListener( DataAccessListener<T> listener ) {
        return this.internalAccessor.removeDataAccessListener( listener );
      }

      public void delete( T t ) throws DataAccessException {
        this.internalAccessor.delete( t );
      }

    };
  }

  @Override
  public Identificator getIdentificator( String id ) throws DataAccessException {
    return getConnection().getIdentificator( id );
  }

}
