package de.ars.daojones.internal.runtime.connections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.ars.daojones.runtime.connections.Accessor;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionBuildException;
import de.ars.daojones.runtime.connections.ConnectionMetaData;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.connections.events.DataAccessListener;
import de.ars.daojones.runtime.context.configuration.ConnectionModel;
import de.ars.daojones.runtime.spi.database.ConnectionFactory;

public class PooledConnection<T> extends AbstractConnection<T> {

  private static final Logger logger = Logger.getLogger( PooledConnection.class.getName() );

  private final List<Connection<T>> connectionPool = Collections.synchronizedList( new ArrayList<Connection<T>>() );
  private final Map<Integer, Integer> useCountsByIndex = Collections.synchronizedMap( new HashMap<Integer, Integer>() );
  private final Map<Integer, Boolean> isUsedByIndex = Collections.synchronizedMap( new HashMap<Integer, Boolean>() );
  private final ConnectionFactory connectionFactory;
  private final ConnectionModel model;
  private final int minCount;
  private final int maxCount;

  /**
   * @param connectionProvider
   * @param connectionFactory
   * @param model
   * @param minCount
   * @param maxCount
   * @throws ConnectionBuildException
   */
  public PooledConnection( final ConnectionProvider connectionProvider, final ConnectionFactory connectionFactory,
          final ConnectionModel model, final int minCount, final int maxCount ) throws ConnectionBuildException {
    super( connectionProvider, model );
    this.connectionFactory = connectionFactory;
    this.model = model;
    this.minCount = minCount;
    this.maxCount = maxCount;
    initConnections();
  }

  private Connection<T> createConnection() throws ConnectionBuildException {
    // TODO
    //    final Connection<T> con = connectionFactory
    //            .createConnection( super.getConnectionProvider(), model, theGenericClass );
    //    connectionPool.add( con );
    //    useCountsByIndex.put( connectionPool.size() - 1, 0 );
    return null;//con;
  }

  private void initConnections() throws ConnectionBuildException {
    for ( int i = 0; i < minCount; i++ ) {
      createConnection();
    }
  }

  private int roundRobinIndex = 0;

  private synchronized int getFreeConnectionIndex() throws ConnectionBuildException {
    // search for free connection
    for ( int i = 0; i < connectionPool.size(); i++ ) {
      final Boolean isUsed = isUsedByIndex.get( i );
      if ( null == isUsed || !isUsed.booleanValue() ) {
        //        PooledConnection.logger.log( Level.FINER, "Connection " + theGenericClass.getSimpleName() + "#" + ( i + 1 )
        //                + " wurde zur Verf�gung gestellt." );
        isUsedByIndex.put( i, true );
        return i;
      }
    }
    // no free connection
    int index;
    if ( connectionPool.size() < maxCount ) {
      createConnection();
      index = connectionPool.size() - 1;
    } else {
      index = roundRobinIndex++;
      roundRobinIndex = roundRobinIndex % connectionPool.size();
    }
    //    PooledConnection.logger.log( Level.FINER, "Connection " + theGenericClass.getName() + "#" + ( index + 1 )
    //            + " wurde zur Verf�gung gestellt." );
    isUsedByIndex.put( index, true );
    return index;
  }

  private Connection<T> getFreeConnection() throws ConnectionBuildException {
    final int index = getFreeConnectionIndex();
    final Connection<T> connection = connectionPool.get( index );
    return connection;
  }

  /* ******************************************************
   *  C O N N E C T I O N   I M P L E M E N T A T I O N   *
   ****************************************************** */

  @Override
  public Accessor<T> getAccessor() {
    try {
      final int index = getFreeConnectionIndex();
      final Connection<T> connection = connectionPool.get( index );
      return new AccessorWrapper<T>( connection ) {

        @Override
        public synchronized void close() throws DataAccessException {
          for ( final Connection<T> con : connectionPool ) {
            con.close();
          }
          connectionPool.clear();
          isUsedByIndex.clear();
          useCountsByIndex.clear();
        }

        @Override
        protected void finalize() throws Throwable {
          PooledConnection.logger.log( Level.FINE, "Connection #" + ( index + 1 ) + " wurde freigegeben." );
          isUsedByIndex.remove( index );
          super.finalize();
        }

        @Override
        public boolean addDataAccessListener( final DataAccessListener<T> listener ) {
          boolean result = false;
          // Event Handlers must be registered to all connections.
          for ( final Connection<T> con : connectionPool ) {
            result = con.addDataAccessListener( listener ) || result;
          }
          return result;
        }

        @Override
        public boolean removeDataAccessListener( final DataAccessListener<T> listener ) {
          boolean result = false;
          // Event Handlers must be removed from all connections.
          for ( final Connection<T> con : connectionPool ) {
            result = con.removeDataAccessListener( listener ) || result;
          }
          return result;
        }

      };
    } catch ( final ConnectionBuildException e ) {
      PooledConnection.logger.log( Level.SEVERE, "Unable to get free connection!", e );
      return null;
    }
  }

  /* (non-Javadoc)
   * @see de.ars.daojones.internal.runtime.connections.AbstractConnection#getMetaData()
   */
  @Override
  public ConnectionMetaData<T> getMetaData() {
    try {
      final Connection<T> connection = getFreeConnection();
      return connection.getMetaData();
    } catch ( final ConnectionBuildException e ) {
      PooledConnection.logger.log( Level.SEVERE, "Unable to get free connection!", e );
      return null;
    }
  }

}
