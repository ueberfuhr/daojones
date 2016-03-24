package de.ars.daojones.internal.integration.eclipse.preferences.pages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.ars.daojones.integration.eclipse.viewers.providers.ConfigurationModelLabelProvider;
import de.ars.daojones.integration.eclipse.viewers.providers.ConnectionFactoryModelContentProvider;
import de.ars.daojones.integration.equinox.DaoJonesPlugin;
import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModel;
import de.ars.daojones.runtime.context.DaoJonesContext;

/**
 * The preference page that displays the connection factories.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 */
public class ConnectionFactoriesPage extends PreferencePage implements IWorkbenchPreferencePage {

  @Override
  protected Control createContents( final Composite parent ) {
    final SashForm pnl = new SashForm( parent, SWT.VERTICAL );
    // Factories
    final ListViewer lstFactories = new ListViewer( pnl, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
    lstFactories.setLabelProvider( new ConfigurationModelLabelProvider() );
    lstFactories.setContentProvider( new ConnectionFactoryModelContentProvider() );
    // Details
    final Label lblDescription = new Label( pnl, SWT.WRAP );
    lstFactories.addSelectionChangedListener( new ISelectionChangedListener() {

      @Override
      public void selectionChanged( final SelectionChangedEvent event ) {
        lblDescription.setText( "" );
        final ISelection selection = event.getSelection();
        if ( !selection.isEmpty() && selection instanceof IStructuredSelection ) {
          final IStructuredSelection ssel = ( IStructuredSelection ) selection;
          final ConnectionFactoryModel md = ( ConnectionFactoryModel ) ssel.getFirstElement();
          lblDescription.setText( md.getDescription() );
        }
      }

    } );
    // configure sash
    pnl.setWeights( new int[] { 1, 1 } );
    // fill data
    final DaoJonesContext djContext = DaoJonesPlugin.getDaoJonesContext();
    if ( null != djContext ) {
      lstFactories.setInput( djContext.getConfiguration() );
    }
    return pnl;
  }

  /**
   * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
   */
  @Override
  public void init( final IWorkbench workbench ) {

  }

}
