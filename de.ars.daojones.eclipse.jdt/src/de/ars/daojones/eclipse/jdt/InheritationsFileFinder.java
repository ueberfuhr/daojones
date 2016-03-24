package de.ars.daojones.eclipse.jdt;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaProject;

import de.ars.daojones.Constants;
import de.ars.equinox.utilities.rcp.PreferencesUtil;

/**
 * A class finding the inheritations file of an {@link IJavaProject}.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class InheritationsFileFinder {

	/**
	 * The name of the property containing the name of the inheritations file.
	 */
	public static final String PROPERTY_INHERITATIONFILE = "inheritationsFile";
	/**
	 * The qualified name of the property containing the name of the inheritations file.
	 */
	public static final QualifiedName QNAME_INHERITATIONSFILE = new QualifiedName(Constants.NAMESPACE, PROPERTY_INHERITATIONFILE);
	private final IFolder container;
	private final IJavaProject project;

	/**
	 * Creates a new instance.
	 * @param container
	 * @param project
	 */
	public InheritationsFileFinder(IFolder container, IJavaProject project) {
		super();
		this.container = container;
		this.project = project;
	}
	
	/**
	 * Returns the file that is used to store inheritation information.
	 * The file name follows the rule
	 * <code>
	 * daojones&lt;project name&gt;&lt;timestamp&gt;.inheritations
	 * </code>
	 * This file may not exist, you have to check this by calling {@link IFile#exists()}.
	 * @return the inheritations file
	 * @throws CoreException 
	 */
	public IFile getInheritationsFile() throws CoreException {
		final String result = getInheritationsFilename();
		return container.getFile(result);
	}
	
	/**
	 * Returns the name of the inheritations file.
	 * @return the name of the inheritations file
	 * @throws CoreException 
	 */
	public String getInheritationsFilename() throws CoreException {
		return PreferencesUtil.getPreference(
			this.project.getProject(), 
			QNAME_INHERITATIONSFILE, 
			"daojones" + this.project.getProject().getName() + new Long(System.currentTimeMillis()).toString() + ".inheritations",
			true // store, if first read
		);
	}
	
}
