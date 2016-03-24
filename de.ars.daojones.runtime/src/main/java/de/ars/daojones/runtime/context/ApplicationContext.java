package de.ars.daojones.runtime.context;

import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorProvider;

public class ApplicationContext {

  protected final ConnectionProvider connectionProvider;
  protected final BeanModelManager beanModelManager;
  protected final BeanAccessorProvider beanAccessorProvider;

  /**
   * Constructor.
   * 
   * @param connectionProvider
   *          the connection provider
   * @param beanModelManager
   *          the bean model manager
   * @param beanAccessorProvider
   *          the bean accessor provider
   */
  public ApplicationContext( final ConnectionProvider connectionProvider, final BeanModelManager beanModelManager,
          final BeanAccessorProvider beanAccessorProvider ) {
    super();
    this.connectionProvider = connectionProvider;
    this.beanModelManager = beanModelManager;
    this.beanAccessorProvider = beanAccessorProvider;
  }

  /**
   * Returns the application id.
   * 
   * @return the application id
   */
  public String getApplicationId() {
    return getConnectionProvider().getApplicationId();
  }

  /**
   * Returns the connection provider.
   * 
   * @returnthe connection provider
   */
  public ConnectionProvider getConnectionProvider() {
    return connectionProvider;
  }

  /**
   * Returns the bean model manager.
   * 
   * @return the bean model manager
   */
  public BeanModelManager getBeanModelManager() {
    return beanModelManager;
  }

  /**
   * Returns the bean accessor provider.
   * 
   * @return the bean accessor provider
   */
  public BeanAccessorProvider getBeanAccessorProvider() {
    return beanAccessorProvider;
  }

}