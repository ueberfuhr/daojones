package de.ars.daojones.internal.drivers.notes.datahandler;

import de.ars.daojones.drivers.notes.DataHandlerException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;

public class EnumDataHandler extends InternalAbstractDataHandler<Enum<?>, String> {

  @SuppressWarnings( { "unchecked", "rawtypes" } )
  @Override
  public Class<? extends Enum<?>> getKeyType() {
    return ( Class ) Enum.class;
  }

  @SuppressWarnings( { "unchecked", "rawtypes" } )
  @Override
  public Enum<?> convertAfterRead( final DataHandlerContext<?> context, final FieldContext<Enum<?>> fieldContext,
          final String value ) throws DataHandlerException {
    return Enum.valueOf( ( Class ) fieldContext.getType(), value );
  }

  @Override
  public String convertForUpdate( final DataHandlerContext<?> context, final FieldContext<Enum<?>> fieldContext,
          final Enum<?> value ) throws DataHandlerException {
    return null != value ? value.name() : null;
  }

}
