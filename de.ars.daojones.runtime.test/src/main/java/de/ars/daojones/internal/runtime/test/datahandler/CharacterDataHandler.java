package de.ars.daojones.internal.runtime.test.datahandler;

import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;
import de.ars.daojones.runtime.test.spi.database.DataHandler;
import de.ars.daojones.runtime.test.spi.database.TestModelIndex;

public class CharacterDataHandler implements DataHandler<Character> {

  @Override
  public Class<? extends Character> getKeyType() {
    return Character.class;
  }

  @Override
  public Character convertRead( final FieldContext<Character> context, final TestModelIndex index, final String value )
          throws DataAccessException, UnsupportedFieldTypeException {
    return null != value && value.length() > 0 ? value.charAt( 0 ) : null;
  }

  @Override
  public String convertWrite( final FieldContext<Character> context, final TestModelIndex index, final Character value )
          throws DataAccessException, UnsupportedFieldTypeException {
    return String.valueOf( value );
  }

}
