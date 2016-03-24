package de.ars.daojones.eclipse.ui.actions;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

import de.ars.daojones.connections.model.IImportDeclaration;
import de.ars.daojones.connections.model.ImportDeclaration;
import de.ars.daojones.eclipse.ui.Activator;
import de.ars.daojones.eclipse.ui.contentprovider.ConnectionsFileProvider;
import de.ars.equinox.utilities.PathUtil;

/**
 * An operation that creates an import declaration
 * to the currently selected object.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class CreateImportDeclarationOperation extends AbstractOperation {

	/**
	 * The default title of this operation.
	 */
	public static final String TITLE = "Create Import Declaration";
	private final ConnectionFileEditorContext context;

	/**
	 * Creates an instance with the default title.
	 * @param context the context of the operation.
	 */
	public CreateImportDeclarationOperation(final ConnectionFileEditorContext context) {
		this(context, TITLE);
	}
	/**
	 * Creates an instance with the given title.
	 * @param context the context of the operation.
	 * @param label the title
	 */
	public CreateImportDeclarationOperation(final ConnectionFileEditorContext context, String label) {
		super(label);
		this.context = context;
	}
	/**
	 * Returns the operation context.
	 * @return the context
	 */
	protected ConnectionFileEditorContext getContext() {
		return this.context;
	}

	private final Collection<IImportDeclaration> declarations = new LinkedList<IImportDeclaration>();
	private IStatus insertDeclarations() {
		final Collection<IImportDeclaration> oldValue = new LinkedList<IImportDeclaration>(getContext().getConfiguration().getImportDeclarations());
		if(getContext().getConfiguration().getImportDeclarations().addAll(declarations)) {
			final Collection<IImportDeclaration> newValue = new LinkedList<IImportDeclaration>(getContext().getConfiguration().getImportDeclarations());
			getContext().firePropertyChange(ConnectionFileEditorContext.PROPERTY_IMPORTS, oldValue, newValue);
			return Status.OK_STATUS;
		} else {
			return Status.CANCEL_STATUS;
		}
	}
	private IStatus removeDeclarations() {
		final Collection<IImportDeclaration> oldValue = new LinkedList<IImportDeclaration>(getContext().getConfiguration().getImportDeclarations());
		if(getContext().getConfiguration().getImportDeclarations().removeAll(declarations)) {
			final Collection<IImportDeclaration> newValue = new LinkedList<IImportDeclaration>(getContext().getConfiguration().getImportDeclarations());
			getContext().firePropertyChange(ConnectionFileEditorContext.PROPERTY_IMPORTS, oldValue, newValue);
			return Status.OK_STATUS;
		} else {
			return Status.CANCEL_STATUS;
		}

	}
	
	/**
	 * Creates a dialog where the user can select a connections file to import.
	 * @param shell the parent shell
	 * @return the dialog instance
	 */
	protected SelectionDialog createImportDeclarationChoiceDialog(Shell shell) {
		final ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(shell, new WorkbenchLabelProvider(), new ConnectionsFileProvider(getContext()));
		dialog.setHelpAvailable(false);
		dialog.setTitle("Connections file selection"); 
		dialog.setMessage("Please select the connections file to import."); 
		dialog.setInput(getContext().getConfigurationFile().getProject());
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		dialog.setValidator(new ISelectionStatusValidator() {
			// TODO Java6-Migration
			// @Override
			public IStatus validate(Object[] selection) {
				for(Object o : selection) {
					if(!(o instanceof IFile)) return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Please only select files!");
				}
				return new Status(IStatus.OK, Activator.PLUGIN_ID, "");
			}
		});
		return dialog;
	}
	
	/**
	 * @see org.eclipse.core.commands.operations.AbstractOperation#execute(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		final Shell shell = null != info ? (Shell)info.getAdapter(Shell.class) : Display.getCurrent().getActiveShell();
		final SelectionDialog dialog = createImportDeclarationChoiceDialog(shell);
		if (dialog.open() == Window.OK) {
			final Object[] oArr = dialog.getResult();
			for(Object o : oArr) {
				if(o instanceof IFile) {
					final IFile file = (IFile)o;
					final ImportDeclaration importDeclaration = new ImportDeclaration();
					importDeclaration.setFile(PathUtil.makeRelative(getContext().getConfigurationFile().getFullPath(), file.getFullPath()).toPortableString());
					declarations.add(importDeclaration);
				}
			}
			return insertDeclarations();
		};
		return Status.CANCEL_STATUS;
	}
	/**
	 * @see org.eclipse.core.commands.operations.AbstractOperation#redo(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return insertDeclarations();
	}
	/**
	 * @see org.eclipse.core.commands.operations.AbstractOperation#undo(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return removeDeclarations();
	}
	
}
