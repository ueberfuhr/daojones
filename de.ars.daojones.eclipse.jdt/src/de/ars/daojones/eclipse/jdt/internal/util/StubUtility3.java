package de.ars.daojones.eclipse.jdt.internal.util;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import de.ars.daojones.eclipse.jdt.AbstractProjectNature;
import de.ars.daojones.eclipse.jdt.Activator;

import static de.ars.daojones.eclipse.jdt.LoggerConstants.*;

/**
 * A class containing static helper methods concerning finding resources within
 * the project or parsing Compilation units to AST's.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class StubUtility3 {

	/**
	 * Creates the AST root from a compilation unit.
	 * @param unit
	 * @param monitor
	 * @return the AST root
	 */
	public static CompilationUnit getRoot(ICompilationUnit unit, IProgressMonitor monitor) {
		if(null == unit) return null;
		final long date1 = System.currentTimeMillis();
		final ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setProject(unit.getJavaProject());
		parser.setSource(unit);
		parser.setResolveBindings(true);
		final long date2 = System.currentTimeMillis();
		getLogger().log(FINE, "Parsed AST in " + (date2-date1) + " ms.");
		return (CompilationUnit)parser.createAST(null);
	}
	
	/**
	 * Searches for a compilation unit within the java project based on
	 * the file resource containing the source or binary code.
	 * @param resource the resource
	 * @return the compilation unit or null, if the compilation unit could not be found
	 * @throws CoreException if the resource is not part of a java project
	 */
	public static ICompilationUnit findCompilationUnit(IResource resource) throws CoreException {
		final IJavaProject javaProject = AbstractProjectNature.getJavaProject(resource.getProject());
		if(null == javaProject) 
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "The marker does not mark an element of a java project!"));
		IPath pathToSearchFor = resource.getProjectRelativePath();
		IJavaElement element = null;
		while(!pathToSearchFor.isEmpty() && null == (element = javaProject.findElement(pathToSearchFor))) pathToSearchFor = pathToSearchFor.removeFirstSegments(1);
		return (null == element || !(element instanceof ICompilationUnit)) ? null : (ICompilationUnit)element;
	}
	
	/**
	 * Parses a resource as a java compilation unit.
	 * @param resource
	 * @param monitor
	 * @return the AST root
	 * @throws CoreException if the resource is not part of a java project
	 */
	public static ASTNode getRoot(IResource resource, IProgressMonitor monitor) throws CoreException {
		return getRoot(findCompilationUnit(resource), monitor);
	}
	
}
