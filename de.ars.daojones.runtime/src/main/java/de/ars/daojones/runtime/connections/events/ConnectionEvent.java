package de.ars.daojones.runtime.connections.events;

import java.io.Serializable;

import de.ars.daojones.runtime.configuration.context.ConnectionModel;

/**
 * An event providing information about connections.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public final class ConnectionEvent implements Serializable {

  private static final long serialVersionUID = 1L;

  private final ConnectionEventType type;
  private final ConnectionModel[] connections;
  private final Object source;

  /**
   * Creates an instance.
   * 
   * @param type
   * @param source
   * @param connections
   */
  public ConnectionEvent( final ConnectionEventType type, final Object source, final ConnectionModel... connections ) {
    super();
    this.type = type;
    this.source = source;
    this.connections = connections;
  }

  /**
   * Returns the event type.
   * 
   * @return the event type
   */
  public ConnectionEventType getType() {
    return type;
  }

  /**
   * Returns the connections.
   * 
   * @return the connections
   */
  public ConnectionModel[] getConnections() {
    return connections;
  }

  /**
   * Returns the object that fired this event.
   * 
   * @return the source the object that fired this event
   */
  public Object getSource() {
    return source;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append( getType() );
    sb.append( '[' );
    sb.append( "source=" );
    sb.append( getSource() );
    sb.append( ";connections={" );
    boolean first = true;
    for ( final ConnectionModel c : getConnections() ) {
      if ( first ) {
        first = false;
      } else {
        sb.append( ',' );
      }
      sb.append( c.getId() );
    }
    sb.append( '}' );
    sb.append( ']' );
    return sb.toString();
  }

}
