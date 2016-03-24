package de.ars.daojones.runtime.configuration.context;

import de.ars.daojones.runtime.configuration.connections.Connection;

/**
 * Default implementation of a connection model.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public class ConnectionModelImpl implements ConnectionModel {

  private static final long serialVersionUID = 1L;
  private static int idCounter;

  private final Id id;
  private final Connection connection;

  /**
   * Constructor.
   * 
   * @param application
   *          the application
   * @param connection
   *          the connection
   */
  public ConnectionModelImpl( final String application, final Connection connection ) {
    super();
    if ( null == connection ) {
      throw new IllegalArgumentException();
    } else {
      this.connection = connection;
      if ( null == connection.getId() ) {
        connection.setId( "connection" + ( ConnectionModelImpl.idCounter++ ) );
      }
      id = new Id( application, connection.getId() );
    }
  }

  @Override
  public Id getId() {
    return id;
  }

  @Override
  public String getName() {
    return connection.getName();
  }

  @Override
  public String getDescription() {
    return connection.getDescription();
  }

  @Override
  public Connection getConnection() {
    return connection;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
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
    final ConnectionModelImpl other = ( ConnectionModelImpl ) obj;
    if ( id == null ) {
      if ( other.id != null ) {
        return false;
      }
    } else if ( !id.equals( other.id ) ) {
      return false;
    }
    return true;
  }

}
