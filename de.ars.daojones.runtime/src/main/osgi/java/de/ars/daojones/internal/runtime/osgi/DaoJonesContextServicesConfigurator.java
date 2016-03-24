package de.ars.daojones.internal.runtime.osgi;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import de.ars.daojones.runtime.configuration.context.ApplicationModel;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.CacheFactoryModel;
import de.ars.daojones.runtime.configuration.context.ConfigurationModel;
import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.context.GlobalConverterModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationProvider;
import de.ars.daojones.runtime.configuration.provider.ConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.ConfigurationSourceAdapter;
import de.ars.daojones.runtime.context.DaoJonesContext;

/**
 * Instances of this class synchronize the assigned DaoJones context with the
 * OSGi Service Registry. During the binding phase, existing services are read
 * out from the platform and the context is configured. During the unbinding
 * phase, all configurations that came from OSGi services, are removed.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
class DaoJonesContextServicesConfigurator {

  private final Collection<DaoJonesContextConfigurationTracker<?>> trackers = new HashSet<DaoJonesContextConfigurationTracker<?>>();

  private final BundleContext bundle;
  private final DaoJonesContext context;
  private ServiceRegistration<DaoJonesContext> serviceRegistration;

  public DaoJonesContextServicesConfigurator( final BundleContext bundle, final DaoJonesContext context ) {
    super();
    this.bundle = bundle;
    this.context = context;
  }

  public DaoJonesContext getContext() {
    return context;
  }

  /**
   * Starts the management of the DaoJones context.
   * 
   * @param config
   *          the service configurator
   * @throws ConfigurationException
   *           if configuring the context fails
   */
  public void bind() throws ConfigurationException {
    /*
     *  allowed services are:
     *   - BeanModelNormalizer ???
     *   - ConnectionModelNormalizer ???
     *   - CommandExecutor ???
     */

    // register service trackers to configure the context
    trackers.add( new DaoJonesContextConfigurationTracker<ConfigurationSource>( bundle, context,
            ConfigurationSource.class ) {

      @Override
      protected ConfigurationSource createConfigurationSource( final ConfigurationSource t ) {
        return t;
      }

      @Override
      protected Class<ConfigurationSource> getServiceClass() {
        return ConfigurationSource.class;
      }
    } );
    trackers.add( createTrackerForModel( ConnectionFactoryModel.class ) );
    trackers.add( createTrackerForModel( CacheFactoryModel.class ) );
    trackers.add( createTrackerForModel( ApplicationModel.class ) );
    trackers.add( createTrackerForModel( ConnectionModel.class ) );
    trackers.add( createTrackerForModel( GlobalConverterModel.class ) );
    trackers.add( createTrackerForModel( BeanModel.class ) );
    // register context as service
    serviceRegistration = bundle.registerService( DaoJonesContext.class, context, null );
  }

  private <T extends ConfigurationModel<?>> DaoJonesContextConfigurationTracker<T> createTrackerForModel(
          final Class<T> modelClass ) {
    return new DaoJonesContextConfigurationTracker<T>( bundle, context, modelClass ) {

      @Override
      protected ConfigurationSource createConfigurationSource( final T t ) {
        return new ConfigurationSourceAdapter() {

          @Override
          protected <Model> ConfigurationProvider<Model> getConfigurationProviderFor(
                  final Class<Model> providerModelClass ) throws ConfigurationException {
            if ( modelClass.getName().equals( providerModelClass.getName() ) ) {
              return new ConfigurationProvider<Model>() {

                @SuppressWarnings( "unchecked" )
                @Override
                public Iterable<Model> readConfiguration() throws ConfigurationException {
                  return Arrays.asList( ( Model ) t );
                }

              };
            } else {
              return super.getConfigurationProviderFor( providerModelClass );
            }
          }
        };
      }

      @Override
      protected Class<T> getServiceClass() {
        return modelClass;
      }
    };
  }

  /**
   * Unbinds the configuration.
   * 
   * @param config
   *          the configuration
   * @throws Exception
   */
  public void unbind() throws Exception {
    bundle.ungetService( serviceRegistration.getReference() );
    serviceRegistration = null;
    for ( final Iterator<DaoJonesContextConfigurationTracker<?>> it = trackers.iterator(); it.hasNext(); it.remove() ) {
      final DaoJonesContextConfigurationTracker<?> tracker = it.next();
      tracker.close();
    }
  }

}
