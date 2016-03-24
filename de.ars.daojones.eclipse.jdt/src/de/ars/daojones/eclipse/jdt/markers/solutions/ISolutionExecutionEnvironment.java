package de.ars.daojones.eclipse.jdt.markers.solutions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;

/**
 * An environment to handle a solution.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface ISolutionExecutionEnvironment extends ISolutionContext {

	/**
	 * Applies a text edit to 
	 * @param edit
	 * @param monitor
	 * @return the {@link UndoEdit} object
	 * @throws CoreException
	 */
	public UndoEdit apply(TextEdit edit, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * @see ASTRewrite#rewriteAST()
	 * @see ASTRewrite#rewriteAST(org.eclipse.jface.text.IDocument, java.util.Map)
	 * @param rewrite
	 * @return the {@link TextEdit} object
	 * @throws JavaModelException
	 * @throws IllegalArgumentException
	 */
	public TextEdit rewrite(ASTRewrite rewrite) throws JavaModelException, IllegalArgumentException;

}