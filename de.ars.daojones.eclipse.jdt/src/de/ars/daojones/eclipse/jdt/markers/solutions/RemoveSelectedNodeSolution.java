package de.ars.daojones.eclipse.jdt.markers.solutions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.text.edits.TextEdit;

import de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities;

/**
 * Adds an annotation to the selected node (method).
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class RemoveSelectedNodeSolution implements ISolution {

	private final String TITLE;
	private final String DESCRIPTION;
	
	/**
	 * Creates a solution.
	 * @param title
	 * @param description
	 */
	public RemoveSelectedNodeSolution(String title, String description) {
		super();
		TITLE = title;
		DESCRIPTION = description;
	}

	/**
	 * Returns the type of the selected node that should be removed.
	 * If the method returns null, the selected node is removed, otherwise
	 * the parent with the given type is removed.
	 * @return the type of the node to be removed or null, if the current selected node should be removed.
	 */
	protected Class<? extends ASTNode> getNodeType() {
		return null;
	}
	
	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.solutions.ISolution#getDescription()
	 */
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.solutions.ISolution#getTitle()
	 */
	@Override
	public String getTitle() {
		return TITLE;
	}

	/**
	 * Returns the node that should be removed from its parent.
	 * @param env the environment
	 * @return the node that should be removed from its parent
	 */
	protected ASTNode getNode(ISolutionExecutionEnvironment env) {
		ASTNode result = env.getSelectedNode();
		if(null != getNodeType()) {
			result = ASTVisitorUtilities.findAncestorWithType(result, getNodeType());
		}
		return result;
	}
	
	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.solutions.ISolution#solve(de.ars.daojones.eclipse.jdt.markers.solutions.ISolutionExecutionEnvironment)
	 */
	@Override
	public void solve(ISolutionExecutionEnvironment env) throws CoreException {
		final AST ast = env.getASTRoot().getAST();
		final ASTRewrite rewrite = ASTRewrite.create(ast);
		final ASTNode node = getNode(env);
		if(null != node) {
			rewrite.remove(node, null);
			// computation of the text edits
			final TextEdit edits = env.rewrite(rewrite);
			env.apply(edits, null);
		}
	}

}
