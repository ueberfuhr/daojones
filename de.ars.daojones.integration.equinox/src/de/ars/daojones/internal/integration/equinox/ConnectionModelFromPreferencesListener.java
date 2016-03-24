package de.ars.daojones.internal.integration.equinox;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.ars.daojones.integration.equinox.DaoJonesPlugin;
import de.ars.daojones.integration.equinox.Preferences;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.connections.events.ConnectionEvent;
import de.ars.daojones.runtime.connections.events.ConnectionEventListener;
import de.ars.daojones.runtime.connections.events.ConnectionEventType;

/**
 * A connection event listener that modified connections before their creation
 * by reading out the preferences and assigning customizations.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 2.0.0
 */
public class ConnectionModelFromPreferencesListener implements ConnectionEventListener {

  @Override
  public void handle( final ConnectionEvent event ) {
    if ( ConnectionEventType.CONNECTION_CREATED == event.getType() ) {
      final Preferences prefs = DaoJonesPlugin.getPreferences();
      final ConnectionModel[] connectionModels = event.getConnections();
      for ( int i = 0; i < connectionModels.length; i++ ) {
        final ConnectionModel model = connectionModels[i];
        if ( null != model && null != model.getId() ) {
          try {
            final ConnectionModel newModel = prefs.load( model );
            if ( model != newModel && null != newModel ) {
              connectionModels[i] = newModel;
            }
          } catch ( final CoreException e ) {
            Activator
                    .getDefault()
                    .getLog()
                    .log( new Status( IStatus.ERROR, Activator.PLUGIN_ID,
                            "Unable to read preferences for connection \"" + model.getId() + "\"!", e ) );
          }
        }
      }
    }
  }

}
