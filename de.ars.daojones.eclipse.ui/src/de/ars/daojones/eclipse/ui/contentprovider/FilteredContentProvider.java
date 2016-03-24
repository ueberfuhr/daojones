package de.ars.daojones.eclipse.ui.contentprovider;

/**
 * An interface used to extend the {@link ConnectionsFileProvider}.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface FilteredContentProvider {

	/**
	 * Decides whether to show the childElement or not.
	 * @param childElement the childElement
	 * @param parent the parent
	 * @return true, if the child should be shown
	 */
	public boolean filter(Object childElement, Object parent);
	
}
