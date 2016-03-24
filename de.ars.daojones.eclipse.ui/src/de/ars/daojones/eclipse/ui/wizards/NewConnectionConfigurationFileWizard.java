package de.ars.daojones.eclipse.ui.wizards;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;

import de.ars.daojones.connections.model.ConnectionConfiguration;
import de.ars.daojones.eclipse.connections.Constants;
import de.ars.daojones.eclipse.ui.Activator;
import de.ars.daojones.eclipse.ui.editor.connection.XmlFileConnectionConfigurationSource;

/**
 * A wizard to create a new XML connection configuration file.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class NewConnectionConfigurationFileWizard extends BasicNewFileResourceWizard {

	private WizardNewFileCreationPage page;
	
	/**
	 * Creates an instance.
	 */
	public NewConnectionConfigurationFileWizard() {
		super();
	}
	
	/**
	 * Returns the {@link WizardNewFileCreationPage}.
	 * @return the {@link WizardNewFileCreationPage}
	 */
	protected WizardNewFileCreationPage getPage() {
		if(null == page) {
			final IWizardPage[] pages = getPages();
			if(pages.length>0) {
				for(IWizardPage p : pages) {
					if(p instanceof WizardNewFileCreationPage) {
						page = (WizardNewFileCreationPage)p;
						break;
					}
				}
			}
		};
		return page;
	}
	
	/**
	 * @see org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		getPage().setFileExtension("xml");
		getPage().setTitle("New DaoJones Connection Configuration File");
	}

	/**
	 * @see org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final IFile newFile = getPage().createNewFile();
		if(null != newFile) {
			try {
				newFile.setPersistentProperty(Constants.FILE_IDENTIFIER_KEY, Constants.FILE_IDENTIFIER_VALUE);
			} catch (CoreException e) {
				Activator.log(IStatus.ERROR, "Unable to set file identifier to DaoJones Connection Configuration File!", e);
			}
			final ConnectionConfiguration conf = new ConnectionConfiguration();
			final XmlFileConnectionConfigurationSource src = new XmlFileConnectionConfigurationSource(newFile);
			try {
				src.update(conf, null);
			} catch (IOException e) {
				final String message = "Unable to initialize content of DaoJones Connection Configuration File!\nThe file might be broken.";
				Activator.log(IStatus.ERROR, "", e);
				MessageDialog.openError(this.getShell(), "Error on creating file!", message);
			}
		}
		return super.performFinish();
	}
	
	

}
