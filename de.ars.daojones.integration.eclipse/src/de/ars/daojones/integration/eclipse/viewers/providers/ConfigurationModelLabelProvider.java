package de.ars.daojones.integration.eclipse.viewers.providers;

import org.eclipse.jface.viewers.LabelProvider;

import de.ars.daojones.internal.integration.eclipse.viewers.providers.AdapableUtil;
import de.ars.daojones.runtime.configuration.context.ConfigurationModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;

/**
 * A label provider for connection models.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 */
public class ConfigurationModelLabelProvider extends LabelProvider {

  @Override
  public String getText( final Object element ) {
    // Kleinerer Bug
    final ConnectionModel connectionModel = AdapableUtil.adapt( element, ConnectionModel.class );
    if ( null != connectionModel ) {
      return connectionModel.getConnection().getName();
    }
    // Standardverhalten
    final ConfigurationModel<?> model = AdapableUtil.adapt( element, ConfigurationModel.class );
    return null != model ? model.getName() : super.getText( element );
  }
}
