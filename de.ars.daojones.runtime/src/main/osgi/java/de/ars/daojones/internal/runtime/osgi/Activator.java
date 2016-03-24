package de.ars.daojones.internal.runtime.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.context.DaoJonesContextFactory;

/**
 * The bundle activator.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public class Activator implements BundleActivator {

  // The shared instance
  private static Activator plugin;

  private DaoJonesContext daoJonesContext;
  private DaoJonesContextServicesConfigurator daoJonesContextManager;

  /**
   * Returns the shared instance. If the DaoJones runtime plugin is stopped,
   * this method returns <tt>null</tt>.
   * 
   * @return the shared instance
   */
  public static Activator getDefault() {
    return Activator.plugin;
  }

  @Override
  public void start( final BundleContext bundleContext ) throws Exception {
    Activator.plugin = this;
    daoJonesContext = initializeDaoJonesContext( bundleContext );
  }

  private DaoJonesContext initializeDaoJonesContext( final BundleContext bundleContext ) throws ConfigurationException {
    final DaoJonesContextFactory dcf = new DaoJonesContextFactory();
    final DaoJonesContext context = dcf.createContext();
    daoJonesContextManager = new DaoJonesContextServicesConfigurator( bundleContext, context );
    daoJonesContextManager.bind();
    return context;
  }

  @Override
  public void stop( final BundleContext bundleContext ) throws Exception {
    try {
      if ( null != daoJonesContextManager ) {
        daoJonesContextManager.unbind();
      }
    } finally {
      daoJonesContextManager = null;
      try {
        if ( null != daoJonesContext ) {
          daoJonesContext.close();
        }
      } finally {
        daoJonesContext = null;
        Activator.plugin = null;
      }
    }
  }

  /**
   * Returns the DaoJones context.
   * 
   * @return the DaoJones context
   */
  public DaoJonesContext getDaoJonesContext() {
    return daoJonesContext;
  }

}
