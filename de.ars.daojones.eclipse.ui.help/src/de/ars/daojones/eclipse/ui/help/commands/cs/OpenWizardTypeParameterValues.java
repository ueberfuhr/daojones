package de.ars.daojones.eclipse.ui.help.commands.cs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.IParameterValues;

/**
 * An implementation of {@link IParameterValues} providing possible parameters
 * for the type of wizard that should be opened using the {@link OpenWizard}
 * handler.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class OpenWizardTypeParameterValues implements IParameterValues {

	/**
	 * @see org.eclipse.core.commands.IParameterValues#getParameterValues()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map getParameterValues() {
		final Map<String, String> result = new HashMap<String, String>();
		result.put("New Wizard", "org.eclipse.ui.newWizards");
		result.put("Import Wizard", "org.eclipse.ui.importWizards");
		result.put("Exort Wizard", "org.eclipse.ui.exportWizards");
		return result;
	}

}
