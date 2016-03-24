package de.ars.daojones.internal.integration.eclipse.preferences.pages;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.databinding.preference.PreferencePageSupport;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.ars.daojones.integration.eclipse.ConfigurationProvider;
import de.ars.daojones.integration.eclipse.viewers.providers.ConfigurationModelLabelProvider;
import de.ars.daojones.integration.eclipse.viewers.providers.ConnectionModelByApplicationContentProvider;
import de.ars.daojones.integration.equinox.DaoJonesPlugin;
import de.ars.daojones.integration.equinox.Preferences;
import de.ars.daojones.internal.integration.eclipse.Activator;
import de.ars.daojones.internal.integration.eclipse.DatabasePathEditorProvider;
import de.ars.daojones.internal.integration.eclipse.MessageDialogUtil;
import de.ars.daojones.internal.integration.eclipse.viewers.providers.AdapableUtil;
import de.ars.daojones.internal.integration.eclipse.viewers.providers.ExtendedTreeLabelProvider;
import de.ars.daojones.runtime.configuration.connections.Connection;
import de.ars.daojones.runtime.configuration.context.ApplicationModel;
import de.ars.daojones.runtime.configuration.context.ConfigurationModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.context.FactoryModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.spi.database.ConnectionFactory;

/**
 * The preference page that displays the connection factories.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 */
public class ConnectionsPage extends PreferencePage implements IWorkbenchPreferencePage {

  private final Map<ConnectionModel, String> editedPath = new HashMap<ConnectionModel, String>();
  private Label lblDescription;
  private Composite pnlDatabasePathEditor;
  private TreeViewer treeApplicationContexts;
  private Composite pnlDetails;
  private PreferencePageSupport validationSupport;

  @Override
  protected Control createContents( final Composite parent ) {
    final SashForm pnl = new SashForm( parent, SWT.VERTICAL );

    treeApplicationContexts = new TreeViewer( pnl, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
    treeApplicationContexts.setContentProvider( new ConnectionModelByApplicationContentProvider() );
    treeApplicationContexts.setLabelProvider( new ExtendedTreeLabelProvider( new ConfigurationModelLabelProvider() ) {

      @Override
      protected String getText( final Object element, final int columnIndex ) {
        if ( columnIndex == 1 ) {
          final ConnectionModel connectionModel = AdapableUtil.adapt( element, ConnectionModel.class );
          if ( null != connectionModel ) {
            final String factoryId = connectionModel.getConnection().getType();
            final FactoryModel<ConnectionFactory> md = DaoJonesPlugin.getDaoJonesContext().getConfiguration()
                    .getConnectionFactoryModelManager().getModel( factoryId );
            return null != md ? md.getName() : factoryId;
          } else {
            return "";
          }
        } else if ( columnIndex == 2 ) {
          return "";
        }
        return null;
      }

      private final Font defaultFont = treeApplicationContexts.getTree().getFont();

      @Override
      public Font getFont( final Object element, final int columnIndex ) {
        if ( element instanceof ApplicationModel ) {
          return ConnectionsPage.getFont( treeApplicationContexts.getTree(), SWT.BOLD );
        } else {
          return defaultFont;
        }
      }

      @Override
      protected Image getImage( final Object element, final int columnIndex ) {
        if ( columnIndex == 2 ) {
          final ConnectionModel connectionModel = AdapableUtil.adapt( element, ConnectionModel.class );
          if ( null != connectionModel && !ConfigurationProvider.isEditable( connectionModel.getConnection() ) ) {
            return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, "icons/lock.png" ).createImage();
          }
        }
        return null;
      }

    } );
    treeApplicationContexts.getTree().setHeaderVisible( true );

    final TreeColumn column1 = new TreeColumn( treeApplicationContexts.getTree(), SWT.LEFT );
    column1.setText( Messages.CONNECTIONSPAGE_CONNECTIONSTREE_TITLE_COLUMN1 );
    column1.setWidth( 300 );
    column1.setResizable( true );
    column1.setMoveable( false );
    final TreeColumn column2 = new TreeColumn( treeApplicationContexts.getTree(), SWT.LEFT );
    column2.setText( Messages.CONNECTIONSPAGE_CONNECTIONSTREE_TITLE_COLUMN2 );
    column2.setWidth( 200 );
    final TreeColumn lockedColumn = new TreeColumn( treeApplicationContexts.getTree(), SWT.CENTER );
    lockedColumn.setText( "" );
    lockedColumn.setWidth( 30 );
    lockedColumn.setResizable( false );
    lockedColumn.setMoveable( false );

    pnlDetails = new Composite( pnl, SWT.NONE );
    pnlDetails.setLayout( new GridLayout( 1, false ) );
    treeApplicationContexts.addSelectionChangedListener( new ISelectionChangedListener() {

      @Override
      public void selectionChanged( final SelectionChangedEvent event ) {
        fillDetails( event.getSelection() );
      }
    } );
    // configure sash
    pnl.setWeights( new int[] { 1, 1 } );
    // fill data
    fillTree();
    return pnl;
  }

  private static Font getFont( final Control control, final int style ) {
    final Display display = Display.getCurrent();
    final FontDescriptor fontDescriptor = FontDescriptor.createFrom( control.getFont() );
    return fontDescriptor.setStyle( style ).createFont( display );
  }

