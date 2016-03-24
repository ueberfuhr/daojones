package de.ars.daojones.internal.runtime.test.datahandler;

import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;
import de.ars.daojones.runtime.test.spi.database.DataHandler;
import de.ars.daojones.runtime.test.spi.database.TestModelIndex;

public class EnumDataHandler implements DataHandler<Enum<?>> {

  @SuppressWarnings( { "unchecked", "rawtypes" } )
  @Override
  public Class<? extends Enum<?>> getKeyType() {
    return ( Class ) Enum.class;
  }

  @SuppressWarnings( { "unchecked", "rawtypes" } )
  @Override
  public Enum<?> convertRead( final FieldContext<Enum<?>> context, final TestModelIndex index, final String value )
          throws DataAccessException, UnsupportedFieldTypeException {
    return Enum.valueOf( ( Class ) context.getType(), value );
  }

  @Override
  public String convertWrite( final FieldContext<Enum<?>> context, final TestModelIndex index, final Enum<?> value )
          throws DataAccessException, UnsupportedFieldTypeException {
    return null != value ? value.name() : null;
  }

}
