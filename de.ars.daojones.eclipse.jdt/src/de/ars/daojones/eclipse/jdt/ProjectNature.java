package de.ars.daojones.eclipse.jdt;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import de.ars.daojones.eclipse.jdt.beans.BeanBuilder;
import de.ars.daojones.eclipse.jdt.internal.libraries.RuntimeLibrary;
import de.ars.daojones.eclipse.resources.DaoJonesProject;
import de.ars.equinox.utilities.rcp.NatureUtil;
/**
 * The DaoJones JDT Project Nature.
 * This nature adds the following JavaProject artifacts:
 * <ul>
 *   <li>Source Folder "daojones.generated"</li>
 *   <li>Variable ClassPathEntry to fetch the DaoJones API</li>
 *   <li>Variable ClassPathEntry to fetch the DaoJones SDK API</li>
 * </ul> 
 */
public class ProjectNature extends AbstractProjectNature {

	/**
	 * The ID of the Nature.
	 */
	public static final String ID = Activator.PLUGIN_ID + ".nature";
	
	/**
	 * An array of nature IDs to toggle.
	 */
	public static final String[] NATURES_TO_TOGGLE = new String[]{
		DaoJonesProject.NATURE_ID, 
		ProjectNature.ID
	};
	
	/**
	 * Toggles the nature of the given project.
	 * @param project the project
	 * @return true, if the project was changed
	 * @throws CoreException
	 */
	public static boolean toggleNature(IProject project) throws CoreException {
		return NatureUtil.setNatureEnabled(project, !NatureUtil.isNatureEnabled(project, NATURES_TO_TOGGLE), NATURES_TO_TOGGLE);
	}

	/**
	 * Returns the classpath entry to the destination source folder.
	 * The destination source folder is the source folder where the
	 * DaoJones beans are created into.
	 * @return the classpath entry to the destination source folder
	 * @throws CoreException 
	 */
	protected IClasspathEntry getDestinationSourceFolderEntry() throws CoreException {
		if(null == getProject()) return null;
		return JavaCore.newSourceEntry(getDestinationSourceFolder().getFullPath());
	}

	/**
	 * Returns the destination source folder.
	 * @return the destination source folder
	 * @throws CoreException 
	 */
	public IFolder getDestinationSourceFolder() throws CoreException {
		return de.ars.daojones.eclipse.jdt.Artifacts.getDestinationSourceFolder(getProject());
	}

	/**
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	@Override
	public void configure() throws CoreException {
		final IJavaProject p = getJavaProject();
		if(null == p) return;
		// Generate Source Folder
		final IFolder destinationSourceFolder = getDestinationSourceFolder();
		if(!destinationSourceFolder.exists()) destinationSourceFolder.create(true, true, null);
		// Modify classpath
		final Set<IClasspathEntry> entries = new HashSet<IClasspathEntry>();
		entries.addAll(Arrays.asList(p.getRawClasspath()));
		entries.add(getDestinationSourceFolderEntry());
		entries.add(JavaCore.newContainerEntry(RuntimeLibrary.PATH));
//		entries.add(getDaoJonesSDKAPIEntry());
		p.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
		NatureUtil.setBuilderEnabled(getProject(), true, BeanBuilder.ID);
	}

	/**
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	@Override
	public void deconfigure() throws CoreException {
		final IJavaProject p = getJavaProject();
		if(null == p) return;
		NatureUtil.setBuilderEnabled(getProject(), false, BeanBuilder.ID);
		// Modify classpath
		final Set<IClasspathEntry> entries = new HashSet<IClasspathEntry>();
		entries.addAll(Arrays.asList(p.getRawClasspath()));
		entries.remove(getDestinationSourceFolderEntry());
		entries.remove(JavaCore.newContainerEntry(RuntimeLibrary.PATH));
//		entries.remove(getDaoJonesSDKAPIEntry());
		p.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
		// Delete Source Folder
		final IFolder destinationSourceFolder = getDestinationSourceFolder();
		if(destinationSourceFolder.exists()) destinationSourceFolder.delete(true, null);
	}

}
