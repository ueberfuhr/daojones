package de.ars.daojones.internal.runtime.utilities;

import java.util.Properties;

import de.ars.daojones.runtime.configuration.connections.Property;

public final class PropertiesHelper {

  private PropertiesHelper() {
    super();
  }

  public static Properties createFrom( final Iterable<Property> properties ) {
    final Properties result = new Properties();
    if ( null != properties ) {
      for ( final Property p : properties ) {
        result.setProperty( p.getName(), p.getValue() );
      }
    }
    return result;
  }

}
