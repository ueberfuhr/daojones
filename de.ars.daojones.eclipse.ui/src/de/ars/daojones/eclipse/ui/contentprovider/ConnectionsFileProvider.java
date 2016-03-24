package de.ars.daojones.eclipse.ui.contentprovider;

import java.util.Collection;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.model.WorkbenchContentProvider;

import de.ars.daojones.connections.model.IImportDeclaration;
import de.ars.daojones.eclipse.connections.ConnectionsFileDescriber;
import de.ars.daojones.eclipse.ui.actions.ConnectionFileEditorContext;
import de.ars.equinox.utilities.AdaptableUtil;
import de.ars.equinox.utilities.PathUtil;

/**
 * This provider shows the connection files within a workspace.
 * It excludes "empty" directories and output folder.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ConnectionsFileProvider extends de.ars.eclipse.ui.util.FilteredContentProvider implements ITreeContentProvider {

	private final ConnectionFileEditorContext context;
	
	/**
	 * Creates an instance.
	 * @param context the context
	 */
	public ConnectionsFileProvider(final ConnectionFileEditorContext context) {
		super(new WorkbenchContentProvider());
		this.context = context;
	}
	/**
	 * Returns the context.
	 * @return the context
	 */
	protected ConnectionFileEditorContext getContext() {
		return this.context;
	}

	private boolean contains(Collection<IImportDeclaration> decs, IPath file) {
		for(IImportDeclaration dec : decs) {
			if(dec.getFile().equals(file.toOSString()) || dec.getFile().equals(file.toPortableString())) return true;
		}
		return false;
	}
	
	private static boolean checkContentDescription(IContentDescription description) {
		if(null == description) return false;
		final IContentType type = description.getContentType();
		if(null == type) return false;
		return ConnectionsFileDescriber.CONTENT_TYPE.equals(type.getId());
	}
	
	/**
	 * @see de.ars.eclipse.ui.util.FilteredContentProvider#filter(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected boolean filter(Object childElement, Object parent) {
		final IContainer container = (IContainer)AdaptableUtil.getAdapter(parent      , IContainer.class);
		final IFolder folder =       (IFolder)   AdaptableUtil.getAdapter(childElement, IFolder.class); 
		final IFile file =           (IFile)     AdaptableUtil.getAdapter(childElement, IFile.class); 

		if(null == container) return false;
		// extensions
		for(FilteredContentProvider provider : ConnectionsFileProviderExtensionsManager.getInstance().getProviders()) {
			if(!provider.filter(childElement, parent)) return false;
		}
		// check own
		if(null != file) {
			final IPath path = file.getFullPath();
			final IFile conFile = getContext().getConfigurationFile();
			try {
				return
					// Do not show the same file as open!
					!path.equals(conFile.getFullPath())
					&&
					// Do not show already imported files
					!contains(
						getContext().getConfiguration().getImportDeclarations(), 
						PathUtil.makeRelative(conFile.getFullPath(), path)
					)
//				// Do only show XML files
//				&& ((IFile)childElement).getName().toUpperCase().endsWith(".xml".toUpperCase())
					// check for content type
					&& checkContentDescription(((IFile)childElement).getContentDescription())
				;
			} catch (CoreException e) {
				return false;
			}
		} else if(null != folder) {
			// Blend out "empty" folders
			return hasChildren(childElement);
		} else return false;
	}

}
