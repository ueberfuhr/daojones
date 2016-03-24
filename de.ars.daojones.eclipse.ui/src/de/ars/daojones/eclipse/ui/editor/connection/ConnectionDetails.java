package de.ars.daojones.eclipse.ui.editor.connection;

import static de.ars.daojones.eclipse.ui.editor.connection.ConnectionFormBean.PROPERTY_CACHED;
import static de.ars.daojones.eclipse.ui.editor.connection.ConnectionFormBean.PROPERTY_CACHEEXPIRATION;
import static de.ars.daojones.eclipse.ui.editor.connection.ConnectionFormBean.PROPERTY_CONNECTION_POOL;
import static de.ars.daojones.eclipse.ui.editor.connection.ConnectionFormBean.PROPERTY_CREDENTIAL;
import static de.ars.daojones.eclipse.ui.editor.connection.ConnectionFormBean.PROPERTY_DATABASE;
import static de.ars.daojones.eclipse.ui.editor.connection.ConnectionFormBean.PROPERTY_DEFAULT;
import static de.ars.daojones.eclipse.ui.editor.connection.ConnectionFormBean.PROPERTY_DESCRIPTION;
import static de.ars.daojones.eclipse.ui.editor.connection.ConnectionFormBean.PROPERTY_FACTORY;
import static de.ars.daojones.eclipse.ui.editor.connection.ConnectionFormBean.PROPERTY_FORCLASSES;
import static de.ars.daojones.eclipse.ui.editor.connection.ConnectionFormBean.PROPERTY_HOST;
import static de.ars.daojones.eclipse.ui.editor.connection.ConnectionFormBean.PROPERTY_MAXRESULTS;
import static de.ars.daojones.eclipse.ui.editor.connection.ConnectionFormBean.PROPERTY_NAME;
import static de.ars.eclipse.ui.util.FormsUtil.SELECT_ALL_ON_FOCUS;
import static de.ars.eclipse.ui.util.FormsUtil.createLabel;
import static de.ars.eclipse.ui.util.FormsUtil.createLabelData;
import static de.ars.eclipse.ui.util.FormsUtil.createSectionData;
import static de.ars.eclipse.ui.util.FormsUtil.createSingleLineTextData;
import static de.ars.eclipse.ui.util.MessageDialogUtil.openError;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.NumberToStringConverter;
import org.eclipse.core.databinding.conversion.StringToNumberConverter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;

import de.ars.daojones.connections.ApplicationContext;
import de.ars.daojones.connections.Connection;
import de.ars.daojones.connections.ConnectionBuildException;
import de.ars.daojones.connections.model.ConnectionWrapper;
import de.ars.daojones.connections.model.CredentialReference;
import de.ars.daojones.connections.model.IChangeableConnection;
import de.ars.daojones.connections.model.IConnection;
import de.ars.daojones.connections.model.IConnectionConfiguration;
import de.ars.daojones.connections.model.ICredential;
import de.ars.daojones.connections.model.ICredentialReference;
import de.ars.daojones.connections.model.IUserPasswordCredential;
import de.ars.daojones.connections.model.ResolvedConnectionConfiguration;
import de.ars.daojones.connections.model.UserPasswordCredential;
import de.ars.daojones.eclipse.connections.ConnectionFactoryManager;
import de.ars.daojones.eclipse.connections.ConnectionFactoryMetaData;
import de.ars.daojones.eclipse.ui.Activator;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.query.NegativeSearchCriterion;
import de.ars.daojones.runtime.query.TautologySearchCriterion;
import de.ars.eclipse.ui.util.IJavaElementProvider;
import de.ars.equinox.utilities.bridge.Bridge;
import de.ars.equinox.utilities.bridge.BridgeException;
import de.ars.equinox.utilities.ui.formbean.CombinedUpdateValueStrategy;
import de.ars.equinox.utilities.ui.formbean.FormBean.Property;
import de.ars.equinox.utilities.ui.widgets.WidgetUtil;

