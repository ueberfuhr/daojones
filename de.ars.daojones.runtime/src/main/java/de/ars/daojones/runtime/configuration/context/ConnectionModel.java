package de.ars.daojones.runtime.configuration.context;

import java.io.Serializable;

import de.ars.daojones.runtime.configuration.connections.Connection;

/**
 * A model for a connection.
 * 
 * @author ueberfuhr, ARS Computer und Consulting GmbH
 * @since 1.2.0
 */
public interface ConnectionModel extends ConfigurationModel<ConnectionModel.Id> {

  /**
   * The id of a connection.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH
   * @since 1.2.0
   */
  public static final class Id implements Serializable {

    /*
     * FINAL CLASS
     * because equals and hashCode have to be implemented
     */

    private static final long serialVersionUID = 1L;

    private final String applicationId;
    private final String connectionId;

    /**
     * Creates an instance.
     * 
     * @param applicationId
     *          the application id
     * @param connectionId
     *          the connection id
     */
    public Id( final String applicationId, final String connectionId ) {
      super();
      this.applicationId = applicationId;
      this.connectionId = connectionId;
    }

    /**
     * Returns the id of the connection.
     * 
     * @return the id of the connection
     */
    public String getConnectionId() {
      return connectionId;
    }

    /**
     * Returns the id of the application.
     * 
     * @return the id of the application
     */
    public String getApplicationId() {
      return applicationId;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( applicationId == null ) ? 0 : applicationId.hashCode() );
      result = prime * result + ( ( connectionId == null ) ? 0 : connectionId.hashCode() );
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
      final Id other = ( Id ) obj;
      if ( applicationId == null ) {
        if ( other.applicationId != null ) {
          return false;
        }
      } else if ( !applicationId.equals( other.applicationId ) ) {
        return false;
      }
      if ( connectionId == null ) {
        if ( other.connectionId != null ) {
          return false;
        }
      } else if ( !connectionId.equals( other.connectionId ) ) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "Id [applicationId=" + applicationId + ", connectionId=" + connectionId + "]";
    }

  }

  /**
   * Returns the connection.
   * 
   * @return the connection
   */
  public Connection getConnection();

}
