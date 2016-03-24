package de.ars.daojones.runtime.docs.database_access_advanced;

import de.ars.daojones.runtime.beans.fields.Converter;
import de.ars.daojones.runtime.beans.fields.Converts;
import de.ars.daojones.runtime.beans.fields.LoadContext;
import de.ars.daojones.runtime.beans.fields.StoreContext;
import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.connections.DataAccessException;

@Converts(Person.class)
public class PersonConverter3 implements Converter {

  @Override
  public void store(final StoreContext context, final Object value)
          throws DataAccessException, UnsupportedFieldTypeException {
    // ...
  }

  @Override
  public Object load(final LoadContext context) throws DataAccessException,
          UnsupportedFieldTypeException {
    final Person person = new Person();
    // ...
    return person;
  }

}
