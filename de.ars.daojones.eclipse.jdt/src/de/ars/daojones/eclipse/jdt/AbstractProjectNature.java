package de.ars.daojones.eclipse.jdt;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * A basic super class for {@link IProjectNature} containing
 * utility methods.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class AbstractProjectNature implements IProjectNature {

	/**
	 * Returns the {@link IJavaProject}.
	 * @return the {@link IJavaProject}
	 */
	protected IJavaProject getJavaProject() {
		return getJavaProject(getProject());
	}
	
	/**
	 * Returns the {@link IJavaProject} based on a given project.
	 * @param project the project
	 * @return the {@link IJavaProject}
	 */
	public static IJavaProject getJavaProject(IProject project) {
		if(null == project) return null;
		try {
			return (IJavaProject)project.getNature(JavaCore.NATURE_ID);
		} catch (CoreException e) {
			Activator.log(IStatus.ERROR, "Error reading java nature!", e);
		}
		return null;
	}
	
	private IProject project;

	/**
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	@Override
	public IProject getProject() {
		return this.project;
	}

	/**
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

}
