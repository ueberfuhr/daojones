package de.ars.daojones.eclipse.jdt.ui.preferences;

import java.util.logging.Level;

import org.eclipse.core.runtime.QualifiedName;

import de.ars.daojones.eclipse.jdt.beans.BeanBuilder;
import de.ars.daojones.eclipse.jdt.ui.Activator;

/**
 * An interface holding preference names.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface Constants {

	/**
	 * The namespace for the preferences.
	 */
	public static final String NAMESPACE = Activator.class.getPackage().getName();
	/**
	 * The preference for activating the console automatically when the {@link BeanBuilder} is running.
	 */
	public static final QualifiedName PREF_WORKBENCH_CONSOLE_AUTOACTIVATE
		= new QualifiedName(NAMESPACE, "autoActivateConsole");
	/**
	 * Default value for PREF_WORKBENCH_CONSOLE_AUTOACTIVATE.
	 */
	public static final boolean PREF_WORKBENCH_CONSOLE_AUTOACTIVATE_DEFAULT
		= true;
	/**
	 * The preference for the console's log level.
	 */
	public static final QualifiedName PREF_WORKBENCH_CONSOLE_LEVEL
		= new QualifiedName(NAMESPACE, "consoleLevel");
	/**
	 * Default value for PREF_WORKBENCH_CONSOLE_LEVEL.
	 */
	public static final String PREF_WORKBENCH_CONSOLE_LEVEL_DEFAULT
		= Level.FINE.getName();
	

}
