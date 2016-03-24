package de.ars.daojones.eclipse.ui.editor.connection;

import static de.ars.daojones.eclipse.ui.LoggerConstants.ERROR;
import static de.ars.daojones.eclipse.ui.LoggerConstants.getLogger;
import static de.ars.eclipse.ui.util.FormsUtil.createSectionData;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;
import org.eclipse.ui.views.navigator.ResourceComparator;

import de.ars.daojones.connections.model.Connection;
import de.ars.daojones.connections.model.IConnection;
import de.ars.daojones.connections.model.IConnectionConfiguration;
import de.ars.daojones.connections.model.ICredential;
import de.ars.daojones.connections.model.IImportDeclaration;
import de.ars.daojones.connections.model.ImportDeclaration;
import de.ars.daojones.connections.model.UserPasswordCredential;
import de.ars.daojones.eclipse.ui.Activator;
import de.ars.daojones.eclipse.ui.Images;
import de.ars.daojones.eclipse.ui.actions.ConnectionFileEditorContext;
import de.ars.daojones.eclipse.ui.actions.CreateImportDeclarationOperation;
import de.ars.eclipse.ui.util.FilteredContentProvider;
import de.ars.equinox.utilities.PathUtil;
import de.ars.equinox.utilities.ui.ActionUtil;

class MasterBlock extends MasterDetailsBlock implements PropertyChangeListener {

	// TODO Documentation of the plug-in.
	/**
	 * The ID of the context menu.
	 */
	public static String TREE_CONTEXTMENU_ID = Activator.PLUGIN_ID + ".editor.connection.masterblock.tree";
	
	private ConnectionDetails connectionDetailsBlock;
	private final FormPage page;
	private final IConnectionConfiguration conf;
	private boolean dirty = false;
	public MasterBlock(FormPage page, IConnectionConfiguration conf) {
		super();
		this.page = page;
		this.conf = conf;
	}
	
	protected FormPage getPage() {
		return this.page;
	}
	
	protected IConnectionConfiguration getConfiguration() {
		return this.conf;
	}
	
	protected FormToolkit getToolkit() {
		return getManagedForm().getToolkit();
	}
	
	private Composite parent;
	protected Composite getParent() {
		return parent;
	}
	
	private IManagedForm managedForm;
	protected IManagedForm getManagedForm() {
		return managedForm;
	}
	
	private Object getEditorInput() {
		return getManagedForm().getInput();
	}
	
	private IFile getFile() {
		if(null != getEditorInput() && getEditorInput() instanceof IFileEditorInput) {
			final IFileEditorInput fInput = (IFileEditorInput)getEditorInput();
			return fInput.getFile();
		} else return null;
	}

	protected ITreeContentProvider getConnectionsFileProvider() {
		return new FilteredContentProvider(new WorkbenchContentProvider()) {
			private boolean contains(Collection<IImportDeclaration> decs, IPath file) {
				for(IImportDeclaration dec : decs) {
					if(dec.getFile().equals(file.toOSString()) || dec.getFile().equals(file.toPortableString())) return true;
				}
				return false;
			}
			@Override
			protected boolean filter(Object childElement, Object parent) {
				if(!(parent instanceof IContainer)) return false;
				if(childElement instanceof IFile) {
					final IPath path = ((IFile)childElement).getFullPath();
					return
						// Do not show the same file as open!
						!path.equals(getFile().getFullPath())
						&&
						// Do not show already imported files
						!contains(getConfiguration().getImportDeclarations(), PathUtil.makeRelative(getFile().getFullPath(), path))
						// Do only show XML files
						&& ((IFile)childElement).getName().toUpperCase().endsWith(".xml".toUpperCase());
				} else if(childElement instanceof IFolder) {
					// Blend out "empty" folders
					return hasChildren(childElement);
				} else return false;
			}
		};
	}
	
	private SelectionDialog createImportDeclarationChoiceDialog(Shell shell) {
		final ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getPage().getSite().getShell(), new WorkbenchLabelProvider(), getConnectionsFileProvider());
		dialog.setHelpAvailable(false);
		dialog.setTitle("Connections file selection"); 
		dialog.setMessage("Please select the connections file to import."); 
		dialog.setInput(getFile().getProject());
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
		
