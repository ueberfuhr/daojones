package de.ars.daojones.integration.eclipse;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IPluginContribution;
import org.eclipse.ui.activities.WorkbenchActivityHelper;

import de.ars.daojones.internal.integration.eclipse.Activator;
import de.ars.daojones.runtime.configuration.connections.Connection;

/**
 * This class provides access to the DaoJones UI configuration.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 2.0
 */
public abstract class ConfigurationProvider {

  /**
   * The name of the extension to configure connections.
   */
  private static final String EXTENSION_CONFIGURATION = Activator.PLUGIN_ID + ".configuration";
  private static final String ELEMENT_CONNECTION = "connection";
  private static final String ELEMENT_EDITABLE = "editable";

  /**
   * Returns a flag indicating whether the connection can be edited by the user
   * or not. This is used by the UI to provide editors to the user.<br/>
   * To configure the editable flag for a connection, use the
   * <code>{@value #ELEMENT_CONNECTION}/{@value #ELEMENT_EDITABLE}</code>
   * element of the <code>{@value #EXTENSION_CONFIGURATION}</code> extension
   * point.
   * 
   * @param connection
   *          the connection
   * @return <tt>true</tt>, if the connection can be edited by the user
   */
  public static boolean isEditable( final Connection connection ) {
    if ( null != connection && null != connection.getId() ) {
      final IExtensionRegistry registry = Platform.getExtensionRegistry();
      if ( null != registry ) {
        final IConfigurationElement[] connectionElements = registry
                .getConfigurationElementsFor( ConfigurationProvider.EXTENSION_CONFIGURATION );
        for ( final IConfigurationElement configurationElement : connectionElements ) {
          if ( ConfigurationProvider.ELEMENT_CONNECTION.equals( configurationElement.getName() )
                  && connection.getId().equals( configurationElement.getAttribute( "id" ) ) ) {
            final IConfigurationElement[] editableElements = configurationElement
                    .getChildren( ConfigurationProvider.ELEMENT_EDITABLE );
            for ( final IConfigurationElement editableElement : editableElements ) {
              final String id = editableElement.getAttribute( "activity" );
              if ( null == id || id.length() == 0 || !WorkbenchActivityHelper.filterItem( new IPluginContribution() {

                @Override
                public String getLocalId() {
                  return id;
                }

                @Override
                public String getPluginId() {
                  return editableElement.getContributor().getName();
                }

              } ) ) {
                return true;
              }
            }
          }
        }
      }
    }
    return false;
  }
}
