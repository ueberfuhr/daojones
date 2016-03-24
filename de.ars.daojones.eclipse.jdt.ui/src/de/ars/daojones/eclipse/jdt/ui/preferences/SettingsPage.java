package de.ars.daojones.eclipse.jdt.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * A page containing UI elements to change the property settings.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class SettingsPage extends PreferencePage implements	IWorkbenchPreferencePage {

	private IWorkbench workbench;
	
	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		new Label(parent, SWT.NULL).setText("Please specify the settings for DaoJones.");
		return parent;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		this.workbench = workbench;
		this.setTitle("DaoJones Settings");
	}
	/**
	 * Returns the {@link IWorkbench}.
	 * @return the {@link IWorkbench}
	 */
	protected IWorkbench getWorkbench() {
		return this.workbench;
	}

}
