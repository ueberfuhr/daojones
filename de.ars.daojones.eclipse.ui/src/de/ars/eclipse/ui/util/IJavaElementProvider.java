package de.ars.eclipse.ui.util;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Shell;

import de.ars.equinox.utilities.bridge.IBridge;

/**
 * An object providing information and operations
 * concerning java elements.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IJavaElementProvider extends IBridge {

	/**
	 * Opens a dialog to select classes that can be used as for classes.
	 * @param parent the parent shell
	 * @param project the project
	 * @return the collection of class names that were selected
	 */
	public abstract Collection<String> getSelectionOfClasses(Shell parent, IProject project);
	/**
	 * Returns an {@link ILabelProvider} for showing the classes.
	 * @param project the current project
	 * @return the {@link ILabelProvider}
	 */
	public abstract ILabelProvider getLabelProvider(IProject project);
	
}
