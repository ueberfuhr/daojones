package de.ars.daojones.runtime.docs.database_access_advanced;

import de.ars.daojones.runtime.beans.fields.ConverterContext;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMappedElement;
import de.ars.daojones.runtime.configuration.beans.DatabaseFieldMapping;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.DaoJonesContextConfigurator;
import de.ars.daojones.runtime.configuration.provider.DaoJonesContextConfigurator.ConfigurationHandle;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.query.FieldComparison;
import de.ars.daojones.runtime.query.Query;

@SuppressWarnings("unused")
public class CodeSnippets {

  public void contextConfiguration(final DaoJonesContext djContext,
          final ConfigurationSource configurationSource) throws ConfigurationException {
    // -1-
    // Create the instance
    final DaoJonesContextConfigurator configurator = new DaoJonesContextConfigurator(
            djContext);
    // Configure the context by specifying a single or multiple configuration sources
    final ConfigurationHandle handle = configurator.configure(configurationSource);
    try {
      // do anything else... (access the database)
    } finally {
      // deconfigure the context
      configurator.deconfigure(handle);
      // BE AWARE: the handle is not valid anymore, do not use it again
    }
  }

  public void converterContext(final ConverterContext context) throws DataAccessException {
    // -1-
    // Get the bean that contains the field
    final Object bean = context.getBean();
    // Get connection to access database during conversion
    final Connection<Person> con = context.getConnectionProvider().getConnection(
            Person.class);
    // Get the field mapping (part of Bean Model)
    final DatabaseFieldMappedElement descriptor = context.getDescriptor();
    final DatabaseFieldMapping fieldMapping = descriptor.getFieldMapping();
    // Get the name of the (database) field
    final String field = fieldMapping.getName();
    // Get the type of the target within the bean
    final Class<?> targetType = context.getTargetType();
  }

  public void familyComparison() {
    // -1-
    final Person family = new Person();
    family.setLastName("Mueller");
    final Query query = Query.create().only(
            new FieldComparison<Person>("person", new FamilyComparison(), family));
  }
}
