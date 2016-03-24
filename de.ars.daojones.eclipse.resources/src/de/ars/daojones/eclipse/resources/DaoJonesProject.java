package de.ars.daojones.eclipse.resources;

import org.eclipse.core.resources.IProject;

import de.ars.daojones.eclipse.DaoJonesPlugin;


/**
 * A class providing methods to access project informations concerning the DaoJones nature.
 * @author Ralf Zahn
 */
public class DaoJonesProject {

	/**
	 * The id of the project nature.
	 */
	public static final String NATURE_ID = DaoJonesPlugin.PLUGIN_ID + "resources.nature";
	
	private DaoJonesProject() {
		super();
	}
	
	/**
	 * Returns the project information concerning DaoJones.
	 * @param project the project
	 * @return the project information concerning DaoJones
	 */
	public static DaoJonesProject get(IProject project) {
		return new DaoJonesProject();
	}

}
