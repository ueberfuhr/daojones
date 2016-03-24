package de.ars.daojones.internal.runtime.configuration.context;

import java.util.Map;

import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.connections.events.ConnectionEventListener;
import de.ars.daojones.runtime.context.Application;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessor;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorProvider;
import de.ars.daojones.runtime.spi.beans.fields.DefaultBeanAccessor;

/**
 * Default implementation of {@link DaoJonesContext}.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public class DaoJonesContextImpl implements DaoJonesContext, BeanAccessorProvider {

  private static final Messages bundle = Messages.create( "configuration.context.DaoJonesContext" );

  private final Map<String, ApplicationContextImpl> applications = new FactoryMap<String, ApplicationContextImpl>() {

    private static final long serialVersionUID = 1L;

    @Override
    protected ApplicationContextImpl create( final String key ) {
      return new ApplicationContextImpl( key, DaoJonesContextImpl.this, getEventManager() );
    }

  };
  private final EventManager eventManager = new EventManager();
  private final DaoJonesContextConfigImpl config = new DaoJonesContextConfigImpl( eventManager, this );
  private final BeanAccessor beanAccessor = new DefaultBeanAccessor();

  /**
   * Returns the event manager.
   * 
   * @return the event manager
   */
  protected EventManager getEventManager() {
    return eventManager;
  }

  @Override
  public DaoJonesContextConfigImpl getConfiguration() {
    return config;
  }

  @Override
  public Application getApplication( final String applicationContextId ) {
    return applications.get( applicationContextId );
  }

  @Override
  public void addConnectionEventListener( final ConnectionEventListener l ) {
    eventManager.addConnectionModelListener( l, null );
  }

  @Override
  public void removeConnectionEventListener( final ConnectionEventListener l ) {
    eventManager.removeConnectionModelListener( l, null );
  }

  @Override
  public void close() throws DataAccessException {
    // First, destroy models
    config.clear();

    // Then, close connections
    for ( final ApplicationContextImpl app : applications.values() ) {
      app.destroyConnections();
    }
    applications.clear();

    // Last, but not least, remove all listeners
    getEventManager().clear();

  }

  /*
   * non-public API
   */
  @Override
  public BeanAccessor getBeanAccessor() {
    return beanAccessor;
  }

  @Override
  public String toString() {
    final Package p = DaoJonesContext.class.getPackage();
    return DaoJonesContextImpl.bundle.get( "context.name", super.toString(), p.getImplementationTitle(),
            p.getImplementationVersion(), p.getImplementationVendor() );
  }
}
