package de.ars.daojones.eclipse.jdt;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.ars.daojones.eclipse.jdt.preferences.Constants;
import de.ars.equinox.utilities.rcp.PreferencesUtil;

/**
 * A class holding references to general artifacts like
 * library files.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class Artifacts {
	
	/**
	 * A variable containing the path to the DaoJones runtime library.
	 */
	public static final String VARIABLE_RUNTIME = "DAOJONES_RUNTIME";
//	/**
//	 * A variable containing the path to the DaoJones development kit library.
//	 */
//	public static final String VARIABLE_SDK = "DAOJONES_SDK";
	/**
	 * The name of the bundle containing the DaoJones runtime classes.
	 */
	public static final String BUNDLENAME_RUNTIME = "de.ars.daojones.runtime";
//	/**
//	 * The name of the bundle containing the DaoJones development kit.
//	 */
//	public static final String BUNDLENAME_SDK = "de.ars.daojones.sdk";
	/**
	 * Returns the bundle containing the DaoJones runtime classes.
	 * @return the bundle containing the DaoJones runtime classes
	 * @throws IOException
	 */
	public static File getDaoJonesLibrary() throws IOException {
		return FileLocator.getBundleFile(Platform.getBundle(BUNDLENAME_RUNTIME));
	}
//	/**
//	 * Returns the bundle containing the DaoJones development kit.
//	 * @return the bundle containing the DaoJones development kit
//	 * @throws IOException
//	 */
//	public static File getDaoJonesSDKLibrary() throws IOException {
//		return FileLocator.getBundleFile(Platform.getBundle(BUNDLENAME_SDK));
//	}

	/**
	 * Returns the name of the source folder where the DaoJones bean are generated into.
	 * @param project the project
	 * @return the name of the source folder where the DaoJones bean are generated into
	 * @throws CoreException 
	 */
	public static String getDestinationSourceFolderName(final IProject project) throws CoreException {
		return PreferencesUtil.getPreference(
			project, 
			Constants.PREF_DESTINATION_SOURCEFOLDER, 
			Activator.getDefault().getPluginPreferences().getString(Constants.PREF_DESTINATION_SOURCEFOLDER.getLocalName()),
			false
		);
	}
	/**
	 * Sets the name of the source folder where the DaoJones bean are generated into.
	 * @param project the project
	 * @param value the name of the source folder where the DaoJones bean are generated into
	 * @throws CoreException
	 */
	public static void setDestinationSourceFolderName(final IProject project, final String value) throws CoreException {
		final IFolder destFolder = getDestinationSourceFolder(project);
		// TODO final ProjectNature nature = (ProjectNature)project.getNature(ProjectNature.NATURE_ID);
		final Job renameJob = new Job("Rename DaoJones destination source folder") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					ProjectNature.toggleNature(project);
					if(destFolder.exists()) destFolder.move(destFolder.getFullPath().removeLastSegments(1).append(value), true, monitor);
					PreferencesUtil.setPreference(
						project, 
						Constants.PREF_DESTINATION_SOURCEFOLDER, 
						value
					);
					ProjectNature.toggleNature(project);
					return Status.OK_STATUS;
				} catch (CoreException e) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unable to rename DaoJones destination source folder!", e);
				}
			}
		};
		renameJob.schedule();
	}
	/**
	 * Returns the path to the source folder where DaoJones beans are stored into.
	 * This does not ensure that the folder exists.
	 * @param project the project
	 * @return the path to the source folder where DaoJones beans are stored into
	 * @throws CoreException 
	 */
	public static IFolder getDestinationSourceFolder(final IProject project) throws CoreException {
		if(null == project) return null;
		return project.getFolder(getDestinationSourceFolderName(project));
	}

}
