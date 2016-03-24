package de.ars.daojones.eclipse.jdt.preferences;

import org.eclipse.core.runtime.QualifiedName;

import de.ars.daojones.eclipse.DaoJonesPlugin;

/**
 * An interface holding the preference constants.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface Constants {

	/**
	 * The namespace.
	 */
	public static final String NAMESPACE = DaoJonesPlugin.PLUGIN_ID;
	/**
	 * The name of the preference where the destination folder is stored into.
	 */
	public static final QualifiedName PREF_DESTINATION_SOURCEFOLDER = new QualifiedName(Constants.NAMESPACE, "sourcefolder");
	/**
	 * The default setting for the source folder preference.
	 */
	public static final String PREF_DESTINATION_SOURCEFOLDER_DEFAULT = "daojones.generated";

}
