package de.ars.daojones.runtime.configuration.context;

import java.io.Serializable;

import de.ars.daojones.runtime.configuration.beans.GlobalConverter;

/**
 * A model for a bean type.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public interface GlobalConverterModel extends ConfigurationModel<GlobalConverterModel.Id> {

  /**
   * The id of a bean.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
   */
  public static final class Id implements Serializable {

    /*
     * FINAL CLASS
     * because equals and hashCode have to be implemented
     */

    private static final long serialVersionUID = 1L;

    private final String applicationId;
    private final String typeId;

    /**
     * Creates an instance.
     * 
     * @param applicationId
     *          the application id
     * @param typeId
     *          the type id
     */
    public Id( final String applicationId, final String typeId ) {
      super();
      this.applicationId = applicationId;
      this.typeId = typeId;
    }

    /**
     * Returns the id of the type.
     * 
     * @return the id of the type
     */
    public String getTypeId() {
      return typeId;
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
      result = prime * result + ( ( typeId == null ) ? 0 : typeId.hashCode() );
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
      if ( typeId == null ) {
        if ( other.typeId != null ) {
          return false;
        }
      } else if ( !typeId.equals( other.typeId ) ) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "Id [applicationId=" + applicationId + ", typeId=" + typeId + "]";
    }

  }

  /**
   * Returns the converter.
   * 
   * @return the converter
   */
  public GlobalConverter getConverter();

}
