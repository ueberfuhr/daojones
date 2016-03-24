package de.ars.daojones.internal.runtime.configuration.context;

import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;

import de.ars.daojones.internal.runtime.connections.ConnectionManager;
import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModelManager;
import de.ars.daojones.runtime.configuration.context.DaoJonesContextConfiguration;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.connections.events.ConnectionEventListener;
import de.ars.daojones.runtime.context.Application;
import de.ars.daojones.runtime.context.ApplicationContext;
import de.ars.daojones.runtime.context.InjectionContext;
import de.ars.daojones.runtime.context.InjectionContextFactory;
import de.ars.daojones.runtime.context.InjectionEngine;
import de.ars.daojones.runtime.context.InjectionException;
import de.ars.daojones.runtime.spi.beans.fields.AlreadyInjectingException;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessor;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorContext;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorProvider;
import de.ars.daojones.runtime.spi.beans.fields.DatabaseAccessor;

public class ApplicationContextImpl implements Application, InjectionEngine, BeanAccessorProvider {

  private static final Messages logger = Messages.create( "configuration.context.ApplicationContext" );

  private final String id;
  private final DaoJonesContextImpl ctx;
  private final ConnectionManager connectionManager;

  public ApplicationContextImpl( final String id, final DaoJonesContextImpl ctx, final EventManager eventManager ) {
    super();
    this.id = id;
    this.ctx = ctx;
    connectionManager = new ConnectionManager( ctx.getConfiguration(), id, eventManager );
  }

  @Override
  public String getApplicationId() {
    return id;
  }

  @Override
  public <T> Connection<T> getConnection( final Class<T> c ) throws DataAccessException {
    return connectionManager.getConnection( c );
  }

  @Override
  public ConnectionModel getConnectionModel( final Class<?> c ) {
    return connectionManager.getConnectionModel( c );
  }

  @Override
  public void addConnectionEventListener( final ConnectionEventListener l ) {
    ctx.getEventManager().addConnectionModelListener( l, getApplicationId() );
  }

  @Override
  public void removeConnectionEventListener( final ConnectionEventListener l ) {
    ctx.getEventManager().removeConnectionModelListener( l, getApplicationId() );
  }

  /**
   * Destroys connections.
   */
  protected void destroyConnections() {
    try {
      connectionManager.destroyConnections();
    } catch ( final DataAccessException e ) {
      ApplicationContextImpl.logger.log( Level.WARNING, e, "error.destroyConnections" );
    }
  }

  @Override
  public void close() throws DataAccessException {
    // First, clear model
    final DaoJonesContextConfiguration config = ctx.getConfiguration();
    config.getApplicationModelManager().deregister( getApplicationId() );
    final ConnectionModelManager connectionModelManager = config.getConnectionModelManager();
    final Collection<ConnectionModel> connections = new HashSet<ConnectionModel>( connectionModelManager.getModels() );
    for ( final ConnectionModel connection : connections ) {
      if ( getApplicationId().equals( connection.getId().getApplicationId() ) ) {
        connectionModelManager.deregister( connection );
      }
    }
    // Then, close connections
    destroyConnections();

    // Last, but not least, remove all listeners
    ctx.getEventManager().clear( getApplicationId() );
  }

  /*
   * non-public API
   */
  public DaoJonesContextImpl getDaoJonesContext() {
    return ctx;
  }

  @Override
  public BeanAccessor getBeanAccessor() {
    return getDaoJonesContext().getBeanAccessor();
  }

  /* *************************
   *    I N J E C T I O N    *
   ************************* */
  @Override
  public InjectionEngine getInjectionEngine() {
    return this;
  }

  protected <T> BeanAccessorContext<T> createBeanAccessorContext( final Class<T> beanClass,
          final InjectionContext context ) throws ConfigurationException, DataAccessException {
    final BeanModelManager bmm = getDaoJonesContext().getConfiguration().getBeanModelManager();
    final BeanModel model = bmm.getEffectiveModel( getApplicationId(), beanClass );
    if ( context instanceof DatabaseAccessorProvidingContext ) {
      final DatabaseAccessor db = ( ( DatabaseAccessorProvidingContext ) context ).getDb( getDaoJonesContext(),
              getApplicationId() );
      return new BeanAccessorContext<T>( beanClass, model, this, db, bmm );
    } else {
      throw new IllegalArgumentException( ApplicationContextImpl.logger.get( "error.injectioncontext",
              InjectionContextFactory.class.getName() ) );
    }
  }

  @Override
  public <T> T createBeanInstance( final Class<T> beanClass, final InjectionContext context ) throws InjectionException {
    try {
      return getBeanAccessor().createBeanInstance( createBeanAccessorContext( beanClass, context ) );
    } catch ( final DataAccessException e ) {
      throw new InjectionException( e );
    } catch ( final FieldAccessException e ) {
      throw new InjectionException( e );
    } catch ( final ConfigurationException e ) {
      throw new InjectionException( e );
    }
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public void inject( final Object bean, final InjectionContext context ) throws InjectionException {
    try {
      getBeanAccessor().inject( createBeanAccessorContext( ( Class<Object> ) bean.getClass(), context ), bean );
    } catch ( final DataAccessException e ) {
      throw new InjectionException( e );
    } catch ( final FieldAccessException e ) {
      throw new InjectionException( e );
    } catch ( final ConfigurationException e ) {
      throw new InjectionException( e );
    } catch ( final AlreadyInjectingException e ) {
      // Do nothing
    }
  }

  @Override
  public ApplicationContext getApplicationContext() {
    return new ApplicationContext( this, getDaoJonesContext().getConfiguration().getBeanModelManager(), this );
  }

}
