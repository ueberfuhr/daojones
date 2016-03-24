package de.ars.eclipse.ui.util;

import java.util.Collection;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

/**
 * A default implementation of {@link IJavaElementProvider}
 * that shows a simple input dialog.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class DefaultJavaElementProvider implements IJavaElementProvider {

	/**
	 * @see de.ars.eclipse.ui.util.IJavaElementProvider#getSelectionOfClasses(org.eclipse.swt.widgets.Shell, org.eclipse.core.resources.IProject)
	 */
	// TODO Java6-Migration
	// @Override
	public Collection<String> getSelectionOfClasses(Shell parent, IProject project) {
		final InputDialog dialog = new InputDialog(parent, "DaoJones Dao class", "Please enter name of DaoJones DAO class.", "", null);
		if(dialog.open() == Window.OK) {
			final Collection<String> result = new TreeSet<String>();
			result.add(dialog.getValue().trim());
			return result;
		};
		return null;
	}
	/**
	 * @see de.ars.eclipse.ui.util.IJavaElementProvider#getLabelProvider(IProject)
	 */
	// TODO Java6-Migration
	// @Override
	public ILabelProvider getLabelProvider(IProject project) {
		return new LabelProvider();
	}

}
