package de.ars.daojones.runtime.beans.identification;

import java.io.Serializable;

/**
 * A simple identificator that provides a simple {@link String} representation.
 * This is only restricted to single-application scenarios.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public class StringIdentificator implements Identificator {

  private static final long serialVersionUID = 1L;

  private final String value;

  /**
   * Creates the instance.
   * 
   * @param value
   *          the string representation
   */
  public StringIdentificator( final String value ) {
    super();
    this.value = value;
  }

  @Override
  public Serializable getId( final String application ) {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( value == null ) ? 0 : value.hashCode() );
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
    final StringIdentificator other = ( StringIdentificator ) obj;
    if ( value == null ) {
      if ( other.value != null ) {
        return false;
      }
    } else if ( !value.equals( other.value ) ) {
      return false;
    }
    return true;
  }

}
