package de.ars.daojones.internal.integration.eclipse;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Composite;

import de.ars.daojones.integration.eclipse.ConfigurationProvider;
import de.ars.daojones.integration.eclipse.DatabasePathEditor;
import de.ars.daojones.integration.eclipse.DefaultDatabasePathEditor;
import de.ars.daojones.runtime.configuration.connections.Connection;

/**
 * A class providing methods to access the driver-specific database path
 * editors.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 2.0
 */
public abstract class DatabasePathEditorProvider {

  private static final Logger logger = Logger.getLogger( DatabasePathEditorProvider.class.getName() );
  private static final String EXTENSION_POINT = Activator.PLUGIN_ID + ".databasePathEditors";

  private DatabasePathEditorProvider() {
    super();
  }

  /**
   * Finds the driver-specific database path editor and creates an instance. If
   * no editor is found, <code>null</code> is returned. You can then use the
   * {@link DefaultDatabasePathEditor} class instead.
   * 
   * @param driver
   *          the driver's ID
   * @return the {@link DatabasePathEditor} instance or <code>null</code>, if
   *         not found
   */
  public static DatabasePathEditor createEditor( final String driver ) {
    assert null != driver;
    final IExtensionRegistry reg = Platform.getExtensionRegistry();
    // read elements
    final IConfigurationElement[] elements = reg
            .getConfigurationElementsFor( DatabasePathEditorProvider.EXTENSION_POINT );
    for ( final IConfigurationElement el : elements ) {
      try {
        if ( "editor".equals( el.getName() ) && driver.equals( el.getAttribute( "connection-factory" ) ) ) {
          return ( DatabasePathEditor ) el.createExecutableExtension( "class" );
        }
      } catch ( final CoreException e ) {
        DatabasePathEditorProvider.logger.log( Level.SEVERE,
                "Unable to create an instance of class " + el.getAttribute( "class" ) + "!", e );
      }
    }
    return null;
  }

  /**
   * Creates a {@link DatabasePathEditor} and initializes it. If there is no
   * editor for the driver, the default database path editor is initialized.
   * 
   * @param parent
   *          the parent {@link Composite}
   * @param con
   *          the connection
   * @param ctx
   *          the data binding context
   * @return the {@link DatabasePathEditor} instance
   */
  public static DatabasePathEditor createAndInitializeEditor( final Composite parent, final Connection con,
          final DataBindingContext ctx ) {
    DatabasePathEditor editor = DatabasePathEditorProvider.createEditor( con.getType() );
    if ( null == editor ) {
      editor = new DefaultDatabasePathEditor();
    }
    editor.createControls( parent );
    final IObservableValue pathValue = new WritableValue( con.getDatabase(), String.class );
    editor.setModel( pathValue, ctx );
    final boolean editable = ConfigurationProvider.isEditable( con );
    editor.setEditable( editable );
    if ( editable ) {
      pathValue.addValueChangeListener( new IValueChangeListener() {

        @Override
        public void handleValueChange( final ValueChangeEvent event ) {
          con.setDatabase( ( String ) event.getObservableValue().getValue() );
        }

      } );
      editor.setFocus();
    }
    return editor;
  }

}
