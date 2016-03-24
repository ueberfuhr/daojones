package de.ars.daojones.internal.runtime.test.datahandler;

import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;
import de.ars.daojones.runtime.test.data.Entry;
import de.ars.daojones.runtime.test.spi.database.DataHandler;
import de.ars.daojones.runtime.test.spi.database.TestModelIndex;

public class IdentificatorDataHandler implements DataHandler<Identificator> {

  @Override
  public Class<? extends Identificator> getKeyType() {
    return Identificator.class;
  }

  @Override
  public Identificator convertRead( final FieldContext<Identificator> context, final TestModelIndex index,
          final String value ) throws DataAccessException, UnsupportedFieldTypeException {
    try {
      final Entry entry = null != value ? index.findEntry( value ) : null;
      return null != entry ? index.createIdentificator( entry ) : null;
    } catch ( final ConfigurationException e ) {
      throw new DataAccessException( e );
    }
  }

  @Override
  public String convertWrite( final FieldContext<Identificator> context, final TestModelIndex index,
          final Identificator value ) throws DataAccessException, UnsupportedFieldTypeException {
    return null != value ? String.valueOf( value.getId( index.getApplication() ) ) : null;
  }

}