	private void addImportDeclaration() {
		if(null != getConfiguration()) {
			final IUndoableOperation operation = new AbstractOperation("Add import declarations") {
				private final Collection<IImportDeclaration> declarations = new LinkedList<IImportDeclaration>();
				private boolean wasDirtyBefore = false;
				private IStatus insertDeclarations(IProgressMonitor monitor, IAdaptable info) {
					if(!declarations.isEmpty()) {
						IImportDeclaration i1 = null;
						for(IImportDeclaration i : declarations) {
							getConfiguration().getImportDeclarations().add(i);
							i1 = i;
						}
						connectionsTree.refresh();
						connectionsTree.setSelection(new TreeSelection(new TreePath(new Object[]{"root", i1})), true);
						setDirty(true);
						if(null != connectionDetailsBlock) connectionDetailsBlock.updateCredentials();
						return Status.OK_STATUS;
					} else {
						return Status.CANCEL_STATUS;
					}
				}
				@Override
				public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
					final SelectionDialog dialog = createImportDeclarationChoiceDialog(Display.getCurrent().getActiveShell());
					if (dialog.open() == Window.OK) {
						final Object[] oArr = dialog.getResult();
						for(Object o : oArr) {
							if(o instanceof IFile) {
								final IFile file = (IFile)o;
								final ImportDeclaration importDeclaration = new ImportDeclaration();
								importDeclaration.setFile(PathUtil.makeRelative(getFile().getFullPath(), file.getFullPath()).toPortableString());
								declarations.add(importDeclaration);
							}
						}
						wasDirtyBefore = isDirty();
						return insertDeclarations(monitor, info);
					};
					return Status.CANCEL_STATUS;
				}
				@Override
				public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
					return insertDeclarations(monitor, info);
				}
				@Override
				public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
					if(!declarations.isEmpty()) {
						for(IImportDeclaration i : declarations) {
							getConfiguration().getImportDeclarations().remove(i);
						}
						connectionsTree.refresh();
						if(null != connectionDetailsBlock) connectionDetailsBlock.updateCredentials();
						setDirty(wasDirtyBefore);
						return Status.OK_STATUS;
					} else {
						return Status.CANCEL_STATUS;
					}
				}
			};
			final IWorkbench workbench = PlatformUI.getWorkbench();// getPage().getSite().getWorkbenchWindow().getWorkbench();
			final IWorkbenchOperationSupport opSupport = workbench.getOperationSupport();
			final IOperationHistory operationHistory = opSupport.getOperationHistory();
			final IUndoContext undoContext = opSupport.getUndoContext();
			operation.addContext(undoContext);
			try {
				operationHistory.execute(operation, null, null);
			} catch (ExecutionException e) {
				final String message = "Unable to execute operation \"" + operation.getLabel() + "\"!";
				getLogger().log(ERROR, message, e);
				ErrorDialog.openError(Display.getCurrent().getActiveShell(), "An error occured", message, new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e));
			}
		};
	}

	@Override
	protected void createMasterPart(final IManagedForm managedForm, final Composite parent) {
		this.managedForm = managedForm;
		this.parent = parent;
		getConnectionsSection();
		getConnectionsClient();
		getConnectionsTree();
		
		final Action ADD_IMPORT_ACTION = new Action("New Import Declaration", ImageDescriptor.createFromImage(Images.CONNECTION_IMPORT_NEW.getImage())) {
			@Override
			public void run() {
				addImportDeclaration();
			}
		};
		final Action ADD_CREDENTIAL_ACTION = new Action("New Credential", ImageDescriptor.createFromImage(Images.CREDENTIAL_NEW.getImage())) {
			@Override
			public void run() {
				if(null != getConfiguration()) {
					final UserPasswordCredential cred = new UserPasswordCredential();
					cred.setId("newCredential");
					getConfiguration().getCredentials().add(cred);
					connectionsTree.refresh();
					connectionsTree.setSelection(new TreeSelection(new TreePath(new Object[]{"root", cred})), true);
					setDirty(true);
					if(null != connectionDetailsBlock) connectionDetailsBlock.updateCredentials();
				};
				super.run();
			}
		};
		final Action ADD_CONNECTION_ACTION = new Action("New Connection", ImageDescriptor.createFromImage(Images.CONNECTION_NEW.getImage())) {
			@Override
			public void run() {
				if(null != getConfiguration()) {
					final Connection con = new Connection();
					con.setName("New connection");
					getConfiguration().getConnections().add(con);
					connectionsTree.refresh();
					connectionsTree.setSelection(new TreeSelection(new TreePath(new Object[]{"root", con})), true);
					setDirty(true);
				};
				super.run();
			}
		};
		final ToolBarManager connectionToolbarManager = new ToolBarManager(SWT.FLAT);
		final ToolBar connectionToolbar = connectionToolbarManager.createControl(getConnectionsSection());
		final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
		connectionToolbar.setCursor(handCursor);
		// Cursor needs to be explicitly disposed
		connectionToolbar.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if ((handCursor != null) && (handCursor.isDisposed() == false)) {
					handCursor.dispose();
				}
			}
		});
		connectionToolbarManager.add(ADD_IMPORT_ACTION);
		connectionToolbarManager.add(ADD_CREDENTIAL_ACTION);
		connectionToolbarManager.add(ADD_CONNECTION_ACTION);
		connectionToolbarManager.update(true);
		getConnectionsSection().setTextClient(connectionToolbar);
