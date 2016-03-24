package de.ars.daojones.runtime.query;

import java.util.Arrays;
import java.util.Collection;

import de.ars.daojones.runtime.beans.annotations.Field;
import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.ApplicationContext;

/**
 * A criterion that compares a field value.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the type of the field
 */
public class FieldComparison<T> extends AbstractFieldSearchCriterion {

  private static final long serialVersionUID = -1L;
  private final Comparison<T> comparison;
  private final T value;

  /**
   * Creates an instance.
   * 
   * @param field
   *          the id of the field.
   * @param comparison
   *          the {@link Comparison}
   * @param value
   *          the value used for comparison
   * @see Field#id()
   */
  public FieldComparison( final String field, final Comparison<T> comparison, final T value ) {
    super( field );
    this.comparison = comparison;
    this.value = value;
  }

  /**
   * Returns the {@link Comparison}.
   * 
   * @return the {@link Comparison}
   */
  public Comparison<T> getComparison() {
    return comparison;
  }

  /**
   * Returns the value used for comparison.
   * 
   * @return the value used for comparison
   */
  public T getValue() {
    return value;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * super.hashCode();
    result = PRIME * result + ( ( comparison == null ) ? 0 : comparison.hashCode() );
    result = PRIME * result + ( ( value == null ) ? 0 : value.hashCode() );
    return result;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @SuppressWarnings( "rawtypes" )
  @Override
  public boolean equals( final Object obj ) {
    if ( !super.equals( obj ) ) {
      return false;
    }
    final FieldComparison other = ( FieldComparison ) obj;
    if ( comparison == null ) {
      if ( other.comparison != null ) {
        return false;
      }
    } else if ( !comparison.equals( other.comparison ) ) {
      return false;
    }
    if ( value == null ) {
      if ( other.value != null ) {
        return false;
      }
    } else if ( !value.equals( other.value ) ) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append( " " ).append( super.toString() ).append( " " ).append( comparison ).append( " \"" ).append( value )
            .append( "\" " );
    return builder.toString();
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public boolean matches( final ApplicationContext ctx, final Object bean ) throws ConfigurationException,
          FieldAccessException, DataAccessException {
    final Object value = getFieldValue( ctx, bean );
    final Comparison<T> comparison = getComparison();
    final Class<T> typeForComparison = comparison.getType();
    final T valueForComparison;
    if ( null == value || typeForComparison.isAssignableFrom( value.getClass() ) ) {
      valueForComparison = ( T ) value;
    } else if ( Collection.class.isAssignableFrom( typeForComparison ) ) {
      if ( value.getClass().isArray() ) {
        valueForComparison = ( T ) Arrays.asList( ( Object[] ) value );
      } else {
        valueForComparison = ( T ) Arrays.asList( value );
      }
    } else {
      // ClassCastException will occur
      valueForComparison = ( T ) value;
    }
    return comparison.matches( valueForComparison, this.value );
  }

  public static void main( final String[] args ) {
    final Object o = args;
    System.out.println( Arrays.asList( ( Object[] ) o ) );
  }

}
