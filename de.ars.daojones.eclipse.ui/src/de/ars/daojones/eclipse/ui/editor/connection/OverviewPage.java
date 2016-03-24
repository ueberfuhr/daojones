package de.ars.daojones.eclipse.ui.editor.connection;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import de.ars.daojones.connections.model.IConnectionConfiguration;
import de.ars.daojones.connections.model.io.InvalidFormatException;
import de.ars.daojones.eclipse.ui.Activator;

/**
 * A page containing the editor for connection configurations
 * as a form editor containing a master-details-block.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class OverviewPage extends FormPage {

	private IConnectionConfiguration conf;
	private XmlFileConnectionConfigurationSource source;
	private MasterBlock master;

	/**
	 * Creates an instance.
	 * @param editor the parent editor
	 * @param id the id of this page
	 */
	public OverviewPage(FormEditor editor, String id) {
		super(editor, id, "Connections");
	}
	
	private XmlFileConnectionConfigurationSource getSource() {
		if(null == source) {
			source = new XmlFileConnectionConfigurationSource(getFile());
		};
		return source;
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		managedForm.setInput(getEditorInput());
		final FormToolkit toolkit = managedForm.getToolkit();
		final Form form = managedForm.getForm().getForm();
		form.setText("Connection Configuration");
		toolkit.decorateFormHeading(form);
		try {
			master = new MasterBlock(this, getConfiguration());
			master.createContent(managedForm);
		} catch (InvalidFormatException e) {
			ErrorDialog.openError(
				getEditor().getSite().getShell(), 
				"Invalid file format!", 
				"The file you want to open does not contain valid content.", 
				new Status(IStatus.ERROR, Activator.PLUGIN_ID, "The file you want to open does not contain valid content.", e)
			);
		} catch (IOException e) {
			ErrorDialog.openError(
				getEditor().getSite().getShell(), 
				"I/O Exception!", 
				"Could not access to the file!", 
				new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Could not access to the file!", e)
			);
		}
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormPage#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return super.isDirty() || master.isDirty();
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormPage#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			final IFile file = getFile();
			if(null != file && null != getConfiguration()) {
				getSource().update(getConfiguration(), null);
			}
			super.doSave(monitor);
			master.setDirty(false);
		} catch (InvalidFormatException e) {
			ErrorDialog.openError(
				getEditor().getSite().getShell(), 
				"Invalid file format!", 
				"The file you want to open does not contain valid content.", 
				new Status(IStatus.ERROR, Activator.PLUGIN_ID, "The file you want to open does not contain valid content.", e)
			);
		} catch (IOException e) {
			ErrorDialog.openError(
				getEditor().getSite().getShell(), 
				"I/O Exception!", 
				"Could not access to the file!", 
				new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Could not access to the file!", e)
			);
		}
	}
	
	/**
	 * @see org.eclipse.ui.forms.editor.FormPage#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		super.doSaveAs();
	}
	
	private IFile getFile() {
		if(null != getEditorInput() && getEditorInput() instanceof IFileEditorInput) {
			final IFileEditorInput fInput = (IFileEditorInput)getEditorInput();
			return fInput.getFile();
		} else return null;
	}
	
	private IConnectionConfiguration getConfiguration() throws InvalidFormatException, IOException {
		if(null == this.conf && null != getEditorInput()) {
			final IFile file = getFile();
			if(null != file) {
				this.conf = getSource().read(null);
			}
		}
		return this.conf;
	}
	
	
}
