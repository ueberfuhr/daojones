package de.ars.daojones.eclipse.jdt.beans;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;

/**
 * A transfer object holding necessary data for problem
 * detecting {@link ASTVisitor}s.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 *
 */
public interface ProblemDetectorEnvironment {

	/**
	 * Returns the source of the {@link AST}.
	 * @return the source of the {@link AST}
	 */
	public IResource getSource();
	
}
