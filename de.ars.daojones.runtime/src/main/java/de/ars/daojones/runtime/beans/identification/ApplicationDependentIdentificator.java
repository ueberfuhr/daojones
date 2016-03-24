package de.ars.daojones.runtime.beans.identification;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An identificator that manages multiple identificators by the application id.
 * This allows to use a single bean with a single id field to get used by
 * multiple applications. For each application, it has its own id.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class ApplicationDependentIdentificator implements Identificator, Iterable<Map.Entry<String, Identificator>> {

  private static final long serialVersionUID = 1L;

  private final Map<String, Identificator> identificators = new HashMap<String, Identificator>();

  public Identificator get( final String application ) {
    return identificators.get( application );
  }

  public void set( final String application, final Identificator identificator ) {
    identificators.put( application, identificator );
  }

  @Override
  public Serializable getId( final String application ) {
    final Identificator identificator = get( application );
    return null != identificator ? identificator.getId( application ) : null;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( identificators == null ) ? 0 : identificators.hashCode() );
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
    final ApplicationDependentIdentificator other = ( ApplicationDependentIdentificator ) obj;
    if ( identificators == null ) {
      if ( other.identificators != null ) {
        return false;
      }
    } else if ( !identificators.equals( other.identificators ) ) {
      return false;
    }
    return true;
  }

  @Override
  public Iterator<Entry<String, Identificator>> iterator() {
    return Collections.unmodifiableMap( identificators ).entrySet().iterator();
  }

}