/**
 * The part of the page showing the details of a connection.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 *
 */
public class ConnectionDetails implements IDetailsPage, PropertyChangeListener {

	private final ConnectionFormBean model = new ConnectionFormBean();
	private final IConnectionConfiguration conf;
	
	private IManagedForm managedForm;
	private Text txtTitle;
	private Text txtDescription;
	private Button chkDefault;
	private Text txtDatabase;
	private Text txtHost;
	private Text txtUser;
	private Text txtPassword;
	private Combo cbFactory;
	private ComboViewer cbCredentialViewer;
	
	private final Map<String, ConnectionFactoryMetaData> factoriesById;
	private final Map<String, ConnectionFactoryMetaData> factoriesByName;
	private final Map<String, ConnectionFactoryMetaData> factoriesByClassName;

	/**
	 * Creates an instance.
	 * @param conf the {@link IConnectionConfiguration}
	 */
	public ConnectionDetails(final IConnectionConfiguration conf) {
		super();
		this.conf = conf;
		factoriesById = ConnectionFactoryManager.getInstance().getFactories();
		factoriesByName = new HashMap<String, ConnectionFactoryMetaData>();
		factoriesByClassName = new HashMap<String, ConnectionFactoryMetaData>();
		for(ConnectionFactoryMetaData md : factoriesById.values()) {
			factoriesByName.put(md.getName(), md);
			factoriesByClassName.put(md.getInstance().getClass().getName(), md);
		}
	}
	
	/**
	 * Updates the entries in the credential selection combobox.
	 */
	public void updateCredentials() {
		if(null != cbCredentialViewer) cbCredentialViewer.refresh(true);
	}
	
	/**
	 * Adds a {@link PropertyChangeListener}.
	 * @param property the property that should change
	 * @param l the {@link PropertyChangeListener}
	 */
	public void addPropertyChangeListener(final Property<IChangeableConnection, ?> property, PropertyChangeListener l) {
		model.addPropertyChangeListener(property, l);
	}

	/**
	 * Removes a {@link PropertyChangeListener}.
	 * @param l the {@link PropertyChangeListener}
	 */
	public void removePropertyChangeListener(PropertyChangeListener l) {
		model.removePropertyChangeListener(l);
	}

	/**
	 * @see IDetailsPage#initialize(IManagedForm)
	 */
	// TODO Java6-Migration
	// @Override
	public void initialize(IManagedForm form) {
		this.managedForm = form;
	}

