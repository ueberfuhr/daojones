package de.ars.daojones.internal.integration.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.context.DaoJonesContextFactory;

/**
 * CDI Producer class to create DaoJones context.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 */
@ApplicationScoped
public class DaoJonesContextProducer {
  private static final Messages bundle = Messages.create( "producers" );

  @Produces
  @Default
  @ApplicationScoped
  public DaoJonesContext createDaoJonesContext() {
    try {
      final DaoJonesContextFactory factory = new DaoJonesContextFactory();
      return factory.createContext();
    } catch ( final ConfigurationException e ) {
      // producer method should only throw unchecked exceptions
      throw new RuntimeException( e );
    }
  }

  public void closeContext( @Disposes @Default final DaoJonesContext context ) {
    try {
      context.close();
    } catch ( final DataAccessException e ) {
      // disposer method should only throw unchecked exceptions
      throw new RuntimeException( e );
    }
  }

  @Override
  public String toString() {
    return DaoJonesContextProducer.bundle.get( "producer.context.name" );
  }

}
