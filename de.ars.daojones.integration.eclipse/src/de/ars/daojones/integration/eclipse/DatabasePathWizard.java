package de.ars.daojones.integration.eclipse;

import org.eclipse.ui.IWorkbench;

/**
 * The wizard to create or edit a database path.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 2.0
 */
public final class DatabasePathWizard {

  private DatabasePathWizard() {
    super();
  }

  /**
   * Opens the wizard to create or edit the path.
   *
   * @param workbench
   *          the workbench
   * @param driver
   *          the driver class
   * @param path
   *          the database path or <code>null</code>, if a new path is created
   * @return the new database path or <code>null</code>, if the wizard was
   *         cancelled
   */
  public static String open( final IWorkbench workbench, final String driver, final String path ) {

    // TODO get wizard, use default one
    return null;
    //    final AbstractDatabasePathWizard wizard = null;
    //    wizard.init( workbench, path );
    //    final WizardDialog dialog = new WizardDialog( workbench.getActiveWorkbenchWindow().getShell(), wizard );
    //    dialog.create();
    //    dialog.setBlockOnOpen( true );
    //    return dialog.open() == Window.OK ? wizard.createPath() : null;
  }

}
