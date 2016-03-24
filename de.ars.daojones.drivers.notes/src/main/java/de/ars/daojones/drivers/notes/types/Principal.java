package de.ars.daojones.drivers.notes.types;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * This interface represents the abstract notion of a principal, which can be
 * used to represent any entity, such as a user name or an internet address.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public abstract class Principal implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Avoid creation of sub classes out of this package (project).
   */
  Principal() {
    super();
  }

  /**
   * Returns the type.
   * 
   * @return the type
   */
  public abstract PrincipalType getType();

  // Single map instead of instance variables
  // --> no need to customize hashCode(), toString(), equals() in case of additional properties
  private Map<String, String> properties = new TreeMap<String, String>();

  /**
   * Sets a property value. If the value is <tt>null</tt>, the property is
   * removed.
   * 
   * @param name
   *          the property name
   * @param value
   *          the property value
   */
  protected void setProperty( final String name, final String value ) {
    if ( null == value ) {
      properties.remove( name );
    } else {
      properties.put( name, value );
    }
  }

  /**
   * Returns the property or <tt>null</tt>, if the property was not set before.
   * 
   * @param name
   *          the property name
   * @return the property value
   */
  protected String getProperty( final String name ) {
    return properties.get( name );
  }

  /**
   * Returns a flag indicating whether this principal has a simple value (just
   * the address or user name) or a hierarchical form or anything else that
   * provides more information.
   * 
   * @return <tt>true</tt>, if this principal has a simple value
   * @see #getValue()
   */
  public abstract boolean isSimple();

  /**
   * Returns the value. This is
   * <ul>
   * <li>the name of the user (if the principal is a user) or</li>
   * <li>the RFC 821 address (if the principal is the address)</li>
   * </ul>
   * 
   * @return
   */
  public abstract String getValue();

  /* ***********************************************
   *    C O M M O N   F U N C T I O N A L I T Y    *
   *********************************************** */

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( properties == null ) ? 0 : properties.hashCode() );
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
    final Principal other = ( Principal ) obj;
    if ( properties == null ) {
      if ( other.properties != null ) {
        return false;
      }
    } else if ( !properties.equals( other.properties ) ) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    for ( final Map.Entry<String, String> entry : properties.entrySet() ) {
      if ( builder.length() > 0 ) {
        builder.append( "/" );
      }
      builder.append( entry.getKey() ).append( "=" ).append( entry.getValue() );
    }
    return builder.toString();
  }

}
