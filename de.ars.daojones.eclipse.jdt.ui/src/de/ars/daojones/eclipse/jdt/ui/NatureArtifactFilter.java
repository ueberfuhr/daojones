package de.ars.daojones.eclipse.jdt.ui;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.ars.daojones.eclipse.jdt.Artifacts;

import static de.ars.daojones.eclipse.jdt.Artifacts .*;

/**
 * A filter hiding the DaoJones artifacts.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class NatureArtifactFilter extends ViewerFilter {
	
	private static final Logger logger = Logger.getLogger(NatureArtifactFilter.class.getName());
	private Collection<String> namesToFilter;
	
	private Collection<String> getNamesToFilter() {
		if(null == namesToFilter) {
			namesToFilter = new HashSet<String>();
			try {
				namesToFilter.add(getDaoJonesLibrary().getName());
				namesToFilter.add(getDaoJonesLibrary().getName() + "/bin");
//				namesToFilter.add(getDaoJonesSDKLibrary().getName());
//				namesToFilter.add(getDaoJonesSDKLibrary().getName() + "/bin");
				namesToFilter.add(getDaoJonesLibrary().getAbsolutePath().replaceAll("\\\\", "/"));
				namesToFilter.add(getDaoJonesLibrary().getAbsolutePath().replaceAll("\\\\", "/") + "/bin");
//				namesToFilter.add(getDaoJonesSDKLibrary().getAbsolutePath().replaceAll("\\\\", "/"));
//				namesToFilter.add(getDaoJonesSDKLibrary().getAbsolutePath().replaceAll("\\\\", "/") + "/bin");
			} catch (IOException e) {
				Activator.log(IStatus.ERROR, "Error fetching daojones library!", e);
			}
		};
		return namesToFilter;
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return 
			// element is not a java element ...
			!(element instanceof IJavaElement) 
			||
			// ... or element is not to be filtered ...
			!getNamesToFilter().contains(((IJavaElement)element).getElementName())
			&&
			!getNamesToFilter().contains(((IJavaElement)element).getPath().toPortableString())
			&&
			// ... and element is not destination source folder
			!isDestinationSourceFolder((IJavaElement)element)
		;
	}
	
	private boolean isDestinationSourceFolder(IJavaElement element) {
		try {
			return 
				null != element.getCorrespondingResource()
				&&
				element.getCorrespondingResource().getFullPath().equals(Artifacts.getDestinationSourceFolder(element.getJavaProject().getProject()).getFullPath());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Unable to identify a java element as DaoJones destination source folder!", e);
			return false;
		}
	}

	
	
}
