package de.ars.daojones.eclipse.jdt.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.ars.daojones.eclipse.jdt.Activator;
import de.ars.daojones.eclipse.jdt.Artifacts;
import de.ars.daojones.eclipse.jdt.preferences.Constants;

/**
 * The preferences for the java project.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ProjectJavaPropertiesPage extends AbstractJavaProjectPreferencesPage {
	
	/**
	 * Create the preference page
	 */
	public ProjectJavaPropertiesPage() {
		super(null);
	}
	/**
	 * @see org.eclipse.ui.dialogs.PropertyPage#setElement(org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public void setElement(IAdaptable element) {
		super.setElement(element);
		final IProject project = null != element ? (IProject)element.getAdapter(IProject.class) : null;
		if(null != project) {
			final IPreferenceStore store = new ScopedPreferenceStore(
				new ProjectScope(project),
				Activator.getDefault().getBundle().getSymbolicName()
			);
			super.setPreferenceStore(store);
			store.addPropertyChangeListener(new IPropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if(Constants.PREF_DESTINATION_SOURCEFOLDER.getLocalName().equals(event.getProperty())) {
						try {
							Artifacts.setDestinationSourceFolderName(project, (String)event.getNewValue());
						} catch (CoreException e) {
							MessageDialog.openError(ProjectJavaPropertiesPage.this.getShell(), "An error occured!", e.getMessage());
						}
					}
				}
			});
		}
	}
	
	

}
