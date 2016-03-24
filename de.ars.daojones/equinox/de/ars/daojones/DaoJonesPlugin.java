package de.ars.daojones;

import java.util.Collection;

import de.ars.daojones.connections.ApplicationContext;
import de.ars.daojones.connections.ApplicationContextFactory;
import de.ars.daojones.connections.Connection;
import de.ars.daojones.connections.ConnectionFactoryMetaData;
import de.ars.daojones.internal.Activator;
import de.ars.daojones.internal.connections.ConnectionFactoryManager;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;

/**
 * A simple class that can be used to get connections.
 * 
 * @author Ralf Zahn
 */
public abstract class DaoJonesPlugin {

  /**
   * The plug-in ID
   */
  public static final String PLUGIN_ID = Activator.PLUGIN_ID;

  /**
   * Returns the {@link ApplicationContext} with the given id.
   * 
   * @param id
   *          the id
   * @return the {@link ApplicationContext}
   * @see ApplicationContextFactory#getApplicationContext(String)
   */
  public static ApplicationContext getApplicationContext( String id ) {
    return ApplicationContextFactory.getInstance().getApplicationContext( id );
  }

  /**
   * Returns the connection for the given DaoJones bean class.
   * 
   * @param <T>
   *          the type of the DaoJones bean
   * @param applicationContextId
   *          the id of the {@link ApplicationContext}
   * @param c
   *          the class of the DaoJones bean
   * @return the connection
   * @throws DataAccessException
   * @see Connection#get(ApplicationContext, Class)
   */
  public static <T extends Dao> Connection<T> getConnection(
      String applicationContextId, Class<T> c ) throws DataAccessException {
    return Connection.get( getApplicationContext( applicationContextId ), c );
  }

  /**
   * Returns the connection factories that are registered at the platform.
   * 
   * @return the connection factories that are registered at the platform
   */
  public static Collection<ConnectionFactoryMetaData> getConnectionFactories() {
    return ConnectionFactoryManager.getInstance().getFactories().values();
  }

}