	/**
	 * Returns the parent managed form.
	 * @return the parent managed form
	 */
	protected IManagedForm getManagedForm() {
		return this.managedForm;
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
	
	/**
	 * @see IDetailsPage#createContents(Composite)
	 */
	// TODO Java6-Migration
	// @Override
	public void createContents(final Composite parent) {
		
		// Don't forget to layout the parent!!!
		parent.setLayout(new GridLayout(1, false));

		final FormToolkit toolkit = getManagedForm().getToolkit();
		/*
		 * G E N E R A L   S E C T I O N
		 */
		final Section generalSection = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		generalSection.setText("General properties");
		generalSection.setLayoutData(createSectionData());
		final Composite generalClient = toolkit.createComposite(generalSection);
		generalClient.setLayout(new GridLayout(2, false));
		generalSection.setClient(generalClient);
		// title
		createLabel(toolkit, generalClient, "Title:", "The title of the connection that is only used for displaying in the UI.", true);
		txtTitle = toolkit.createText(generalClient, "", SWT.BORDER | SWT.SINGLE);
		txtTitle.setLayoutData(createSingleLineTextData(true));
		txtTitle.addFocusListener(SELECT_ALL_ON_FOCUS);
		// description
		createLabel(toolkit, generalClient, "Description:", "The short comment describing the connection.", false);
		txtDescription = toolkit.createText(generalClient, "", SWT.BORDER | SWT.SINGLE);
		txtDescription.setLayoutData(createSingleLineTextData(true));
		txtDescription.addFocusListener(SELECT_ALL_ON_FOCUS);
		// Default Checkbox
		chkDefault = toolkit.createButton(generalClient, "Use this connection per default", SWT.CHECK);
		final GridData chkDefaultData = (GridData)createLabelData(generalClient);
		chkDefaultData.horizontalSpan = 2;
		chkDefault.setLayoutData(chkDefaultData);

		IJavaElementProvider javaElementProviderTmp = null;
		try {
			javaElementProviderTmp = Bridge.get(IJavaElementProvider.class);
		} catch (BridgeException e3) {
			MessageDialog.openError(getManagedForm().getForm().getShell(), "Error during initialization!", "No bridge of type \"" + IJavaElementProvider.class.getName() + "\" was found!");
		}
		final IJavaElementProvider javaElementProvider = javaElementProviderTmp;
		final Composite pnlForClasses = toolkit.createComposite(generalClient);
		pnlForClasses.setLayoutData(createSectionData());
		((GridData)pnlForClasses.getLayoutData()).horizontalSpan = 2;
		pnlForClasses.setLayout(new GridLayout(2, false));
		((GridData)createLabel(toolkit, pnlForClasses, "Please add classes that should be handled by this connection.", null, false).getLayoutData()).horizontalSpan = 2;
		final TableViewer forClassesList = new TableViewer(pnlForClasses);
		final GridData forClassesListLayoutData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		// Set preferred size because of layout problems with dynamic list height
		forClassesListLayoutData.heightHint = 60;
		forClassesList.getControl().setLayoutData(forClassesListLayoutData);
		if(null != javaElementProvider) forClassesList.setLabelProvider(javaElementProvider.getLabelProvider(getFile().getProject()));
		forClassesList.setContentProvider(new IStructuredContentProvider() {
			// TODO Java6-Migration
			// @Override
			public Object[] getElements(Object inputElement) {
				final Set<String> forClasses = new TreeSet<String>();
				if(null != ((ConnectionFormBean)inputElement).getForClasses()) forClasses.addAll(((ConnectionFormBean)inputElement).getForClasses());
				return forClasses.toArray(new String[forClasses.size()]);
			}
			// TODO Java6-Migration
			// @Override
			public void dispose() {}
			// TODO Java6-Migration
			// @Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
		});
		forClassesList.setInput(this.model);
		
		final Composite pnlForClassesButtons = toolkit.createComposite(pnlForClasses);
		pnlForClassesButtons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		pnlForClassesButtons.setLayout(new GridLayout(1, false));
		final Button btnAdd = toolkit.createButton(pnlForClassesButtons, "Add...", SWT.PUSH);
		btnAdd.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Collection<String> forClassesToAdd = javaElementProvider.getSelectionOfClasses(parent.getShell(), getFile().getProject());
				if(null != forClassesToAdd) {
					final Collection<String> forClasses = new TreeSet<String>();
					forClasses.addAll(model.getForClasses());
					for(String forClassToAdd : forClassesToAdd) {
						forClasses.add(forClassToAdd);
					}
					model.setForClasses(forClasses);
//					forClassesList.refresh();
				}
			}
		});
		if(null == javaElementProvider) btnAdd.setEnabled(false);
		// ENTF/DEL removes currently selected objects
		final Action deleteForClassAction = new Action() {
			@Override
			public void run() {
				final int index = forClassesList.getTable().getSelectionIndex();
				final IStructuredSelection selection = (IStructuredSelection) forClassesList.getSelection();
				final Collection<String> forClasses = new TreeSet<String>();
				forClasses.addAll(model.getForClasses());
				for(Object o : selection.toList()) {
					forClasses.remove(o);
				}
				model.setForClasses(forClasses);
//				forClassesList.refresh(false);
				forClassesList.getTable().setSelection(index);
			}
		};
		final Button btnRemove = toolkit.createButton(pnlForClassesButtons, "Remove", SWT.PUSH);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteForClassAction.run();
			}
		});
		btnRemove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btnRemove.setEnabled(false);
		forClassesList.addSelectionChangedListener(new ISelectionChangedListener() {
			// TODO Java6-Migration
			// @Override
			public void selectionChanged(SelectionChangedEvent event) {
				btnRemove.setEnabled(!event.getSelection().isEmpty());
			}
		});
		forClassesList.getTable().addKeyListener(new KeyListener() {
			// TODO Java6-Migration
			// @Override
			public void keyPressed(KeyEvent e) {}
			// TODO Java6-Migration
			// @Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == SWT.DEL && btnRemove.isEnabled()) {
					deleteForClassAction.run();
				};
			}
		});
		
		// PropertyChangeListeners
		addPropertyChangeListener(PROPERTY_FORCLASSES, new PropertyChangeListener() {
			// TODO Java6-Migration
			// @Override
			public void propertyChange(PropertyChangeEvent e) {
				forClassesList.refresh();
			}
		});
