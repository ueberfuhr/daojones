package de.ars.daojones.eclipse.jdt.preferences;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import de.ars.daojones.eclipse.jdt.Activator;

import static de.ars.daojones.eclipse.jdt.preferences.Constants.*;

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
		final Preferences prefs = Activator.getDefault().getPluginPreferences();
		prefs.setDefault(PREF_DESTINATION_SOURCEFOLDER.getLocalName(), PREF_DESTINATION_SOURCEFOLDER_DEFAULT);
	}

}
