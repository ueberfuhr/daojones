package de.ars.daojones.internal.runtime.beans.fields;

import de.ars.daojones.runtime.beans.fields.ConverterContext;
import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.beans.fields.LoadContext;
import de.ars.daojones.runtime.beans.fields.StoreContext;
import de.ars.daojones.runtime.beans.fields.TypeConverter;

/**
 * Default implementation of a converter.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public class DefaultConverter extends TypeConverter<Object, Object> {

  @Override
  protected Object toPropertyValue( final LoadContext context, final Object value ) throws FieldAccessException {
    return value;
  }

  @Override
  protected Object toDatabaseValue( final StoreContext context, final Object value ) throws FieldAccessException {
    return value;
  }

  @SuppressWarnings( "unchecked" )
  @Override
  protected Class<Object> getDatabaseType( final ConverterContext context ) {
    return ( Class<Object> ) context.getTargetType();
  }

}
