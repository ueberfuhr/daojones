package de.ars.daojones.integration.eclipse.viewers.providers;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ars.daojones.runtime.configuration.context.ApplicationModel;
import de.ars.daojones.runtime.configuration.context.ApplicationModelManager;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModelManager;
import de.ars.daojones.runtime.configuration.context.DaoJonesContextConfiguration;

/**
 * A Content Provider this provides connections.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 2.0
 */
public class ConnectionModelByApplicationContentProvider implements ITreeContentProvider {

  private DaoJonesContextConfiguration configuration;

  /**
   * Constructor.
   */
  public ConnectionModelByApplicationContentProvider() {
    super();
  }

  protected DaoJonesContextConfiguration getConfiguration() {
    return this.configuration;
  }

  @Override
  public Object[] getChildren( final Object parentElement ) {
    if ( parentElement instanceof DaoJonesContextConfiguration ) {
      final ApplicationModelManager cmm = ( ( DaoJonesContextConfiguration ) parentElement )
              .getApplicationModelManager();
      return cmm.getModels().toArray();
    } else if ( parentElement instanceof ApplicationModel ) {
      final List<ConnectionModel> result = new LinkedList<ConnectionModel>();
      final DaoJonesContextConfiguration config = getConfiguration();
      if ( null != config ) {
        final ApplicationModel am = ( ApplicationModel ) parentElement;
        final ConnectionModelManager cmm = getConfiguration().getConnectionModelManager();
        for ( final ConnectionModel cm : cmm.getModels() ) {
          if ( cm.getId().getApplicationId().equals( am.getId() ) ) {
            result.add( cm );
          }
        }
      }
      return result.toArray();
    } else if ( parentElement instanceof IAdaptable ) {
      final IAdaptable adaptable = ( IAdaptable ) parentElement;
      return getChildren( adaptable.getAdapter( DaoJonesContextConfiguration.class ) );
    }
    return new Object[0];
  }

  /* *****************************************************************
   *   D E F A U L T   M E T H O D   I M P L E M E N T A T I O N S   *
   ***************************************************************** */

  @Override
  public Object getParent( final Object child ) {
    final DaoJonesContextConfiguration conf = getConfiguration();
    if ( child instanceof ConnectionModel && null != conf ) {
      final String application = ( ( ConnectionModel ) child ).getId().getApplicationId();
      return conf.getApplicationModelManager().getModel( application );
    } else if ( child instanceof ApplicationModel ) {
      return conf;
    } else {
      return null;
    }
  }

  @Override
  public boolean hasChildren( final Object parent ) {
    final Object[] children = getChildren( parent );
    return null != children && children.length > 0;
  }

  @Override
  public Object[] getElements( final Object inputElement ) {
    return getChildren( inputElement );
  }

  @Override
  public void dispose() {
  }

  @Override
  public void inputChanged( final Viewer viewer, final Object oldInput, final Object newInput ) {
    this.configuration = null;
    if ( newInput instanceof DaoJonesContextConfiguration ) {
      this.configuration = ( DaoJonesContextConfiguration ) newInput;
    }
  }

}
