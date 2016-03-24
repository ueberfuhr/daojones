package de.ars.daojones.runtime.configuration.context;

/**
 * A class that bundles configuration components.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public interface DaoJonesContextConfiguration {

  /**
   * Returns the application model manager. Applications do not have to be
   * described by a model, they can be created automatically, whenever a
   * connection belongs to the application's id. Using an application model has
   * the following advantages:
   * <ul>
   * <li>The application model provides human-readable information to display in
   * the UI.</li>
   * <li>The application model provides such information before the application
   * context is requested for the first time.</li>
   * </ul>
   * 
   * @return the application model manager
   */
  public ApplicationModelManager getApplicationModelManager();

  /**
   * Returns the connection model manager for an application. Use this manager
   * to register connection models that then can be requested by your
   * application.
   * 
   * @return the connection model manager
   */
  public ConnectionModelManager getConnectionModelManager();

  /**
   * Returns the bean model manager for an application. Use this manager to
   * register bean models for all types that then can be requested by your
   * application.
   * 
   * @return the bean model manager
   */
  public BeanModelManager getBeanModelManager();

  /**
   * Returns the connection factory model manager. It is necessary to register
   * the connection factory (a.k.a. &quot;driver&quot;) before connections can
   * be requested for a kind of database.
   * 
   * @return the connection factory model manager
   */
  public ConnectionFactoryModelManager getConnectionFactoryModelManager();

  /**
   * Returns the cache factory model manager. Use this manager to register cache
   * factories that support caching of DaoJones beans for specific cache types.
   * 
   * @return the cache factory model manager
   */
  public CacheFactoryModelManager getCacheFactoryModelManager();

  /**
   * Returns the callback handler factory model manager. Use this manager to
   * register callback handler factories that support authentication callbacks
   * for secured connections.
   * 
   * @return the callback handler factory model manager
   */
  public CallbackHandlerFactoryModelManager getCallbackHandlerFactoryModelManager();

}
