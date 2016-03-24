/**
 * 
 */
package de.ars.daojones.eclipse.ui.help.commands.cs;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;

import de.ars.daojones.eclipse.ui.help.Activator;

/**
 * This command runs a wizard creating a kind of resource.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class OpenWizard extends AbstractHandler implements IHandler {

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			final String wizardId = event.getParameter("wizardId");
			final String wizardType = event.getParameter("wizardType");
			Activator.getDefault().getLog().log(
					new Status(IStatus.INFO, Activator.PLUGIN_ID,
							"Opening wizard with ID \'" + wizardId + "\'."));
			final IExtensionRegistry registry = Platform.getExtensionRegistry();
			for (IConfigurationElement config : registry
					.getConfigurationElementsFor(wizardType)) {
				if ("wizard".equals(config.getName())
						&& wizardId.equals(config.getAttribute("id"))) {
					final IWorkbenchWizard wizard = (IWorkbenchWizard) config
							.createExecutableExtension("class");
					final ISelection selection = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getSelectionService()
							.getSelection();
					wizard
							.init(
									PlatformUI.getWorkbench(),
									selection instanceof IStructuredSelection ? (IStructuredSelection) selection
											: null);

					final WizardDialog dialog = new WizardDialog(PlatformUI
							.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), wizard);
					// PixelConverter converter= new
					// PixelConverter(JFaceResources.getDialogFont());
					// dialog.setMinimumPageSize(converter.convertWidthInCharsToPixels(70),
					// converter.convertHeightInCharsToPixels(20));
					dialog.create();
					return dialog.open();
				}
			}
			throw new ExecutionException("A wizard with the ID \'" + wizardId
					+ "\' could not be found!");
		} catch (CoreException e) {
			throw new ExecutionException("Error during opening wizard", e);
		}
	}

}
