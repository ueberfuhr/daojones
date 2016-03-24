package de.ars.daojones.eclipse.jdt.ui.editor.connection;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.Viewer;

import de.ars.daojones.eclipse.ui.contentprovider.FilteredContentProvider;
import de.ars.equinox.utilities.AdaptableUtil;

/**
 * A JDT provider that excludes Java output folder.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ConnectionsFileProvider implements FilteredContentProvider {

	/**
	 * @see de.ars.daojones.eclipse.ui.contentprovider.FilteredContentProvider#filter(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public boolean filter(Object childElement, Object parent) {
		final IFolder folder = (IFolder) AdaptableUtil.getAdapter(childElement,
				IFolder.class);
		if (null != folder) {
			return select(null, parent, folder);
		}
		return true;
	}

	/**
	 * Returns the result of this filter, when applied to the given element.
	 * THIS METHOD IS COPIED FROM THE CLASS
	 * org.eclipse.jdt.internal.ui.filters.OutputFolderFilter
	 * (org.eclipse.jdt.ui) !!!
	 * 
	 * @param viewer
	 *            the viewer
	 * @param parent
	 *            the parent
	 * @param element
	 *            the element to test
	 * @return <code>true</code> if element should be included
	 * @since 3.0
	 */
	private boolean select(Viewer viewer, Object parent, Object element) {
		if (element instanceof IFolder) {
			IFolder folder = (IFolder) element;
			IProject proj = folder.getProject();
			try {
				if (!proj.hasNature(JavaCore.NATURE_ID))
					return true;

				IJavaProject jProject = JavaCore.create(folder.getProject());
				if (jProject == null || !jProject.exists())
					return true;

				// Check default output location
				IPath defaultOutputLocation = jProject.getOutputLocation();
				IPath folderPath = folder.getFullPath();
				if (defaultOutputLocation != null
						&& defaultOutputLocation.equals(folderPath))
					return false;

				// Check output location for each class path entry
				IClasspathEntry[] cpEntries = jProject.getRawClasspath();
				for (int i = 0, length = cpEntries.length; i < length; i++) {
					IPath outputLocation = cpEntries[i].getOutputLocation();
					if (outputLocation != null
							&& outputLocation.equals(folderPath))
						return false;
				}
			} catch (CoreException ex) {
				return true;
			}
		}
		return true;
	}
}
