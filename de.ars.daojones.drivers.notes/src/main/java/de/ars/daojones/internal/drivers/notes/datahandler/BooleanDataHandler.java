package de.ars.daojones.internal.drivers.notes.datahandler;

import de.ars.daojones.drivers.notes.DataHandlerException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;

public class BooleanDataHandler extends InternalAbstractDataHandler<Boolean, String> {

  @Override
  public Class<? extends Boolean> getKeyType() {
    return Boolean.class;
  }

  @Override
  public Boolean convertAfterRead( final DataHandlerContext<?> context, final FieldContext<Boolean> fieldContext,
          final String value ) throws DataHandlerException {
    return Boolean.parseBoolean( value );
  }

  @Override
  public String convertForUpdate( final DataHandlerContext<?> context, final FieldContext<Boolean> fieldContext,
          final Boolean value ) throws DataHandlerException {
    return Boolean.toString( null != value && value );
  }

}
