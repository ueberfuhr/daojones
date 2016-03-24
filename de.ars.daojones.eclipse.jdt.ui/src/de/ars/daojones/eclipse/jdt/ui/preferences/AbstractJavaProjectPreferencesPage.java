package de.ars.daojones.eclipse.jdt.ui.preferences;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.swtdesigner.FieldLayoutPreferencePage;
import com.swtdesigner.ResourceManager;

/**
 * The preferences for the workbench.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
abstract class AbstractJavaProjectPreferencesPage extends FieldLayoutPreferencePage {

	/**
	 * Create the preference page.
	 * @param preferencesStore the {@link IPreferenceStore}
	 */
	public AbstractJavaProjectPreferencesPage(final IPreferenceStore preferencesStore) {
		super();
		setPreferenceStore(preferencesStore);
	}
	
	/**
	 * Create contents of the preference page
	 * @param parent
	 */
	@Override
	public Control createPageContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new FormLayout());

		final Group pnlProjectDefaultSettings = new Group(container, SWT.NONE);
		final FormData fd_pnlProjectDefaultSettings = new FormData();
		fd_pnlProjectDefaultSettings.right = new FormAttachment(100, -5);
		fd_pnlProjectDefaultSettings.bottom = new FormAttachment(0, 60);
		fd_pnlProjectDefaultSettings.top = new FormAttachment(0, 5);
		fd_pnlProjectDefaultSettings.left = new FormAttachment(0, 5);
		pnlProjectDefaultSettings.setLayoutData(fd_pnlProjectDefaultSettings);
		final FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		fillLayout.marginWidth = 5;
		fillLayout.marginHeight = 10;
		pnlProjectDefaultSettings.setLayout(fillLayout);
		pnlProjectDefaultSettings.setRegion(null);
		pnlProjectDefaultSettings.setText("Project Default Settings");

		final Composite composite = new Composite(pnlProjectDefaultSettings, SWT.NONE);
		composite.setLayout(new FormLayout());
		final Label lblIconDestinationSourceFolder = new Label(composite, SWT.NULL);
		final FormData lblIconDestinationSourceFolderData = new FormData();
		lblIconDestinationSourceFolderData.left = new FormAttachment(0,0);
		lblIconDestinationSourceFolderData.right = new FormAttachment(0,16);
		lblIconDestinationSourceFolderData.top = new FormAttachment(0,0);
		//lblIconDestinationSourceFolderData.bottom = new FormAttachment(0,16);
		lblIconDestinationSourceFolder.setLayoutData(lblIconDestinationSourceFolderData);
		lblIconDestinationSourceFolder.setImage(
			ResourceManager.getPluginImage(
				Platform.getBundle("org.eclipse.jdt.ui").getBundleContext(), 
				"icons/full/obj16/packagefolder_obj.gif"
			)
		);
		final Composite composite2 = new Composite(composite, SWT.NONE);
		final FormData composite2Data = new FormData();
		composite2Data.left = new FormAttachment(0, 20);
		composite2Data.top = new FormAttachment(0,0);
		composite2Data.right = new FormAttachment(100,0);
		composite2.setLayoutData(composite2Data);

		final StringFieldEditor txtDestinationSourceFolder = new StringFieldEditor(
				de.ars.daojones.eclipse.jdt.preferences.Constants.PREF_DESTINATION_SOURCEFOLDER.getLocalName(), 
				"Source folder for generated files:", 
				composite2) {
			@Override
			protected boolean doCheckState() {
				return
					super.doCheckState()
					&&
					Character.isLetter(getStringValue().charAt(0))
					&&
					(
						Character.isLetter(getStringValue().charAt(getStringValue().length()-1))
						||
						Character.isDigit(getStringValue().charAt(getStringValue().length()-1))
					)
					&&
					getStringValue().indexOf(File.separator)<0
				;
			}
		};
		txtDestinationSourceFolder.setErrorMessage("Please specify a valid source folder name!");
		txtDestinationSourceFolder.setEmptyStringAllowed(false);
		txtDestinationSourceFolder.setPreferenceStore(getPreferenceStore());
		txtDestinationSourceFolder.setPage(this);

		addField(txtDestinationSourceFolder);

		return container;
	}

}
