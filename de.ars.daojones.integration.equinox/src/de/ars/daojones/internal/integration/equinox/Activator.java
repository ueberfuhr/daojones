package de.ars.daojones.internal.integration.equinox;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import de.ars.daojones.integration.equinox.DaoJonesPlugin;
import de.ars.daojones.integration.equinox.Preferences;
import de.ars.daojones.integration.equinox.util.ConnectionLock;
import de.ars.daojones.runtime.configuration.connections.Connection;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModelManager;
import de.ars.daojones.runtime.configuration.provider.ConfigurationSource;
import de.ars.daojones.runtime.context.DaoJonesContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 2.0.0
 */
public class Activator extends Plugin implements IPreferenceChangeListener {

  /**
   * The plug-in ID
   */
  public static final String PLUGIN_ID = "de.ars.daojones.integration.equinox";

  private static final Logger logger = Logger.getLogger( Activator.class.getName() );

  // The shared instance
  private static Activator plugin;

  private final Preferences preferences = new Preferences( Activator.PLUGIN_ID );
  private final ConfigurationSource extensionRegistryConfiguration = new ExtensionRegistryConfigurationSource();
  private ServiceRegistration<ConfigurationSource> serviceRegistration;

  @Override
  public void start( final BundleContext context ) throws Exception {
    Activator.plugin = this;
    getPreferences().addPreferenceChangeListener( this );
    // register extension registry configuration source as a service
    this.serviceRegistration = context.registerService( ConfigurationSource.class, this.extensionRegistryConfiguration,
            null );

  }

  @Override
  public void stop( final BundleContext context ) throws Exception {
    if ( null != this.serviceRegistration ) {
      this.serviceRegistration.unregister();
    }
    getPreferences().removePreferenceChangeListener( this );
    Activator.plugin = null;
    super.stop( context );
  }

  /**
   * Returns the shared instance
   * 
   * @return the shared instance
   */
  public static Activator getDefault() {
    return Activator.plugin;
  }

  /**
   * Returns an object that is used to load and store workspace preferences for
   * the DaoJones runtime.
   * 
   * @return the preferences object
   */
  public Preferences getPreferences() {
    return this.preferences;
  }

  /**
   * Logs an info message.
   * 
   * @param message
   *          the message
   */
  public static void logInfo( final String message ) {
    if ( null != Activator.getDefault() ) {
      Activator.getDefault().getLog().log( new Status( IStatus.INFO, Activator.PLUGIN_ID, message ) );
    } else {
      Activator.logger.log( Level.INFO, message );
    }
  }

  /**
   * Logs a warning message.
   * 
   * @param message
   *          the message
   */
  public static void logWarning( final String message, final Throwable t ) {
    if ( null != Activator.getDefault() ) {
      Activator.getDefault().getLog().log( new Status( IStatus.WARNING, Activator.PLUGIN_ID, message, t ) );
    } else {
      Activator.logger.log( Level.WARNING, message, t );
    }
  }

  /**
   * Logs an error message.
   * 
   * @param message
   *          the message
   */
  public static void logError( final String message, final Throwable t ) {
    if ( null != Activator.getDefault() ) {
      Activator.getDefault().getLog().log( new Status( IStatus.ERROR, Activator.PLUGIN_ID, message, t ) );
    } else {
      Activator.logger.log( Level.SEVERE, message, t );
    }
  }

  @Override
  public void preferenceChange( final PreferenceChangeEvent event ) {
    final DaoJonesContext context = DaoJonesPlugin.getDaoJonesContext();
    if ( null != context ) {
      final Preferences pref = getPreferences();
      final ConnectionModel.Id id = pref.getConnectionId( event.getKey() );
      if ( null != id ) {
        final Object newPath = event.getNewValue();
        if ( null != newPath ) {
          // find old connection
          final ConnectionModelManager cmm = context.getConfiguration().getConnectionModelManager();
          final ConnectionModel model = cmm.getModel( id );
          if ( null != model ) {
            final Connection con = model.getConnection();
            final String oldPath = con.getDatabase();
            if ( !newPath.equals( oldPath ) ) {
              // replace connection
              final ISchedulingRule rule = new ConnectionLock( model );
              final Job job = new Job( "Re-creating connection \"" + con.getName() + "\"" ) {

                @Override
                protected IStatus run( final IProgressMonitor monitor ) {
                  // Log info
                  getLog().log(
                          new Status( IStatus.INFO, Activator.PLUGIN_ID, "Re-creating connection with id \"" + id
                                  + "\" and new path \"" + newPath + "\"!" ) );
                  // TODO find a possibility to close all connections and to reconnect again
                  //											try {
                  //												// Delete old connection
                  //												final Application application = context.getApplication(id.getApplicationId());
                  //												application.deleteConnection(con);
                  //												// Create new connection
                  //												try {
                  //													// no wrapper necessary because of the
                  //													// ConnectionModelListener
                  //													application.createConnection(con);
                  //												} catch (final ConnectionBuildException e) {
                  //													return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                  //															"Could not create connection with id \"" + id
                  //																	+ "\" and new path \"" + newPath + "\"!", e);
                  //												}
                  //											} catch (final DataAccessException e) {
                  //												return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                  //														"Could not delete connection with id \"" + id
                  //																+ "\"!", e);
                  //											}
                  return Status.OK_STATUS;
                }

              };
              job.setRule( rule );
              job.schedule();
            }
            return;
          }
        }
      }
    }
  }

}
