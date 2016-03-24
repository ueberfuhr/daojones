package de.ars.daojones.internal.runtime.osgi;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import de.ars.daojones.internal.runtime.osgi.DaoJonesContextConfigurationTracker.TrackedObject;
import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.DaoJonesContextConfigurator;
import de.ars.daojones.runtime.configuration.provider.DaoJonesContextConfigurator.ConfigurationHandle;
import de.ars.daojones.runtime.context.DaoJonesContext;

abstract class DaoJonesContextConfigurationTracker<T> implements ServiceTrackerCustomizer<T, TrackedObject>, Closeable {

  private static final Messages nls = Messages.create( "osgi.DaoJonesContextConfigurationTracker" );

  private final DaoJonesContextConfigurator configurator;
  private final ServiceTracker<T, TrackedObject> tracker;
  private final BundleContext bundle;

  public DaoJonesContextConfigurationTracker( final BundleContext bundle, final DaoJonesContext context,
          final Class<T> clazz ) {
    super();
    this.bundle = bundle;
    this.configurator = new DaoJonesContextConfigurator( context );
    this.tracker = new ServiceTracker<T, TrackedObject>( bundle, clazz, this );
    this.tracker.open();
  }

  @Override
  public void close() throws IOException {
    this.tracker.close();
  }

  protected abstract ConfigurationSource createConfigurationSource( T t );

  protected abstract Class<T> getServiceClass();

  /**
   * A class that encapsulated the information that is necessary to handle
   * modifications to the service.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
   * @since 2.0
   */
  public static class TrackedObject implements Serializable {

    private static final long serialVersionUID = 1L;

    private ConfigurationHandle handle;

    private TrackedObject() {
      super();
    }

    /**
     * Returns the configuration handle that is created by the
     * {@link DaoJonesContextConfigurator}.
     * 
     * @return the configuration key
     */
    public ConfigurationHandle getHandle() {
      return handle;
    }

    private void setHandle( final ConfigurationHandle handle ) {
      this.handle = handle;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( handle == null ) ? 0 : handle.hashCode() );
      return result;
    }

    @Override
    public boolean equals( final Object obj ) {
      if ( this == obj ) {
        return true;
      }
      if ( obj == null ) {
        return false;
      }
      if ( getClass() != obj.getClass() ) {
        return false;
      }
      final TrackedObject other = ( TrackedObject ) obj;
      if ( handle == null ) {
        if ( other.handle != null ) {
          return false;
        }
      } else if ( !handle.equals( other.handle ) ) {
        return false;
      }
      return true;
    }

  }

  private void handleException( final ServiceReference<T> reference, final Exception e, final String key ) {
    final Bundle provider = reference.getBundle();
    DaoJonesContextConfigurationTracker.nls.log( Level.SEVERE, e, key, // params...
            this.getServiceClass(), // {0}
            provider.getBundleId(), // {1}
            provider.getSymbolicName(), // {2}
            provider.getVersion() // {3}
            );
  }

  private void configure( final ServiceReference<T> reference, final TrackedObject trackedObject )
          throws ConfigurationException {
    final T service = bundle.getService( reference );
    final ConfigurationSource cs = createConfigurationSource( service );
    final ConfigurationHandle handle = configurator.configure( cs );
    trackedObject.setHandle( handle );
  }

  private void unconfigure( final TrackedObject trackedObject ) throws ConfigurationException {
    if ( null != trackedObject.getHandle() ) {
      configurator.deconfigure( trackedObject.getHandle() );
      trackedObject.setHandle( null );
    }
  }

  @Override
  public TrackedObject addingService( final ServiceReference<T> reference ) {
    final TrackedObject result = new TrackedObject();
    try {
      configure( reference, result );
    } catch ( final ConfigurationException e ) {
      handleException( reference, e, "error.adding" );
    }
    return result;
  }

  @Override
  public void modifiedService( final ServiceReference<T> reference, final TrackedObject service ) {
    try {
      unconfigure( service );
      configure( reference, service );
    } catch ( final ConfigurationException e ) {
      handleException( reference, e, "error.modified" );
    }
  }

  @Override
  public void removedService( final ServiceReference<T> reference, final TrackedObject service ) {
    try {
      unconfigure( service );
    } catch ( final ConfigurationException e ) {
      handleException( reference, e, "error.removed" );
    }
  }

}
