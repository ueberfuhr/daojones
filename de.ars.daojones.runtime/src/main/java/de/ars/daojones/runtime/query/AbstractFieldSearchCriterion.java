package de.ars.daojones.runtime.query;

import de.ars.daojones.runtime.beans.annotations.Field;
import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.context.ApplicationContext;

/**
 * A common super class for criteria that filter results by comparing a field
 * value. The field value is the value that is stored within the database. It is
 * not dependent from any converter that is used to transform the database value
 * into the Java property value.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public abstract class AbstractFieldSearchCriterion extends AbstractSearchCriterion {

  private static final long serialVersionUID = 1L;

  private final String field;

  /**
   * Creates an instance
   * 
   * @param field
   *          the id of the field.
   * @see Field#id()
   */
  public AbstractFieldSearchCriterion( final String field ) {
    super();
    this.field = field;
  }

  /**
   * Returns the id of the field.
   * 
   * @return the field the id of the field
   */
  public String getField() {
    return field;
  }

  /**
   * Reads the value of the field.
   * 
   * @param ctx
   *          the application context
   * @param bean
   *          the bean
   * @param field
   *          the name of the field
   * @throws ConfigurationException
   *           if a bean model is not configured or if a field with the given id
   *           could not be found or is write-only
   * @throws FieldAccessException
   *           if accessing the field occurs an error
   */
  protected Object getFieldValue( final ApplicationContext ctx, final Object bean ) throws ConfigurationException,
          FieldAccessException {
    return super.getFieldValue( ctx, bean, field );
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append( " FIELD[@id=\"" ).append( getField().toString() ).append( "\"] " );
    return builder.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( field == null ) ? 0 : field.hashCode() );
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
    final AbstractFieldSearchCriterion other = ( AbstractFieldSearchCriterion ) obj;
    if ( field == null ) {
      if ( other.field != null ) {
        return false;
      }
    } else if ( !field.equals( other.field ) ) {
      return false;
    }
    return true;
  }

}
