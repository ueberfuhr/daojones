package de.ars.daojones.runtime.connections;

import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.context.ApplicationContext;

/**
 * An interface for all objects that provide access to connections of a single
 * application context.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 1.2
 */
public interface ConnectionProvider {

  /**
   * Returns the id of the application context.
   * 
   * @return the id of the application context
   */
  String getApplicationId();

  /**
   * Returns the application context
   * 
   * @return the application context
   */
  public ApplicationContext getApplicationContext();

  /**
   * Returns the connection model for a bean class.
   * 
   * @param c
   *          the bean class
   * @return the connection model
   * @since 2.0
   */
  ConnectionModel getConnectionModel( Class<?> c );

  /**
   * Returns the connection for a bean class. This method is delegated to
   * {@link #getConnection(ConnectionModel)} by using
   * {@link #getConnectionModel(Class)}.
   * 
   * @param <T>
   *          the bean type
   * @param c
   *          the bean class
   * @return the connection for the bean class
   */
  <T> Connection<T> getConnection( Class<T> c ) throws DataAccessException;

}