//		final ToolBar tbTree = new ToolBar(getConnectionsTree().getTree(), SWT.SHADOW_OUT | SWT.FLAT); 
//		final ToolBarManager tbTreeManager = new ToolBarManager(tbTree);
//		tbTreeManager.add(ADD_ACTION);
//		tbTreeManager.update(true);
		
		getManagedForm().addPart(getConnectionsSectionPart());

		// 		Add toolbar buttons to form
//		getManagedForm().getForm().getToolBarManager().add(ADD_IMPORT_ACTION);

	}
	
	/**
	 * Returns the dirty flag
	 * @return the dirty flag
	 */
	public boolean isDirty() {
		return dirty;
	}
	/**
	 * Sets the dirty flag.
	 * @param dirty the dirty flag
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		getManagedForm().dirtyStateChanged();
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		connectionDetailsBlock = new ConnectionDetails(getConfiguration());
		connectionDetailsBlock.addPropertyChangeListener(ConnectionFormBean.PROPERTY_NAME, new PropertyChangeListener() {
			// TODO Java6-Migration
			// @Override
			public void propertyChange(PropertyChangeEvent e) {
				getConnectionsTree().refresh();
			}
		});
		detailsPart.setPageProvider(new IDetailsPageProvider() {
			public IDetailsPage getPage(Object key) {
				if(key instanceof Class<?>) {
					final Class<?> c = (Class<?>)key;
					if(IConnection.class.isAssignableFrom(c)) {
						return connectionDetailsBlock;
					}
				}
				return null;
			}
			public Object getPageKey(Object object) {
				return null != object ? object.getClass() : null;
			}
		});
		//detailsPart.registerPage(IConnection.class, connectionDetailsBlock);
	}
	
	/* *************************
	 *   C O M P O N E N T S   *
	 ************************* */
	
	private Section connectionsSection;
	protected Section getConnectionsSection() {
		if(null == connectionsSection) {
			connectionsSection = getToolkit().createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR);
			connectionsSection.setText("Connections");
			connectionsSection.setDescription("Please select a connection to edit its properties.");
			connectionsSection.setLayoutData(createSectionData());
			connectionsSection.setClient(getConnectionsClient());
		};
		return connectionsSection;
	}
	
	private SectionPart connectionsSectionPart;
	protected SectionPart getConnectionsSectionPart() {
		if(null == connectionsSectionPart) {
			connectionsSectionPart = new SectionPart(getConnectionsSection());
		};
		return connectionsSectionPart;
	}
	
	private Composite connectionsClient;
	protected Composite getConnectionsClient() {
		if(null == connectionsClient) {
			connectionsClient = getToolkit().createComposite(getConnectionsSection());
			connectionsClient.setLayout(new GridLayout(1, false));
		};
		return connectionsClient;
	}
	
	private ConnectionFileEditorContext createContext() {
		final ConnectionFileEditorContext result = new ConnectionFileEditorContext(getConfiguration(), getFile());
		result.addPropertyChangeListener(this);
		return result;
	}
	
	private TreeViewer connectionsTree;
	protected TreeViewer getConnectionsTree() {
		if(null == connectionsTree) {
			connectionsTree = new TreeViewer(getToolkit().createTree(getConnectionsClient(), SWT.BORDER | SWT.MULTI | SWT.V_SCROLL));
			connectionsTree.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
			final MenuManager menuMgr = new MenuManager("#ConnectionsEditorPopupMenu", TREE_CONTEXTMENU_ID);
			menuMgr.setRemoveAllWhenShown(true);
			menuMgr.addMenuListener(new IMenuListener() {
	            public void menuAboutToShow(IMenuManager manager) {
	            	MenuManager newMenu = new MenuManager("&New");
	            	newMenu.add(ActionUtil.createAction(new CreateImportDeclarationOperation(createContext())));
	            	manager.add(newMenu);
	            	
	            	manager.add(new Separator("top"));
	            }
	        });
			connectionsTree.getTree().setMenu(menuMgr.createContextMenu(connectionsTree.getTree()));
			//getPage().getEditorSite().registerContextMenu(TREE_CONTEXTMENU_ID, menuMgr, connectionsTree);
			connectionsTree.addSelectionChangedListener(new ISelectionChangedListener() {
				// TODO Java6-Migration
				// @Override
				public void selectionChanged(SelectionChangedEvent event) {
					getManagedForm().fireSelectionChanged(getConnectionsSectionPart(), event.getSelection());
				}
			});
			connectionsTree.getTree().addKeyListener(new KeyListener() {
				// TODO Java6-Migration
				// @Override
				public void keyPressed(KeyEvent e) {}
				@SuppressWarnings("unchecked")
	// TODO Java6-Migration
	// @Override
				public void keyReleased(KeyEvent e) {
					if(e.keyCode == SWT.DEL) {
						final IStructuredSelection selection = (IStructuredSelection) connectionsTree.getSelection();
						if(!selection.isEmpty()) {
							for(Iterator<Object> it = selection.iterator(); it.hasNext(); ) {
								final Object o = it.next();
								if(o instanceof IImportDeclaration) {
									getConfiguration().getImportDeclarations().remove(o);
									setDirty(true);
									if(null != connectionDetailsBlock) connectionDetailsBlock.updateCredentials();
								} else if(o instanceof ICredential) {
									getConfiguration().getCredentials().remove(o);
									setDirty(true);
									if(null != connectionDetailsBlock) connectionDetailsBlock.updateCredentials();
								} else if(o instanceof IConnection) {
									getConfiguration().getConnections().remove(o);
									setDirty(true);
								}
							}
							connectionsTree.refresh();
						}
					};
				}
			});
			// Problem with selection of objects that are equal but not identical
			connectionsTree.setComparer(new IElementComparer() {
				private int counter = 0;
				// TODO Java6-Migration
				// @Override
				public boolean equals(Object a, Object b) {
					return a == b;
				}
				// TODO Java6-Migration
				// @Override
				public int hashCode(Object element) {
					if(null == element) {
						return 0;
					} else if(element instanceof ICredential || element instanceof IConnection || element instanceof IImportDeclaration) {
						return counter++;
					} else {
						return element.hashCode();
					}
				}
			});
			connectionsTree.setContentProvider(getConnectionConfigurationTreeProvider());
			connectionsTree.setLabelProvider(getConnectionConfigurationTreeProvider());
			connectionsTree.setInput("root");
			connectionsTree.expandAll();
		};
		return connectionsTree;
	}
	
	private ConnectionConfigurationTreeProvider connectionConfigurationTreeProvider;
	protected ConnectionConfigurationTreeProvider getConnectionConfigurationTreeProvider() {
		if(null == connectionConfigurationTreeProvider) {
			connectionConfigurationTreeProvider = new ConnectionConfigurationTreeProvider(getConfiguration());
		};
		return connectionConfigurationTreeProvider;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(ConnectionFileEditorContext.PROPERTY_IMPORTS.equals(evt.getPropertyName())) {
			connectionsTree.refresh();
			//connectionsTree.setSelection(new TreeSelection(new TreePath(new Object[]{"root", i1})), true);
			setDirty(true);
			if(null != connectionDetailsBlock) connectionDetailsBlock.updateCredentials();
		}
	}
	
}
