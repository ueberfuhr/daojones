package de.ars.daojones.integration.equinox;

import de.ars.daojones.internal.integration.equinox.Activator;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.osgi.DaoJonesBundle;

/**
 * A simple object representing this plug-in.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 2.0.0
 */
public abstract class DaoJonesPlugin {

  private DaoJonesPlugin() {
    super();
  }

  /**
   * The plug-in ID.
   */
  public static final String PLUGIN_ID = Activator.PLUGIN_ID;

  /**
   * Returns an object that is used to load and store workspace preferences for
   * the DaoJones runtime.
   * 
   * @return The preferences object or <code>null</code>, if the preferences are
   *         not available. This can happen when the plugin is stopped.
   */
  public static Preferences getPreferences() {
    final Activator plugin = Activator.getDefault();
    return null != plugin ? plugin.getPreferences() : null;
  }

  /**
   * Returns the DaoJones context that is managed by this plug-in.
   * 
   * @return the DaoJones context
   */
  public static DaoJonesContext getDaoJonesContext() {
    return DaoJonesBundle.getDaoJonesContext();
  }

}
