package de.ars.daojones.integration.eclipse;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

/**
 * A wizard implementation to create or edit a database path dependent from the
 * driver. Drivers have to implement this wizard to provide a special UI.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 2.0
 */
public abstract class AbstractDatabasePathWizard extends Wizard {

  /**
   * This method injects the current path. Note that the could be the path of
   * another database driver in case of switching the driver in the UI.
   * 
   * @param workbench
   *          the workbench
   * @param path
   *          the current path or <tt>null</tt>, if no path is available.
   */
  protected abstract void init( IWorkbench workbench, String path );

  /**
   * Creates the path that is set by this wizard. This is invoked after
   * executing the wizard.
   * 
   * @return the path
   */
  protected abstract String createPath();

  @Override
  public final boolean performFinish() {
    return true;
  }

}
