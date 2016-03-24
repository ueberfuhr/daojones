package de.ars.daojones.runtime.query;

import java.lang.reflect.Array;
import java.util.Collection;

import de.ars.daojones.runtime.beans.annotations.Field;
import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.ApplicationContext;

/**
 * A criterion to search for empty fields in the database.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class IsEmpty extends AbstractFieldSearchCriterion {

  private static final long serialVersionUID = -1L;

  /**
   * Creates an instance.
   * 
   * @param field
   *          the id of the field.
   * @see Field#id()
   */
  public IsEmpty( final String field ) {
    super( field );
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append( " EMPTY [" ).append( super.toString() ).append( "] " );
    return builder.toString();
  }

  @SuppressWarnings( "rawtypes" )
  @Override
  public boolean matches( final ApplicationContext ctx, final Object bean ) throws ConfigurationException,
          FieldAccessException, DataAccessException {
    final Object value = getFieldValue( ctx, bean );
    if ( null == value ) {
      return true;
    }
    if ( value instanceof String && ( ( String ) value ).length() == 0 ) {
      return true;
    }
    if ( value instanceof Collection && ( ( Collection ) value ).isEmpty() ) {
      return true;
    }
    if ( value.getClass().isArray() && Array.getLength( value ) == 0 ) {
      return true;
    }
    return false;
  }

}
