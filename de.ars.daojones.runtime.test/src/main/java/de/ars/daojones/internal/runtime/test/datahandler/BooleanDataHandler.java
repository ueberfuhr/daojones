package de.ars.daojones.internal.runtime.test.datahandler;

import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;
import de.ars.daojones.runtime.test.spi.database.DataHandler;
import de.ars.daojones.runtime.test.spi.database.TestModelIndex;

public class BooleanDataHandler implements DataHandler<Boolean> {

  @Override
  public Class<? extends Boolean> getKeyType() {
    return Boolean.class;
  }

  @Override
  public Boolean convertRead( final FieldContext<Boolean> context, final TestModelIndex index, final String value )
          throws DataAccessException, UnsupportedFieldTypeException {
    return Boolean.parseBoolean( value );
  }

  @Override
  public String convertWrite( final FieldContext<Boolean> context, final TestModelIndex index, final Boolean value )
          throws DataAccessException, UnsupportedFieldTypeException {
    return Boolean.toString( null != value && value );
  }

}
