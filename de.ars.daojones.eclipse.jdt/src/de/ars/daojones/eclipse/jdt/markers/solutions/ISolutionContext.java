package de.ars.daojones.eclipse.jdt.markers.solutions;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * A simple context for a solution.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface ISolutionContext {

	/**
	 * Returns the java project.
	 * @return the java project
	 */
	public IJavaProject getJavaProject();

	/**
	 * Returns the AST root.
	 * @return the AST root
	 */
	public CompilationUnit getASTRoot();

	/**
	 * Returns the compilation unit.
	 * @return the compilation unit
	 */
	public ICompilationUnit getCompilationUnit();
	
	/**
	 * Returns the node that is marked by the marker. If no special node
	 * is marked, {@link #getSelectedNode()} returns null; 
	 * @return the node that is marked by the marker
	 */
	public ASTNode getSelectedNode();
	
}