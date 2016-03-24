package de.ars.daojones.connections;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.ars.daojones.connections.model.IConnection;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;

abstract class ConnectionManager {

  private static final String DEFAULT_KEY = null;// Dao.class.getName();
  private Map<String, ConnectionInfo> connectionMap = new HashMap<String, ConnectionInfo>();
  private Set<IConnection> connections = new HashSet<IConnection>();

  private final class ConnectionInfo {
    private final IConnection connectionModel;
    private Connection<Dao> connectionInstance;

    public ConnectionInfo( IConnection connectionModel ) {
      super();
      this.connectionModel = connectionModel;
    }

    public Connection<Dao> getConnectionInstance() {
      return connectionInstance;
    }

    public void setConnectionInstance( Connection<Dao> connectionInstance ) {
      this.connectionInstance = connectionInstance;
    }

    public IConnection getConnectionModel() {
      return connectionModel;
    }
  }

  /**
   * Creates a connection for a special class.
   * 
   * @param className
   *          the name of the class
   * @param connection
   *          the connection meta data
   */
  protected void createConnection( String className, IConnection connection ) {
    connectionMap.put( className, new ConnectionInfo( connection ) );
    connections.add( connection );
  }

  /**
   * Creates a connection.
   * 
   * @param connection
   *          the connection meta data
   */
  public void createConnection( IConnection connection ) {
    final Collection<String> forClasses = new HashSet<String>();
    forClasses.addAll( connection.getForClasses() );
    if ( connection.isDefault() )
      forClasses.add( DEFAULT_KEY );
    for ( final String className : forClasses ) {
      createConnection( className, connection );
    }
    ;
  }

  protected abstract Connection<Dao> createConnectionObject(
      IConnection connection, Class<Dao> c ) throws ConnectionBuildException;

  private ConnectionInfo find( final Class<?> c ) {
    if ( null == c || c.getName().equals( Object.class.getName() ) )
      return null;
    final ConnectionInfo info = connectionMap.get( c.getName() );
    if ( null != info )
      return info;
    // Search for superclass
    final ConnectionInfo superClassInfo = find( c.getSuperclass() );
    if ( null != superClassInfo )
      return superClassInfo;
    // Search for interfaces
    for ( Class<?> i : c.getInterfaces() ) {
      final ConnectionInfo interfaceInfo = find( i );
      if ( null != interfaceInfo )
        return interfaceInfo;
    }
    // give up
    return connectionMap.get( DEFAULT_KEY );
  }

  @SuppressWarnings( "unchecked" )
  protected <T extends Dao> Connection<T> getConnectionForClass( Class<T> c )
      throws DataAccessException {
    ConnectionInfo originalInfo;
    synchronized ( connectionMap ) {
      originalInfo = connectionMap.get( c.getName() );
      if ( null == originalInfo ) {
        final ConnectionInfo info = find( c );
        if ( null == info )
          return null;
        originalInfo = new ConnectionInfo( info.getConnectionModel() );
        connectionMap.put( c.getName(), originalInfo );
      }
    }
    synchronized ( originalInfo ) {
      Connection<Dao> result = originalInfo.getConnectionInstance();
      if ( null == result ) {
        try {
          result = createConnectionObject( originalInfo.getConnectionModel(),
              ( Class<Dao> ) c );
        } catch ( ConnectionBuildException e ) {
          throw new DataAccessException( e );
        }
        originalInfo.setConnectionInstance( result );
      }
      return ( Connection<T> ) result;
    }
  }

  protected void destroyConnections() throws DataAccessException {
    for ( Map.Entry<String, ConnectionInfo> entry : connectionMap.entrySet() ) {
      if ( null == entry.getValue() )
        continue;
      final Connection<Dao> con = entry.getValue().getConnectionInstance();
      if ( null != con ) {
        entry.getValue().setConnectionInstance( null );
        con.close();
      }
    }
    // connections.clear();
  }

}
