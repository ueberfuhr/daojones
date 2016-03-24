package de.ars.daojones.eclipse.jdt.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.ars.daojones.eclipse.jdt.ui.Activator;

import static de.ars.daojones.eclipse.jdt.ui.preferences.Constants.*;

/**
 * An initializer for preferences.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class PreferencesInitializer extends AbstractPreferenceInitializer {

	/**
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PREF_WORKBENCH_CONSOLE_AUTOACTIVATE.getLocalName(), PREF_WORKBENCH_CONSOLE_AUTOACTIVATE_DEFAULT);
		store.setDefault(PREF_WORKBENCH_CONSOLE_LEVEL.getLocalName(), PREF_WORKBENCH_CONSOLE_LEVEL_DEFAULT);
	}

}
