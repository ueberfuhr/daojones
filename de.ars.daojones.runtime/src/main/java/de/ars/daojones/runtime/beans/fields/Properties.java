package de.ars.daojones.runtime.beans.fields;

import java.util.List;

import de.ars.daojones.runtime.configuration.beans.Property;

/**
 * A helper class that manages constants and helper methods concerning field
 * properties.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public abstract class Properties {

  /**
   * The name of the property that specifies the field type.
   */
  public static final String FIELD_TYPE_PROPERTY = "field-type";
  /**
   * The name of the property that specifies the <tt>computed</tt> flag.
   * Computed fields will be reinjected after storing or deleting a bean.
   */
  public static final String COMPUTED_PROPERTY = "computed";

  private Properties() {
    super();
  }

  /**
   * Returns a property by its name.
   * 
   * @param metadata
   *          the field properties
   * @param name
   *          the property name
   * @return the property or <tt>null</tt>, if there isn't any property with the
   *         given name
   */
  public static Property getProperty( final List<Property> metadata, final String name ) {
    for ( final Property p : metadata ) {
      if ( p.getName().equals( name ) ) {
        return p;
      }
    }
    return null;
  }

  /**
   * Returns the field type.
   * 
   * @param metadata
   *          the field properties
   * @return the field type or <tt>null</tt>, if there isn't any property
   */
  public static String getFieldType( final List<Property> metadata ) {
    final Property p = Properties.getProperty( metadata, Properties.FIELD_TYPE_PROPERTY );
    return null != p ? p.getValue() : null;
  }

  /**
   * Returns the <tt>computed</tt> flag. Computed fields will be reinjected
   * after storing or deleting a bean.
   * 
   * @param metadata
   *          the field properties
   * @return the <tt>computed</tt> flag
   */
  public static boolean isComputed( final List<Property> metadata ) {
    final Property p = Properties.getProperty( metadata, Properties.COMPUTED_PROPERTY );
    return null != p ? Boolean.valueOf( p.getValue() ) : false;
  }

}
