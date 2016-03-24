package de.ars.daojones.internal.runtime.connections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import de.ars.daojones.internal.runtime.configuration.context.DaoJonesContextConfigImpl;
import de.ars.daojones.internal.runtime.configuration.context.EventManager;
import de.ars.daojones.internal.runtime.security.CredentialVaultManager;
import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.internal.runtime.utilities.PropertiesHelper;
import de.ars.daojones.runtime.configuration.connections.Cache;
import de.ars.daojones.runtime.configuration.context.CacheFactoryModel;
import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.connections.events.ConnectionEvent;
import de.ars.daojones.runtime.connections.events.ConnectionEventType;
import de.ars.daojones.runtime.context.ApplicationContext;
import de.ars.daojones.runtime.spi.cache.CacheException;
import de.ars.daojones.runtime.spi.cache.CacheFactory;
import de.ars.daojones.runtime.spi.cache.CallbackHandlerException;
import de.ars.daojones.runtime.spi.database.ConnectionBuildException;
import de.ars.daojones.runtime.spi.database.ConnectionContext;
import de.ars.daojones.runtime.spi.database.ConnectionFactory;
import de.ars.daojones.runtime.spi.database.CredentialVault;

/**
 * A manager class that provides access to connections that are created from a
 * connection model. There is one connection manager per application context.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public final class ConnectionManager implements ConnectionProvider {

  private static final Messages logger = Messages.create( "connections.ConnectionManager" );

  private static final String DEFAULT_CONNECTION_KEY = null;

  private final Map<Class<?>, Connection<?>> connectionByClass = new HashMap<Class<?>, Connection<?>>();
  private final DaoJonesContextConfigImpl configuration;
  private final String application;
  private final EventManager eventManager;

  public ConnectionManager( final DaoJonesContextConfigImpl configuration, final String application,
          final EventManager eventManager ) {
    super();
    this.configuration = configuration;
    this.application = application;
    this.eventManager = eventManager;
  }

  /**
   * Returns the DaoJones configuration.
   * 
   * @return the DaoJones configuration
   */
  protected DaoJonesContextConfigImpl getConfiguration() {
    return configuration;
  }

  @Override
  public String getApplicationId() {
    return application;
  }

  @Override
  public ConnectionModel getConnectionModel( final Class<?> c ) {
    // 1) Create map from class name to connection model.
    final Map<String, ConnectionModel> modelByClass = new HashMap<String, ConnectionModel>();
    for ( final ConnectionModel model : getConfiguration().getConnectionModelManager().getModels() ) {
      if ( getApplicationId().equals( model.getId().getApplicationId() ) ) {
        if ( model.getConnection().isDefault() ) {
          modelByClass.put( ConnectionManager.DEFAULT_CONNECTION_KEY, model );
        } else {
          final Collection<String> assignedBeanTypes = model.getConnection().getAssignedBeanTypes();
          // if specific connection for class
          if ( null != assignedBeanTypes ) {
            for ( final String clz : assignedBeanTypes ) {
              modelByClass.put( clz, model );
            }
          }
        }
      }
    }
    // 2) Search for model
    return ConnectionManager.findConnectionModel( c, modelByClass );
  }

  @Override
  @SuppressWarnings( "unchecked" )
  public <T> Connection<T> getConnection( final Class<T> c ) throws DataAccessException {
    try {
      final Class<?> key = c;
      synchronized ( connectionByClass ) {
        Connection<T> result = ( Connection<T> ) connectionByClass.get( key );
        if ( null == result ) {
          try {
            final ConnectionModel model = getConnectionModel( c );
            result = null != model ? createConnection( model, c ) : null;
            connectionByClass.put( key, result );
          } catch ( final ConnectionBuildException e ) {
            throw new DataAccessException( e );
          }
        }
        return result;
      }
    } catch ( final ConfigurationException e ) {
      throw new DataAccessException( e );
    }
  }

  /**
   * Creates a connection.
   * 
   * @param <T>
   *          the bean type
   * @param model
   *          the connection model
   * @param beanType
   *          the bean type
   * @return the connection
   * @throws ConnectionBuildException
   * @throws ConfigurationException
   */
  @SuppressWarnings( { "unchecked", "rawtypes" } )
  protected <T> Connection<T> createConnection( final ConnectionModel model, final Class<T> beanType )
          throws ConnectionBuildException, ConfigurationException {
    try {
      // Create Connection Factory
      final ConnectionFactoryModel cfm = getConfiguration().getConnectionFactoryModelManager().getModel(
              model.getConnection().getType() );
      if ( null == cfm ) {
        throw new ConnectionBuildException( ConnectionManager.logger.get( "error.missingConnectionFactory", model
                .getConnection().getType() ) );
      }
      final ConnectionFactory factory = cfm.getInstance();
      // create credential vault
      final CredentialVaultManager credentialVaultManager = getConfiguration().getCredentialVaultManager();
      final CredentialVault credentialVault = credentialVaultManager.getCredentialVault( model );
      // Create Connection Context
      final ConnectionContext<T> connectionContext = new ConnectionContext<T>( this, model, beanType,
              getConfiguration().getBeanModelManager(), getConfiguration(), credentialVault );
      // Create database driver's connection
      final de.ars.daojones.runtime.spi.database.Connection<T> driver = factory.createConnection( connectionContext );
      // Create cache driver's cache
      final Cache cache = model.getConnection().getCache();
      final CacheBuilder cacheBuilder;
      if ( null != cache ) {
        final CacheFactoryModel cacheFM = getConfiguration().getCacheFactoryModelManager().getModel( cache.getType() );
        if ( null == cacheFM ) {
          throw new ConnectionBuildException( ConnectionManager.logger.get( "error.missingCacheFactory",
                  cache.getType() ) );
        }
        final CacheFactory cf = cacheFM.getInstance();
        final Properties cacheProperties = PropertiesHelper.createFrom( cache.getProperties() );
        cacheBuilder = new CacheBuilder() {

          @Override
          public <K, V> de.ars.daojones.runtime.spi.cache.Cache<K, V> createCache( final Class<K> keyType,
                  final Class<V> valueType ) throws CacheException {
            return cf.createCache( new CacheContextImpl<K, V>( model.getId(), cacheProperties, keyType, valueType ) );
          }
        };
      } else {
        cacheBuilder = null;
      }

      // Create DaoJones connection and inject driver's connection
      final Connection<T> con = new CachedConnectionImpl( connectionContext, driver, cacheBuilder );
      eventManager.fireEvent( new ConnectionEvent( ConnectionEventType.CONNECTION_CREATED, this, model ) );
      return new ConnectionWrapper<T>( con ) {

        @Override
        public void close() throws DataAccessException {
          eventManager.fireEvent( new ConnectionEvent( ConnectionEventType.CONNECTION_CLOSED, this, model ) );
          super.close();
        }

      };
    } catch ( final CallbackHandlerException e ) {
      throw new ConnectionBuildException( e );
    }
  }

  /* ***************************************************************************
   *   F I N D   C O N N E C T I O N   M O D E L   B Y   B E A N   C L A S S   * 
   *************************************************************************** */

  private static ConnectionModel findConnectionModel( final Class<?> c, final Map<String, ConnectionModel> modelByClass ) {
    if ( null == c || c.getName().equals( Object.class.getName() ) ) {
      return null;
    }
    final ConnectionModel model = modelByClass.get( c.getName() );
    if ( null != model ) {
      return model;
    }
    // Search for superclass
    final ConnectionModel superClassInfo = ConnectionManager.findConnectionModel( c.getSuperclass(), modelByClass );
    if ( null != superClassInfo ) {
      return superClassInfo;
    }
    // Search for interfaces
    for ( final Class<?> i : c.getInterfaces() ) {
      final ConnectionModel interfaceInfo = ConnectionManager.findConnectionModel( i, modelByClass );
      if ( null != interfaceInfo ) {
        return interfaceInfo;
      }
    }
    // give up
    return modelByClass.get( ConnectionManager.DEFAULT_CONNECTION_KEY );
  }

  /**
   * Destroys all connections.
   * 
   * @throws DataAccessException
   */
  public void destroyConnections() throws DataAccessException {
    synchronized ( connectionByClass ) {
      for ( final Connection<?> con : connectionByClass.values() ) {
        if ( null != con ) {
          con.close();
        }
      }
      connectionByClass.clear();
    }
  }

  @Override
  public ApplicationContext getApplicationContext() {
    return new ApplicationContext( this, getConfiguration().getBeanModelManager(), getConfiguration() );
  }

}
