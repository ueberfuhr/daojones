package de.ars.daojones.eclipse.jdt.ui.editor.connection;

import static de.ars.daojones.eclipse.jdt.ui.Activator.log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;

import de.ars.daojones.eclipse.jdt.AbstractProjectNature;
import de.ars.daojones.eclipse.jdt.ProjectNature;
import de.ars.daojones.runtime.Dao;
import de.ars.eclipse.ui.util.IJavaElementProvider;

/**
 * A default implementation of {@link IJavaElementProvider}
 * using JDT tools.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class JdtJavaElementProvider implements IJavaElementProvider {

	/**
	 * @see de.ars.eclipse.ui.util.IJavaElementProvider#getSelectionOfClasses(org.eclipse.swt.widgets.Shell, org.eclipse.core.resources.IProject)
	 */
	@Override
	public Collection<String> getSelectionOfClasses(Shell parent, IProject project) {
		try {
			final IType daoType = ProjectNature.getJavaProject(project).findType(Dao.class.getName());
			final SelectionDialog dialog = JavaUI.createTypeDialog(
				parent, 
				PlatformUI.getWorkbench().getProgressService(), 
				null != daoType ? SearchEngine.createHierarchyScope(daoType) : SearchEngine.createWorkspaceScope(),
				IJavaElementSearchConstants.CONSIDER_CLASSES, 
				true
			);
			dialog.setTitle("Please select DaoJones classes");
			if (dialog.open() == Window.OK) {
				final Set<String> result = new TreeSet<String>();
				for(Object o : dialog.getResult()) {
					final IType type = (IType) o;
					result.add(type.getFullyQualifiedName());
				}
				return result;
			}
		} catch (JavaModelException e) {
			log(IStatus.ERROR, "Error trying to show selection dialog for DaoJones DAOs.", e);
		}
		return null;
	}
	/**
	 * @see de.ars.eclipse.ui.util.IJavaElementProvider#getLabelProvider(IProject)
	 */
	@Override
	public ILabelProvider getLabelProvider(final IProject project) {
		return new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_TYPE | JavaElementLabelProvider.SHOW_QUALIFIED) {
			private final IJavaProject javaProject = AbstractProjectNature.getJavaProject(project);
			private final Map<Object, IJavaElement> elements = new HashMap<Object, IJavaElement>();
			private IJavaElement findJavaElement(Object value) {
				if(null == javaProject || null == value || value instanceof IJavaElement) return null;
				synchronized (elements) {
					IJavaElement element = elements.get(value);
					if(null == element) {
						if(value instanceof IResource) {
							try {
								element = javaProject.findElement(((IResource)value).getProjectRelativePath());
							} catch (JavaModelException e) {
								log(IStatus.ERROR, "Unable to find java element from resource \"" + ((IResource)value).getProjectRelativePath() + "\"!", e);
							}
						} else {
							try {
								element = javaProject.findType(value.toString());
							} catch (JavaModelException e) {
								log(IStatus.ERROR, "Unable to find java type \"" + value.toString() + "\"!", e);
							}
						}
						if(null != element) elements.put(value, element);
					};
					return element;
				}
			}
			@Override
			public Image getImage(Object element) {
				final IJavaElement jel = findJavaElement(element);
				return null != jel ? super.getImage(jel) : null;
			}
			@Override
			public StyledString getStyledText(Object element) {
				final IJavaElement jel = findJavaElement(element);
				return null != jel ? super.getStyledText(jel) : new StyledString(null != element ? element.toString() : "");
			}
			private String extractPackageFragmentToSuffix(String text) {
				if(null == text) return null;
				int index = text.lastIndexOf('.');
				return index<1 ? text : text.substring(index+1) + " - " + text.substring(0, index);
				
			}
			@Override
			public String getText(Object element) {
				final IJavaElement jel = findJavaElement(element);
				return extractPackageFragmentToSuffix(null != jel ? super.getText(jel) : (null != element ? element.toString() : ""));
			}
		};
	}

}
