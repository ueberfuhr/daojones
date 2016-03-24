package de.ars.daojones.eclipse.jdt.internal.libraries;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import de.ars.daojones.eclipse.jdt.Activator;

/**
 * The initializer for DaoJones libraries.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ClasspathContainers extends ClasspathContainerInitializer {

	/**
	 * The ID of the DaoJones classpath container.
	 */
	public static final String LIBRARY_CONTAINER_ID = Activator.PLUGIN_ID + ".LIBRARY";
	
	private static final Map<IPath, IClasspathContainer> CONTAINERS = new HashMap<IPath, IClasspathContainer>();
	
	private static void register(IClasspathContainer container) {
		CONTAINERS.put(container.getPath(), container);
	}
	
	static {
		register(new RuntimeLibrary());
	}
	/**
	 * @see org.eclipse.jdt.core.ClasspathContainerInitializer#initialize(org.eclipse.core.runtime.IPath, org.eclipse.jdt.core.IJavaProject)
	 */
	@Override
	public void initialize(final IPath containerPath, final IJavaProject project)
			throws CoreException {
		if(LIBRARY_CONTAINER_ID.equals(containerPath.segment(0))) {
			final IClasspathContainer container = CONTAINERS.get(containerPath);
			//super.requestClasspathContainerUpdate(containerPath, project, container);
			new WorkspaceJob("Adding DaoJones library to classpath...") {
				@Override
				public IStatus runInWorkspace(IProgressMonitor monitor)
						throws CoreException {
					JavaCore.setClasspathContainer(containerPath, new IJavaProject[]{project}, new IClasspathContainer[]{container}, null);
					return Status.OK_STATUS;
				}
				
			}.schedule();
		}
	}

}
