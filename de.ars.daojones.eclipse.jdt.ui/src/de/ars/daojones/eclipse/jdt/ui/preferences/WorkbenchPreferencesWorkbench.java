package de.ars.daojones.eclipse.jdt.ui.preferences;

import java.util.logging.Level;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.swtdesigner.FieldLayoutPreferencePage;

/**
 * The preferences for the workbench.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class WorkbenchPreferencesWorkbench extends FieldLayoutPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Create the preference page
	 */
	public WorkbenchPreferencesWorkbench() {
		super();
	}

	/**
	 * Create contents of the preference page
	 * @param parent
	 */
	@Override
	public Control createPageContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(1, false));

		final Group pnlConsole = new Group(container, SWT.NONE);
		pnlConsole.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pnlConsole.setLayout(new GridLayout(1, false));
		pnlConsole.setRegion(null);
		pnlConsole.setText("Bean Builder Console");

		final Composite pnlShowConsoleAutomatically = new Composite(pnlConsole, SWT.NONE);
		pnlShowConsoleAutomatically.setLayout(new GridLayout(1, false));
		
		final BooleanFieldEditor showConsoleAutomatically = new BooleanFieldEditor(
			Constants.PREF_WORKBENCH_CONSOLE_AUTOACTIVATE.getLocalName(),
			"Show when Bean Builder is running",
			pnlShowConsoleAutomatically
		);
		final Composite pnlCbConsoleLogLevel = new Composite(pnlConsole, SWT.NONE);
		pnlCbConsoleLogLevel.setLayout(new GridLayout(1, false));
		final ComboFieldEditor cbConsoleLogLevel = new ComboFieldEditor(
			Constants.PREF_WORKBENCH_CONSOLE_LEVEL.getLocalName(),
			"Logging Level",
			new String[][] {
				{Level.ALL.getName(), Level.ALL.getName()},
				{Level.FINEST.getName(), Level.FINEST.getName()},
				{Level.FINER.getName(), Level.FINER.getName()},
				{Level.FINE.getName(), Level.FINE.getName()},
				{Level.CONFIG.getName(), Level.CONFIG.getName()},
				{Level.INFO.getName(), Level.INFO.getName()},
				{Level.WARNING.getName(), Level.WARNING.getName()},
				{Level.SEVERE.getName(), Level.SEVERE.getName()},
				{Level.OFF.getName(), Level.OFF.getName()}
			}, 
			pnlCbConsoleLogLevel	
		);
		addField(showConsoleAutomatically);
		addField(cbConsoleLogLevel);

		return container;
	}
	
	/**
	 * Initialize the preference page
	 * @param workbench 
	 */
	public void init(IWorkbench workbench) {
		// Initialize the preference page
		final IPreferenceStore store = de.ars.daojones.eclipse.jdt.ui.Activator.getDefault().getPreferenceStore();
		//PreferencesInitializer.initialize(store);
		setPreferenceStore(store);
	}

}
