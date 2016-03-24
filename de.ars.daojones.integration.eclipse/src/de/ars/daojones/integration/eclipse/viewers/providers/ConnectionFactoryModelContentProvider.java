package de.ars.daojones.integration.eclipse.viewers.providers;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;

import de.ars.daojones.runtime.configuration.context.DaoJonesContextConfiguration;

/**
 * A {@link IStructuredContentProvider} that provides the connection factory
 * models. The input must be the {@link DaoJonesContextConfiguration} instance.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 */
public class ConnectionFactoryModelContentProvider extends ArrayContentProvider {

  @Override
  public Object[] getElements( final Object inputElement ) {
    if ( inputElement instanceof DaoJonesContextConfiguration ) {
      final DaoJonesContextConfiguration config = ( DaoJonesContextConfiguration ) inputElement;
      return super.getElements( config.getConnectionFactoryModelManager().getModels() );
    } else {
      return super.getElements( inputElement );
    }
  }

}
