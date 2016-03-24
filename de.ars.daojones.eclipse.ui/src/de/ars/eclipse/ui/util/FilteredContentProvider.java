package de.ars.eclipse.ui.util;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * A content provider that filters out content.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class FilteredContentProvider implements ITreeContentProvider {

	private final ITreeContentProvider original;

	/**
	 * Creates an instance.
	 * @param original the source
	 */
	public FilteredContentProvider(ITreeContentProvider original) {
		super();
		this.original = original;
	}
	
	/**
	 * Filters out a child element.
	 * @param childElement the child element
	 * @param parent the parent element
	 * @return true, if the element can be shown
	 */
	protected abstract boolean filter(Object childElement, Object parent);

	private Object[] filter(Object[] oChilds, Object parent) {
		if(null == oChilds || oChilds.length<1) return oChilds;
		final List<Object> childs = new LinkedList<Object>();
		for(Object o : oChilds) {
			if(filter(o, null != parent ? parent : getParent(o))) childs.add(o);
		};
		return childs.toArray(new Object[childs.size()]);
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	// TODO Java6-Migration
	// @Override
	public Object[] getChildren(Object parentElement) {
		return filter(original.getChildren(parentElement), parentElement);
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	// TODO Java6-Migration
	// @Override
	public Object getParent(Object element) {
		return original.getParent(element);
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	// TODO Java6-Migration
	// @Override
	public boolean hasChildren(Object element) {
		Object[] childs;
		return original.hasChildren(element) && null != (childs = getChildren(element)) && childs.length>0;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	// TODO Java6-Migration
	// @Override
	public Object[] getElements(Object inputElement) {
		return filter(original.getElements(inputElement), null);
	}
	
	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	// TODO Java6-Migration
	// @Override
	public void dispose() {
		original.dispose();
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	// TODO Java6-Migration
	// @Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		original.inputChanged(viewer, oldInput, newInput);
	}

}
