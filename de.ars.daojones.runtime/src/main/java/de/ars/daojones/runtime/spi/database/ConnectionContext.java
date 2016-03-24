package de.ars.daojones.runtime.spi.database;

import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.context.ApplicationContext;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorProvider;

/**
 * The context of a connection-creation-event. This is used as a transfer object
 * for all information that a connection factory may need.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 * @param <T>
 *          the bean type
 */
/**
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 * @param <T>
 */
public class ConnectionContext<T> extends ApplicationContext {

  private static final Messages bundle = Messages.create( "spi.database.ConnectionContext" );

  private final ConnectionModel connectionModel;
  private final Class<T> beanType;
  private final BeanModel beanModel;
  private final CredentialVault credentialVault;

  /**
   * Creates an instance.
   * 
   * @param connectionProvider
   *          the connection provider
   * @param connectionModel
   *          the connection model
   * @param beanType
   *          the bean type
   * @param beanModelManager
   *          the bean model manager
   * @param beanAccessorProvider
   *          the bean accessor provider
   * @throws ConfigurationException
   */
  public ConnectionContext( final ConnectionProvider connectionProvider, final ConnectionModel connectionModel,
          final Class<T> beanType, final BeanModelManager beanModelManager,
          final BeanAccessorProvider beanAccessorProvider, final CredentialVault credentialVault )
          throws ConfigurationException {
    super( connectionProvider, beanModelManager, beanAccessorProvider );
    this.connectionModel = connectionModel;
    this.beanType = beanType;
    this.beanModel = beanModelManager.getEffectiveModel( connectionProvider.getApplicationId(), beanType );
    if ( null == this.beanModel ) {
      throw new ConfigurationException( ConnectionContext.bundle.get( "error.nomodel", connectionModel.getId()
              .getApplicationId(), beanType.getName() ) );
    }
    this.credentialVault = credentialVault;
  }

  /**
   * Returns the connection model.
   * 
   * @return the connection model
   */
  public ConnectionModel getConnectionModel() {
    return connectionModel;
  }

  /**
   * Returns the bean type.
   * 
   * @return the bean type
   */
  public Class<T> getBeanType() {
    return beanType;
  }

  /**
   * Returns the bean model.
   * 
   * @return the bean model
   */
  public BeanModel getBeanModel() {
    return beanModel;
  }

  /**
   * Returns the application id.
   * 
   * @return the application id
   */
  @Override
  public String getApplicationId() {
    return getConnectionProvider().getApplicationId();
  }

  /**
   * Returns the credential vault.
   * 
   * @return the credential vault
   */
  public CredentialVault getCredentialVault() {
    return credentialVault;
  }

}