//		addPropertyChangeListener(new PropertyChangeListener() {
//			@Override
//			public void propertyChange(PropertyChangeEvent e) {
//				if(ConnectionFormBean.PROPERTY_DEFAULT.equals(e.getPropertyName())) {
//					setVisible(!(Boolean)e.getNewValue(), parent, pnlForClasses);
//				}
//			}
//		});
		
		/*
		 * C O N N E C T I O N D A T A   S E C T I O N
		 */
		final Section connectionDataSection = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		connectionDataSection.setText("Connection data");
		connectionDataSection.setLayoutData(createSectionData());
		final Composite connectionDataClient = toolkit.createComposite(connectionDataSection);
		connectionDataClient.setLayout(new GridLayout(2, false));
		connectionDataSection.setClient(connectionDataClient);
		
		// vendor
		createLabel(toolkit, connectionDataClient, "Vendor:", "The driver vendor for the database.", true);
		cbFactory = new Combo(connectionDataClient, SWT.DROP_DOWN | SWT.READ_ONLY);
		cbFactory.setLayoutData(createSingleLineTextData(true));
		toolkit.adapt(cbFactory, true, true);
		// set items
		cbFactory.setItems(factoriesByName.keySet().toArray(new String[factoriesByName.size()]));
		
		// database
		createLabel(toolkit, connectionDataClient, "Database:", "The (file) name of the database.", true);
		txtDatabase = toolkit.createText(connectionDataClient, "", SWT.BORDER | SWT.SINGLE);
		txtDatabase.setLayoutData(createSingleLineTextData(true));
		txtDatabase.addFocusListener(SELECT_ALL_ON_FOCUS);

		// host
		/*final Control lblHost = */createLabel(toolkit, connectionDataClient, "Host:", "The name or IP address of the host. Leave empty for local access.", false);
		txtHost = toolkit.createText(connectionDataClient, "", SWT.BORDER | SWT.SINGLE);
		txtHost.setLayoutData(createSingleLineTextData(true));
		txtHost.addFocusListener(SELECT_ALL_ON_FOCUS);

		// credential
		createLabel(toolkit, connectionDataClient, "Credential:", "The credential that is used for connecting the database.", false);
		final Combo cbCredential = new Combo(connectionDataClient, SWT.DROP_DOWN | SWT.READ_ONLY);
		cbCredential.setLayoutData(createSingleLineTextData(true));
		toolkit.adapt(cbCredential, true, true);
		// set items
		cbCredentialViewer = new ComboViewer(cbCredential);
		final String NO_CREDENTIAL = new String("-- Do not use any credential --");
		final String CUSTOM_CREDENTIAL = new String("-- Define a custom credential --");
		cbCredentialViewer.setContentProvider(new IStructuredContentProvider() {
			// TODO Java6-Migration
			// @Override
			public Object[] getElements(Object inputElement) {
				final Collection<String> credentialIDs = new TreeSet<String>();
				// fetch credential IDs
				IConnectionConfiguration conf = ConnectionDetails.this.conf;
				try {
					conf = new ResolvedConnectionConfiguration(conf);
				} catch (IOException e) {
					Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unable to resolve imported connection configurations!", e));
				}
				for(ICredential cred : conf.getCredentials()) {
					credentialIDs.add(cred.getId());
				}
				// create result array
				final Collection<String> result = new ArrayList<String>(credentialIDs.size()+2);
				result.add(NO_CREDENTIAL);
				result.addAll(credentialIDs);
				result.add(CUSTOM_CREDENTIAL);
				return result.toArray(new String[result.size()]);
			}
			// TODO Java6-Migration
			// @Override
			public void dispose() {}
			// TODO Java6-Migration
			// @Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
		});
		cbCredentialViewer.setInput("root");
		
		// user
		final Control lblUser = createLabel(toolkit, connectionDataClient, "Username:", "The name of the user account for login. (if the database is secured)", false);
		txtUser = toolkit.createText(connectionDataClient, "", SWT.BORDER | SWT.SINGLE);
		txtUser.setLayoutData(createSingleLineTextData(true));
		txtUser.addFocusListener(SELECT_ALL_ON_FOCUS);

		// password
		final Control lblPassword = createLabel(toolkit, connectionDataClient, "Password:", "The password of the user account for login. (if the database is secured)", false);
		txtPassword = toolkit.createText(connectionDataClient, "", SWT.BORDER | SWT.SINGLE);
		txtPassword.setLayoutData(createSingleLineTextData(true));
		txtPassword.addFocusListener(SELECT_ALL_ON_FOCUS);
		txtPassword.setEchoChar('*');
		
		addPropertyChangeListener(PROPERTY_CREDENTIAL, new PropertyChangeListener() {
			// TODO Java6-Migration
			// @Override
			public void propertyChange(PropertyChangeEvent e) {
				final ICredential cred = (ICredential)e.getNewValue();
				final boolean isCustom = null != cred && cred instanceof IUserPasswordCredential;
				// disable
				WidgetUtil.setVisible(isCustom, lblUser, txtUser, lblPassword, txtPassword);
				parent.layout();
			}
		});

		final Hyperlink lnkTestConnection = toolkit.createHyperlink(connectionDataClient, "Test connection from this machine.", SWT.NONE);
		final GridData gdLnkTestConnection = new GridData();
		gdLnkTestConnection.horizontalSpan = 2;
		lnkTestConnection.setLayoutData(gdLnkTestConnection);
		lnkTestConnection.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				try {
					final ApplicationContext ctx = new ApplicationContext("") {
//						@Override
//						protected ConnectionFactory createConnectionFactory(String classname) throws ConnectionBuildException {
//							final ConnectionFactoryMetaData md = factoriesByClassName.get(classname);
//							if(null != md) return md.getInstance();
//							throw new ConnectionBuildException("There is no Connection Factory registered with class \"" + classname + "\"!");
//						}
					};
					final IConnection testConnection = new ConnectionWrapper(model.getModel()) {
						private static final long serialVersionUID = 1L;
						@Override
						public Collection<String> getForClasses() {
							return Arrays.asList(Dao.class.getName());
						}
					};
					ctx.createConnection(testConnection);
					try {
						// Get connection instance(s)
						final Connection<TestDao> con = Connection.get(ctx, TestDao.class); 
						if(null != con) {
							con.find(new NegativeSearchCriterion(new TautologySearchCriterion()));
							MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Connection Test successful!", "1 connection tested.");
						} else {
							MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "The connection is never used!", "Please set the connection as default or add special DAO classes!");
						}
					} finally {
						try {
							ctx.destroy();
						} catch (DataAccessException e1) {}
					}
				} catch (DataAccessException e1) {
					try {
						throw e1.getCause();
					} catch(ConnectionBuildException e2) {
						openError("Error during building connections!", e2);
					} catch(Throwable t) {
						openError("Error during reading data from connections!", e1);
					}
				}
			}
		});

		/*
		 * C A C H I N G D A T A   S E C T I O N
		 */
		final Section cachingDataSection = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE);
		cachingDataSection.setText("Caching");
		cachingDataSection.setLayoutData(createSectionData());
		final Composite cachingDataClient = toolkit.createComposite(cachingDataSection);
		cachingDataClient.setLayout(new GridLayout(2, false));
		cachingDataSection.setClient(cachingDataClient);
		
		// Caching Checkbox
		final Button chkCached = toolkit.createButton(cachingDataClient, "Enable result caching", SWT.CHECK);
		final GridData chkCachedData = (GridData)createLabelData(cachingDataClient);
		chkCachedData.horizontalSpan = 2;
		chkCached.setLayoutData(chkCachedData);

		final Control lblCacheExpiration = createLabel(toolkit, cachingDataClient, "Cache entries expire after (ms):", "Leave empty if cache entries should not expire.", false);
		lblCacheExpiration.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		lblCacheExpiration.setEnabled(false);
		final Text txtCacheExpiration = toolkit.createText(cachingDataClient, "", SWT.BORDER | SWT.SINGLE);
		txtCacheExpiration.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtCacheExpiration.setEnabled(false);
		addPropertyChangeListener(PROPERTY_CACHED, new PropertyChangeListener() {
			// TODO Java6-Migration
			// @Override
			public void propertyChange(PropertyChangeEvent e) {
				final boolean cached = ((Boolean)e.getNewValue()).booleanValue();
				lblCacheExpiration.setEnabled(cached);
				txtCacheExpiration.setEnabled(cached);
			}
		});
		/*
		 * C O N N E C T I O N P O O L I N G D A T A   S E C T I O N
		 */
		final Section connectionPoolingDataSection = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE);
		connectionPoolingDataSection.setText("Connection Pooling");
		connectionPoolingDataSection.setLayoutData(createSectionData());
		final Composite connectionPoolingDataClient = toolkit.createComposite(connectionPoolingDataSection);
		connectionPoolingDataClient.setLayout(new GridLayout(2, false));
		connectionPoolingDataSection.setClient(connectionPoolingDataClient);
		
		// MinConnections
		createLabel(toolkit, connectionPoolingDataClient, "Minimum connection count:", "The minimum count of connections that should be initialized.", true);
		final Text txtMinimumConnections = toolkit.createText(connectionPoolingDataClient, "1", SWT.BORDER | SWT.SINGLE);
		txtMinimumConnections.setLayoutData(createSingleLineTextData(true));
		// MaxConnections
		createLabel(toolkit, connectionPoolingDataClient, "Maximum connection count:", "The maximum count of connections that should be initialized.", true);
		final Text txtMaximumConnections = toolkit.createText(connectionPoolingDataClient, "1", SWT.BORDER | SWT.SINGLE);
		txtMaximumConnections.setLayoutData(createSingleLineTextData(true));

		/*
		 * F U R T H E R   S E C T I O N
		 */
		final Section furtherSection = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE);
		furtherSection.setText("Further Settings");
		furtherSection.setLayoutData(createSectionData());
		final Composite furtherDataClient = toolkit.createComposite(furtherSection);
		furtherDataClient.setLayout(new GridLayout(2, false));
		furtherSection.setClient(furtherDataClient);
		// MaxResults
		final Button chkLimited = toolkit.createButton(furtherDataClient, "Limit resultsets to (elements):", SWT.CHECK);
