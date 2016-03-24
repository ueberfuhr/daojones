package de.ars.daojones.eclipse.ui.editor.connection;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import static de.ars.daojones.eclipse.ui.LoggerConstants.*;

/**
 * An editor for connection files.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ConnectionsEditor extends FormEditor implements IResourceChangeListener {

	private OverviewPage connectionsEditorPage;
	private IResource resource;
	private IWorkspace workspace;
	
	private OverviewPage getConnectionsEditorPage() {
		if(null == connectionsEditorPage) {
			connectionsEditorPage = new OverviewPage(this, "connectionsEditor");
		}
		return connectionsEditorPage;
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	@Override
	protected void addPages() {
		try {
			super.addPage(getConnectionsEditorPage());
			//updateActionBarContributor(0);
		} catch (PartInitException e) {
			getLogger().log(ERROR, "Unable to initialize part control!", e);
		}
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		this.resource = (IResource)input.getAdapter(IResource.class);
		this.workspace = null != this.resource ? this.resource.getWorkspace() : null;
		if(null != this.workspace) {
			this.workspace.addResourceChangeListener(this);
		}
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		getConnectionsEditorPage().doSave(monitor);
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		getConnectionsEditorPage().doSaveAs();
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return getConnectionsEditorPage().isSaveAsAllowed();
	}

	/**
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	// TODO Java6-Migration
	// @Override
	public void resourceChanged(IResourceChangeEvent event) {
		if(null != this.resource && !this.resource.exists()) {
			this.close(false);
		}
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormEditor#dispose()
	 */
	@Override
	public void dispose() {
		if(null != this.workspace) {
			this.workspace.removeResourceChangeListener(this);
		}
		super.dispose();
	}

	
	
}
