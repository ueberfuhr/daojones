package de.ars.daojones.internal.runtime.configuration.context;

import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.InjectionContext;
import de.ars.daojones.runtime.spi.beans.fields.DatabaseAccessor;

public interface DatabaseAccessorProvidingContext extends InjectionContext {

  DatabaseAccessor getDb( DaoJonesContextImpl context, String applicationId ) throws DataAccessException,
          ConfigurationException;

}
