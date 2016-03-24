package de.ars.daojones.eclipse.jdt.ui.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import de.ars.daojones.eclipse.jdt.Activator;

/**
 * The preferences for the workbench.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class WorkbenchPreferencesJavaSettings extends AbstractJavaProjectPreferencesPage implements IWorkbenchPreferencePage {

	/**
	 * Create the preference page
	 */
	public WorkbenchPreferencesJavaSettings() {
		super(
			new ScopedPreferenceStore(
				new InstanceScope(),
				Activator.getDefault().getBundle().getSymbolicName()
			)
		);
	}
	
	/**
	 * Initialize the preference page
	 * @param workbench 
	 */
	public void init(IWorkbench workbench) {}

}
