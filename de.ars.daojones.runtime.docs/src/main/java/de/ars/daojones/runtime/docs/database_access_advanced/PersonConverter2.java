package de.ars.daojones.runtime.docs.database_access_advanced;

import de.ars.daojones.runtime.beans.fields.Converter;
import de.ars.daojones.runtime.beans.fields.LoadContext;
import de.ars.daojones.runtime.beans.fields.LoadContext.Reader;
import de.ars.daojones.runtime.beans.fields.StoreContext;
import de.ars.daojones.runtime.beans.fields.StoreContext.Writer;
import de.ars.daojones.runtime.beans.fields.UnsupportedFieldTypeException;
import de.ars.daojones.runtime.configuration.beans.Property;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping.UpdatePolicy;
import de.ars.daojones.runtime.connections.DataAccessException;

public class PersonConverter2 implements Converter {

  // Store the first name and last name into multiple fields
  @Override
  public void store(final StoreContext context, final Object value)
          throws DataAccessException, UnsupportedFieldTypeException {
    final Person person = (Person) value;
    final String field = context.getDescriptor().getFieldMapping().getName();
    final Property[] md = context.getMetadata();
    final Writer writer = context.getWriter();
    final UpdatePolicy up = UpdatePolicy.REPLACE;
    writer.storeToDatabase(field + "_firstName", String.class, person.getFirstName(), up,
            md);
    writer.storeToDatabase(field + "_lastName", String.class, person.getLastName(), up,
            md);
  }

  @Override
  public Object load(final LoadContext context) throws DataAccessException,
          UnsupportedFieldTypeException {
    final Person person = new Person();
    final String field = context.getDescriptor().getFieldMapping().getName();
    final Property[] md = context.getMetadata();
    final Reader reader = context.getReader();
    person.setFirstName(reader.readFromDatabase(field + "_firstName", String.class, md));
    person.setLastName(reader.readFromDatabase(field + "_lastName", String.class, md));
    return person;
  }
}
