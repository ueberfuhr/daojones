package de.ars.daojones.integration.equinox;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.osgi.service.prefs.BackingStoreException;

import de.ars.daojones.runtime.configuration.connections.Connection;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;

/**
 * This object manages the workspace preferences for DaoJones.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 2.0.0
 */
public class Preferences {

  private static final String PREFIX_CONNECTION = "connection.";
  private static final String SEPARATOR = "@@";
  private static final String SUFFIX_PATH = ".path";

  private static final MessageFormat PREF_CONNECTION_PATH = new MessageFormat( Preferences.PREFIX_CONNECTION + "{0}"
          + Preferences.SEPARATOR + "{1}" + Preferences.SUFFIX_PATH );

  private final String pluginId;
  private final IEclipsePreferences configuration;

  /**
   * Creates an instance.
   * 
   * @param pluginId
   *          the plugin id
   */
  public Preferences( final String pluginId ) {
    super();
    this.pluginId = pluginId;
    this.configuration = ConfigurationScope.INSTANCE.getNode( pluginId );
  }

  /**
   * Decides whether the connection model can be identified and so be stored
   * into and loaded from the preferences store.
   * 
   * @param connectionModel
   *          the connection model
   * @return <tt>true</tt>, if the connection model can be identified, otherwise
   *         <tt>false</tt>
   */
  protected boolean isIdentifiable( final ConnectionModel connectionModel ) {
    return null != connectionModel.getId() && null != connectionModel.getId().getConnectionId();
  }

  /**
   * Saves the connection model properties. Currently, only the path is saved as
   * customizable property.
   * 
   * @param connectionModels
   *          the connection models
   * @throws CoreException
   *           if one of the connection is not customizable (in this case, no
   *           connection is saved
   */
  public void save( final ConnectionModel... connectionModels ) throws CoreException {
    // test ids
    for ( final ConnectionModel connectionModel : connectionModels ) {
      if ( !isIdentifiable( connectionModel ) ) {
        throw new CoreException( new Status( IStatus.ERROR, this.pluginId,
                "Cannot save connection because it has no id!" ) );
      }
    }
    // save paths
    for ( final ConnectionModel connectionModel : connectionModels ) {
      this.configuration.put( getConnectionPathPreferenceName( connectionModel ), connectionModel.getConnection()
              .getDatabase() );
    }
    // store
    try {
      this.configuration.flush();
    } catch ( final BackingStoreException e ) {
      throw new CoreException( new Status( IStatus.WARNING, this.pluginId, "Cannot save preferences!" ) );
    }
  }

  /**
   * Loads the connection model properties. Currently, only the path is saved as
   * customizable property.
   * 
   * @param connectionModel
   *          the connection
   * @return the customized connection model or the original parameter object,
   *         if no customizations are available
   * @throws CoreException
   *           if the connection is not customizable
   */
  public ConnectionModel load( final ConnectionModel connectionModel ) throws CoreException {
    if ( !isIdentifiable( connectionModel ) ) {
      throw new CoreException(
              new Status( IStatus.ERROR, this.pluginId, "Cannot load connection because it has no id!" ) );
    }
    final String path = this.configuration.get(
            Preferences.PREF_CONNECTION_PATH.format( new Object[] { connectionModel.getId() } ), null );
    if ( null != path && path.length() > 0 && !path.equals( connectionModel.getConnection().getDatabase() ) ) {
      connectionModel.getConnection().setDatabase( path );
    }
    return connectionModel;
  }

  /**
   * Restores the defaults of the given connections.
   * 
   * @param connections
   *          the connections
   * @throws CoreException
   */
  public void restoreDefaults( final Connection... connections ) throws CoreException {
    for ( final Connection connection : connections ) {
      if ( null != connection.getId() ) {
        this.configuration.remove( Preferences.PREF_CONNECTION_PATH.format( new Object[] { connection.getId() } ) );
      }
    }
    try {
      this.configuration.flush();
    } catch ( final BackingStoreException e ) {
      throw new CoreException( new Status( IStatus.WARNING, this.pluginId, "Cannot save preferences!" ) );
    }
  }

  /**
   * Returns the id of the connection by the preference name.
   * 
   * @param preference
   *          the preference name
   * @return the id of the connection or <code>null</code>, if this preference
   *         is not a connection preference
   */
  public ConnectionModel.Id getConnectionId( final String preference ) {
    if ( preference.startsWith( Preferences.PREFIX_CONNECTION ) && preference.endsWith( Preferences.SUFFIX_PATH ) ) {
      final String id = preference.substring( Preferences.PREFIX_CONNECTION.length(), preference.length()
              - Preferences.SUFFIX_PATH.length() );
      final int separatorIdx = id.lastIndexOf( Preferences.SEPARATOR );
      final ConnectionModel.Id result = new ConnectionModel.Id( separatorIdx >= 0 ? id.substring( 0, separatorIdx )
              : null, separatorIdx >= 0 ? id.substring( separatorIdx + Preferences.SEPARATOR.length() ) : id );
      return result;
    } else {
      return null;
    }
  }

  /**
   * Returns the name of the preference to store the connection's path.
   * 
   * @param connectionModel
   *          the connection id
   * @return the name of the preference to store the connection's path
   */
  protected String getConnectionPathPreferenceName( final ConnectionModel connectionModel ) {
    final ConnectionModel.Id id = null == connectionModel ? null : connectionModel.getId();
    return null != id ? Preferences.PREF_CONNECTION_PATH.format( new Object[] { id.getApplicationId(),
        id.getConnectionId() } ) : null;
  }

  /**
   * Register the given listener for notification of preference changes to this
   * node. Calling this method multiple times with the same listener has no
   * effect. The given listener argument must not be <code>null</code>.
   * 
   * @param listener
   *          the preference change listener to register
   * @throws IllegalStateException
   *           if this node or an ancestor has been removed
   * @see #removePreferenceChangeListener(IEclipsePreferences.IPreferenceChangeListener)
   * @see IEclipsePreferences.IPreferenceChangeListener
   */
  public void addPreferenceChangeListener( final IPreferenceChangeListener listener ) {
    this.configuration.addPreferenceChangeListener( listener );
  }

  /**
   * De-register the given listener from receiving notification of preference
   * changes to this node. Calling this method multiple times with the same
   * listener has no effect. The given listener argument must not be
   * <code>null</code>.
   * 
   * @param listener
   *          the preference change listener to remove
   * @throws IllegalStateException
   *           if this node or an ancestor has been removed
   * @see #addPreferenceChangeListener(IEclipsePreferences.IPreferenceChangeListener)
   * @see IEclipsePreferences.IPreferenceChangeListener
   */
  public void removePreferenceChangeListener( final IPreferenceChangeListener listener ) {
    this.configuration.removePreferenceChangeListener( listener );
  }

}
