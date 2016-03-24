package de.ars.daojones.runtime.docs.database_access_advanced;

import de.ars.daojones.runtime.beans.fields.ConverterContext;
import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.beans.fields.LoadContext;
import de.ars.daojones.runtime.beans.fields.StoreContext;
import de.ars.daojones.runtime.beans.fields.TypeConverter;

public class PersonConverter extends TypeConverter<String, Person> {

  @Override
  protected Person toPropertyValue(final LoadContext context, final String value)
          throws FieldAccessException {
    final Person person = new Person();
    final String[] names = value.split(" ");
    person.setFirstName(names[0]);
    person.setLastName(names[1]);
    return person;
  }

  @Override
  protected String toDatabaseValue(final StoreContext context, final Person value)
          throws FieldAccessException {
    final Person person = value;
    return person.getFirstName() + " " + person.getLastName();
  }

  @Override
  protected Class<String> getDatabaseType(final ConverterContext context) {
    return String.class;
  }

}
