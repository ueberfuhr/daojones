package de.ars.daojones.internal.runtime.test.datahandler;

import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;
import de.ars.daojones.runtime.test.spi.database.DataHandler;
import de.ars.daojones.runtime.test.spi.database.TestModelIndex;

public class StringDataHandler implements DataHandler<String> {

  @Override
  public Class<? extends String> getKeyType() {
    return String.class;
  }

  @Override
  public String convertRead( final FieldContext<String> context, final TestModelIndex index, final String value )
          throws DataAccessException, UnsupportedFieldTypeException {
    return value;
  }

  @Override
  public String convertWrite( final FieldContext<String> context, final TestModelIndex index, final String value )
          throws DataAccessException, UnsupportedFieldTypeException {
    return value;
  }

}
