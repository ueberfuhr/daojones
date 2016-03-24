package de.ars.daojones.runtime.configuration.provider;

import java.util.List;

import de.ars.daojones.runtime.beans.fields.Converter;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping;
import de.ars.daojones.runtime.configuration.beans.InstanceProvidingElement.InstanceByClassProvider;
import de.ars.daojones.runtime.configuration.beans.LocalConverter;
import de.ars.daojones.runtime.configuration.beans.Property;

/**
 * A super class for field annotation handlers that provides utility methods.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public abstract class AbstractFieldAnnotationHandler implements FieldAnnotationHandler {

  /**
   * Adds a property to the field mapping.
   * 
   * @param mapping
   *          the field mapping
   * @param name
   *          the property name
   * @param value
   *          the property value
   * @throws ConfigurationException
   */
  protected void addProperty( final DatabaseFieldMapping mapping, final String name, final String value )
          throws ConfigurationException {
    final List<Property> metadata = mapping.getMetadata();
    final Property property = new Property();
    property.setName( name );
    property.setValue( value );
    metadata.add( property );
  }

  /**
   * Adds a property to the field mapping. The property has a value of
   * <tt>null</tt>, which can be named as a <i>Marker Property</i>.
   * 
   * @param mapping
   *          the field mapping
   * @param name
   *          the property name
   * @throws ConfigurationException
   */
  protected void addProperty( final DatabaseFieldMapping mapping, final String name ) throws ConfigurationException {
    addProperty( mapping, name, null );
  }

  /**
   * Sets the converter to the field mapping.
   * 
   * @param mapping
   *          the field mapping
   * @param converter
   *          the converter
   * @throws ConfigurationException
   */
  protected void setConverter( final DatabaseFieldMapping mapping, final Class<? extends Converter> converter )
          throws ConfigurationException {
    final LocalConverter localConverter = new LocalConverter();
    localConverter.setInstanceProvider( new InstanceByClassProvider<Converter>( converter ) );
    mapping.setConverter( localConverter );
  }

}
