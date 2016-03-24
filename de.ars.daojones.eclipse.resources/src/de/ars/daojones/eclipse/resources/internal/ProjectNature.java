package de.ars.daojones.eclipse.resources.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import de.ars.daojones.eclipse.resources.DaoJonesProject;

/**
 * The {@link IProjectNature} indicating that DaoJones is activated for a
 * project.
 * Note: At the moment, this nature is activated by the DaoJones JDT Nature,
 * because there is no special need for a nature. This Nature is only used for
 * sharing NatureImages in the UI. 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ProjectNature implements IProjectNature {

	/**
	 * The id of this nature.
	 */
	public static final String ID = DaoJonesProject.NATURE_ID;
	private IProject project;

	/**
	 * @see IProjectNature#configure()
	 */
//	@Override
	public void configure() throws CoreException {}
	/**
	 * @see IProjectNature#deconfigure()
	 */
//	@Override
	public void deconfigure() throws CoreException {}
	/**
	 * @see IProjectNature#getProject()
	 */
//	@Override
	public IProject getProject() {
		return this.project;
	}
	/**
	 * @see IProjectNature#setProject(IProject)
	 */
//	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

}