  private void fillTree() {
    final ISelection sel = treeApplicationContexts.getSelection();
    final DaoJonesContext djContext = DaoJonesPlugin.getDaoJonesContext();
    if ( null != djContext ) {
      treeApplicationContexts.setInput( djContext.getConfiguration() );
    }
    treeApplicationContexts.expandAll();
    if ( null != sel && !sel.isEmpty() ) {
      treeApplicationContexts.setSelection( sel );
    }
  }

  private void fillDetails( final ISelection selection ) {
    disposeCustomWidgets();
    if ( !selection.isEmpty() && selection instanceof IStructuredSelection ) {
      final IStructuredSelection ssel = ( IStructuredSelection ) selection;
      final Object o = ssel.getFirstElement();
      // Common Configuration Model
      final ConfigurationModel<?> configModel = AdapableUtil.adapt( o, ConfigurationModel.class );
      if ( null != configModel ) {
        lblDescription = new Label( pnlDetails, SWT.WRAP );
        lblDescription.setLayoutData( GridDataFactory.fillDefaults().grab( true, false ).create() );
        final String description = configModel.getDescription();
        if ( null != description ) {
          lblDescription.setText( description );
        }
      }
      // ConnectionModel - specific
      final ConnectionModel con = AdapableUtil.adapt( o, ConnectionModel.class );
      if ( null != con ) {
        // editor
        pnlDatabasePathEditor = new Composite( /*new ScrolledComposite(*/
        pnlDetails/*, SWT.NONE )*/, SWT.NONE );
        pnlDatabasePathEditor.setLayoutData( GridDataFactory.fillDefaults().grab( true, true ).create() );
        pnlDatabasePathEditor.setLayout( new GridLayout( 1, false ) );
        // editor
        final Composite pnlEditor = new Composite( pnlDatabasePathEditor, SWT.NONE );
        pnlEditor.setLayoutData( GridDataFactory.fillDefaults().grab( true, true ).create() );
        // create connection wrapper
        final Connection original = con.getConnection();
        final Connection connectionDuplicate = new Connection() {

          private static final long serialVersionUID = 1L;

          @Override
          public String getDatabase() {
            return editedPath.containsKey( con ) ? editedPath.get( con ) : original.getDatabase();
          }

          @Override
          public void setDatabase( final String value ) {
            editedPath.put( con, value );
          }

        };
        connectionDuplicate.setId( original.getId() );
        connectionDuplicate.setName( original.getName() );
        connectionDuplicate.setDefault( original.isDefault() );
        connectionDuplicate.setDescription( original.getDescription() );
        connectionDuplicate.setType( original.getType() );
        final DataBindingContext ctx = new DataBindingContext();
        DatabasePathEditorProvider.createAndInitializeEditor( pnlEditor, connectionDuplicate, ctx );
        validationSupport = PreferencePageSupport.create( this, ctx );
        // utilities
        final Link link = new Link( pnlDatabasePathEditor, SWT.NONE );
        link.setLayoutData( GridDataFactory.fillDefaults().grab( false, false ).create() );
        link.setText( "<A>Test connection</A>" );
        link.addSelectionListener( new SelectionAdapter() {
          @Override
          public void widgetSelected( final SelectionEvent event ) {
            try {
              ConnectionTester.testConnection( DaoJonesPlugin.getDaoJonesContext(), connectionDuplicate );
              MessageDialog.openInformation( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                      "Connection Test successful!", "1 connection tested." );
            } catch ( final ConfigurationException e ) {
              MessageDialogUtil.openError( Activator.PLUGIN_ID, "Error during building connections!", e );
            } catch ( final DataAccessException e ) {
              MessageDialogUtil.openError( Activator.PLUGIN_ID, "Error during reading data from connections!",
                      e.getCause() );
            }
          }
        } );
      }
      pnlDetails.layout();
    }
  }

  @Override
  public void init( final IWorkbench workbench ) {

  }

  @Override
  public boolean performOk() {
    for ( final Map.Entry<ConnectionModel, String> entry : editedPath.entrySet() ) {
      final Preferences prefs = DaoJonesPlugin.getPreferences();
      final ConnectionModel con = entry.getKey();
      final String newPath = entry.getValue();
      con.getConnection().setDatabase( newPath );
      try {
        prefs.save( con );
      } catch ( final CoreException e ) {
        Activator.getDefault().getLog().log( e.getStatus() );
      }
    }
    editedPath.clear();
    return super.performOk();
  }

  @Override
  protected void performDefaults() {
    editedPath.clear();
    // refresh current editor
    fillDetails( treeApplicationContexts.getSelection() );
    super.performDefaults();
  }

  @Override
  protected void performApply() {
    super.performApply();
    // Refresh list
    fillTree();
  }

  @Override
  public boolean performCancel() {
    editedPath.clear();
    return super.performCancel();
  }

  @Override
  public void dispose() {
    disposeCustomWidgets();
    super.dispose();
  }

  private void disposeCustomWidgets() {
    if ( null != validationSupport ) {
      validationSupport.dispose();
      validationSupport = null;
    }
    if ( null != pnlDatabasePathEditor ) {
      pnlDatabasePathEditor.dispose();
      pnlDatabasePathEditor = null;
    }
    if ( null != lblDescription ) {
      lblDescription.dispose();
      lblDescription = null;
    }
  }

}