//		createLabel(toolkit, furtherDataClient, "Maximum result count:", "The maximum count of results loaded from the database.", true);
		final Text txtMaximumResults = toolkit.createText(furtherDataClient, "", SWT.BORDER | SWT.SINGLE);
		txtMaximumResults.setLayoutData(createSingleLineTextData(true));
		addPropertyChangeListener(PROPERTY_MAXRESULTS, new PropertyChangeListener() {
			// TODO Java6-Migration
			// @Override
			public void propertyChange(PropertyChangeEvent e) {
				final int limit = (Integer)e.getNewValue();
				final boolean limited = limit>0 && limit <Integer.MAX_VALUE;
				txtMaximumResults.setEnabled(limited);
			}
		});

//		toolkit.createFormText(getManagedForm().getForm().getBody(), true).setText("mytext http://www.ars.de", false, true);

		// Create bindings
		this.model.observeText(txtTitle, PROPERTY_NAME);
		this.model.observeText(txtDescription, PROPERTY_DESCRIPTION);
		this.model.observeSelection(chkDefault, PROPERTY_DEFAULT);
		this.model.observeText(txtHost, PROPERTY_HOST);
		this.model.observeSelection(cbCredential, PROPERTY_CREDENTIAL,
			// target-to-model: factory name -> class name
			new UpdateValueStrategy() {
				@Override
				public Object convert(Object value) {
					if(null == value || NO_CREDENTIAL.equals(value)) return null;
					if(CUSTOM_CREDENTIAL.equals(value)) {
						return new UserPasswordCredential();
					} else {
						return new CredentialReference(value.toString());
					}
				}
			}, 
			// model-to-target: class name -> factory name
			new UpdateValueStrategy() {
				@Override
				public Object convert(Object value) {
					if(null != value && value instanceof IUserPasswordCredential) {
						return CUSTOM_CREDENTIAL;
					} else if(null != value && value instanceof ICredentialReference) {
						return ((ICredentialReference)value).getReferenceId();
					} else {
						return NO_CREDENTIAL;
					}
				}
			}
		);
		this.model.observeText(txtUser, PROPERTY_CREDENTIAL, this.model.USERNAME_WRITE_TO_CREDENTIAL, this.model.USERNAME_READ_FROM_CREDENTIAL);
		this.model.observeText(txtPassword, PROPERTY_CREDENTIAL, this.model.PASSWORD_WRITE_TO_CREDENTIAL, this.model.PASSWORD_READ_FROM_CREDENTIAL);
		this.model.observeText(txtDatabase, PROPERTY_DATABASE);
		this.model.observeSelection(cbFactory, PROPERTY_FACTORY,
			// target-to-model: factory name -> class name
			new UpdateValueStrategy() {
				@Override
				public Object convert(Object value) {
					if(null == value) return "";
					final ConnectionFactoryMetaData md = factoriesByName.get(value.toString());
					return null == md ? value : md.getInstance().getClass().getName();
				}
			}, 
			// model-to-target: class name -> factory name
			new UpdateValueStrategy() {
				@Override
				public Object convert(Object value) {
					if(null == value) return "";
					final ConnectionFactoryMetaData md = factoriesByClassName.get(value.toString());
					return null == md ? value : md.getName();
				}
			}
		);
		this.model.observeSelection(chkCached, PROPERTY_CACHED);
		this.model.observeText(txtCacheExpiration, PROPERTY_CACHEEXPIRATION);
		this.model.observeText(txtMinimumConnections, PROPERTY_CONNECTION_POOL, new CombinedUpdateValueStrategy(new TextToIntegerUpdateStrategy(), this.model.MIN_CONNECTIONS_WRITE_TO_CONNECTIONPOOL), new CombinedUpdateValueStrategy(this.model.MIN_CONNECTIONS_READ_FROM_CONNECTIONPOOL, new IntegerToTextUpdateStrategy()));
		this.model.observeText(txtMaximumConnections, PROPERTY_CONNECTION_POOL, new CombinedUpdateValueStrategy(new TextToIntegerUpdateStrategy(), this.model.MAX_CONNECTIONS_WRITE_TO_CONNECTIONPOOL), new CombinedUpdateValueStrategy(this.model.MAX_CONNECTIONS_READ_FROM_CONNECTIONPOOL, new IntegerToTextUpdateStrategy()));
		this.model.observeSelection(chkLimited, PROPERTY_MAXRESULTS, this.model.RESULTSLIMITED_WRITE_TO_MAXRESULTS, this.model.RESULTSLIMITED_READ_FROM_MAXRESULTS);
		this.model.observeText(txtMaximumResults, PROPERTY_MAXRESULTS, new TextToIntegerUpdateStrategy(), new IntegerToTextUpdateStrategy());
	}
	
	private static class TextToIntegerUpdateStrategy extends UpdateValueStrategy {
		private final StringToNumberConverter converter = StringToNumberConverter.toInteger(true);
		@Override
		public Object convert(Object value) {
			return converter.convert(value);
		}
	}
	private static class IntegerToTextUpdateStrategy extends UpdateValueStrategy {
		private final NumberToStringConverter converter = NumberToStringConverter.fromInteger(true);
		@Override
		public Object convert(Object value) {
			return converter.convert(value);
		}
	}

	/**
	 * @see IDetailsPage#commit(boolean)
	 */
	// TODO Java6-Migration
	// @Override
	public void commit(boolean onSave) {
		if(onSave) setDirty(false);
	}
	/**
	 * @see IDetailsPage#dispose()
	 */
	// TODO Java6-Migration
	// @Override
	public void dispose() {}
	/**
	 * @see IDetailsPage#isDirty()
	 */
	// TODO Java6-Migration
	// @Override
	public boolean isDirty() {
		return dirty;
	}
	/**
	 * @see IDetailsPage#isStale()
	 */
	// TODO Java6-Migration
	// @Override
	public boolean isStale() {
		return false;
	}
	
	/**
	 * @see IDetailsPage#refresh()
	 */
	// TODO Java6-Migration
	// @Override
	public void refresh() {
	}
	/**
	 * @see IDetailsPage#setFocus()
	 */
	// TODO Java6-Migration
	// @Override
	public void setFocus() {
		this.txtTitle.setFocus();
	}
	/**
	 * @see IDetailsPage#setFormInput(Object)
	 */
	// TODO Java6-Migration
	// @Override
	public boolean setFormInput(Object input) {
		setDirty(false);
		return true;
	}
	/**
	 * @see IDetailsPage#selectionChanged(IFormPart, ISelection)
	 */
	// TODO Java6-Migration
	// @Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		if(selection instanceof IStructuredSelection) {
			final Object o = ((IStructuredSelection)selection).getFirstElement();
			if(o instanceof IChangeableConnection) setModel((IChangeableConnection)o);
		}
	}
	
	private void setModel(IChangeableConnection model) {
		// Create combobox entries
//		final Collection<String> newFactoryItems = new TreeSet<String>();
//		newFactoryItems.addAll(factoriesByName.keySet());
//		if(null != model && null != model.getFactory() && !factoriesByClassName.containsKey(model.getFactory())) {
//			newFactoryItems.add(model.getFactory());
//		}
//		final String[] items = newFactoryItems.toArray(new String[newFactoryItems.size()]);
//		cbFactory.setItems(items);
		//cbFactory.setText(null != model && null != model.getFactory() ? model.getFactory() : "");
		this.model.removePropertyChangeListener(this);
		this.model.setModel(model);
		this.model.addPropertyChangeListener(this);
		
		refresh();
	}
	
	private boolean dirty = false;
	
	private void setDirty(boolean dirty) {
		this.dirty = dirty;
		if(null != this.managedForm) this.managedForm.dirtyStateChanged();
	}

	private static boolean equals(Object o1, Object o2) {
		if(null == o1 && null == o2) return true;
		if(null == o1 || null == o2) return false;
		return o1.equals(o2);
	}
	
	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	// TODO Java6-Migration
	// @Override
	public void propertyChange(java.beans.PropertyChangeEvent e) {
		setDirty(isDirty() || !equals(e.getOldValue(), e.getNewValue()));
	}
	
}
