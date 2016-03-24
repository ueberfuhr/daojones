package de.ars.daojones.internal.runtime.test.notes;

import de.ars.daojones.drivers.notes.types.Address;
import de.ars.daojones.drivers.notes.types.Principal;
import de.ars.daojones.drivers.notes.types.User;
import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;
import de.ars.daojones.runtime.test.spi.database.DataHandler;
import de.ars.daojones.runtime.test.spi.database.TestModelIndex;

public class PrincipalDataHandler implements DataHandler<Principal> {

  @Override
  public Class<? extends Principal> getKeyType() {
    return Principal.class;
  }

  @Override
  public Principal convertRead( final FieldContext<Principal> context, final TestModelIndex index, final String value )
          throws DataAccessException, UnsupportedFieldTypeException {
    if ( null == value ) {
      return null;
    } else if ( value.indexOf( '@' ) > 0 && context.getType().isAssignableFrom( Address.class ) ) {
      return Address.valueOf( value );
    } else if ( context.getType().isAssignableFrom( User.class ) ) {
      return User.valueOf( value );
    } else {
      throw new DataAccessException( new IllegalArgumentException( value ) );
    }
  }

  @Override
  public String convertWrite( final FieldContext<Principal> context, final TestModelIndex index, final Principal value )
          throws DataAccessException, UnsupportedFieldTypeException {
    return null != value ? value.toString() : null;
  }

}
