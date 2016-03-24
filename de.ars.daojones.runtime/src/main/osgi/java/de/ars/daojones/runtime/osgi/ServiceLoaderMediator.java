package de.ars.daojones.runtime.osgi;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.ServiceLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;

import de.ars.daojones.internal.runtime.configuration.provider.ServiceLoaderConfigurationSource;

/**
 * A mediator for service loader configuration within OSGi. OSGi bundles are
 * isolated, so the ServiceLoader itself does not work like in standard Java
 * enviromentments. Therefore, you could inherit your activator (or delegate to
 * this instance) to get the implementations, that are loaded by the
 * serviceloader, as an OSGi service.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public class ServiceLoaderMediator implements BundleActivator {

  /*
   * OSGi 5 Enterprise introduces package org.osgi.service.serviceloader!
   */

  private final List<ServiceRegistration<?>> services = new LinkedList<ServiceRegistration<?>>();

  @SuppressWarnings( { "rawtypes", "unchecked" } )
  @Override
  public void start( final BundleContext context ) throws Exception {
    // get bundle classloader
    final Bundle bundle = context.getBundle();
    final BundleWiring bundleWiring = bundle.adapt( BundleWiring.class );
    final ClassLoader cl = null != bundleWiring ? bundleWiring.getClassLoader() : getClass().getClassLoader();
    // Read the services and register them to the platform
    for ( final Class<?> serviceClass : ServiceLoaderConfigurationSource.serviceClasses ) {
      for ( final Object impl : ServiceLoader.load( serviceClass, cl ) ) {
        services.add( context.registerService( ( Class ) serviceClass, impl, null ) );
      }
    }
  }

  @Override
  public void stop( final BundleContext context ) throws Exception {
    try {
      if ( !services.isEmpty() ) {
        for ( final ListIterator<ServiceRegistration<?>> it = services.listIterator( services.size() ); it
                .hasPrevious(); ) {
          final ServiceRegistration<?> service = it.previous();
          service.unregister();
        }
      }
    } finally {
      services.clear();
    }
  }

}
