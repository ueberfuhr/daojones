package de.ars.daojones.runtime.docs.database_access_advanced;

import java.util.List;

import de.ars.daojones.runtime.beans.fields.Converter;
import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.beans.fields.LoadContext;
import de.ars.daojones.runtime.beans.fields.StoreContext;
import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.configuration.beans.Property;
import de.ars.daojones.runtime.connections.DataAccessException;

@SuppressWarnings("unused")
public class MetaConverter implements Converter {

  @Override
  public Object load(final LoadContext context) throws FieldAccessException,
          DataAccessException, UnsupportedFieldTypeException {
    final Object result = null;
    final List<Property> metadata = context.getDescriptor().getFieldMapping()
            .getMetadata();
    for (final Property meta : metadata) {
      final String name = meta.getName();
      final String value = meta.getValue();
    }
    // ...
    return result;
  }

  @Override
  public void store(final StoreContext context, final Object value)
          throws FieldAccessException, DataAccessException, UnsupportedFieldTypeException {
    // ...
  }

}
