package de.ars.daojones.eclipse.jdt.markers.solutions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.text.edits.TextEdit;

import static de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities.*;
import static de.ars.daojones.eclipse.jdt.markers.solutions.SolutionUtilities.*;

/**
 * A solution that sets the superclass or an interface
 * of the selected class.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class SetTypeHierarchySolution implements ISolution {

	/**
	 * Returns the type that the selected type should subclass.
	 * @return the type that the selected type should subclass
	 */
	protected abstract Class<?> getSuperType();
	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.solutions.ISolution#getTitle()
	 */
	@Override
	public String getTitle() {
		final Class<?> c = getSuperType();
		if(c.isInterface()) {
			return "Let the class implement the interface" + c.getName() + ".";
		} else {
			return "Change the supertype of the class to " + c.getName() + ".";
		}
	}
	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.solutions.ISolution#getDescription()
	 */
	@Override
	public String getDescription() {
		return null;
	}
	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.solutions.ISolution#solve(de.ars.daojones.eclipse.jdt.markers.solutions.ISolutionExecutionEnvironment)
	 */
	@Override
	public void solve(ISolutionExecutionEnvironment env) throws CoreException {
		final TypeDeclaration type = findAncestorWithType(env.getSelectedNode(), TypeDeclaration.class);
		if(null != type) {
			final AST ast = env.getASTRoot().getAST();
			final ASTRewrite rewrite = ASTRewrite.create(ast);
			final Class<?> c = getSuperType();
			final boolean imported = tryToImport(env, rewrite, c.getName());
			final Type newSuperType = newQualifiedType(ast, imported ? c.getSimpleName() : c.getName());
			if(c.isInterface()) {
				rewrite.getListRewrite(type, TypeDeclaration.SUPER_INTERFACE_TYPES_PROPERTY).insertLast(newSuperType, null);
			} else {
				rewrite.set(type, TypeDeclaration.SUPERCLASS_TYPE_PROPERTY,	newSuperType, null);
			}
			final TextEdit edits = env.rewrite(rewrite);
			env.apply(edits, null);
		}
	}
	
}
