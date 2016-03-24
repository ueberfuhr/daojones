package de.ars.daojones.eclipse.jdt.markers.solutions;

import static de.ars.daojones.eclipse.jdt.markers.solutions.SolutionUtilities.tryToImport;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.text.edits.TextEdit;

import de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities;

/**
 * Adds an annotation to the selected node (method).
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class AddAnnotationSolution implements ISolution {
	
	/**
	 * Returns the node where to add the annotation. By default, the selected
	 * node is annotated.
	 * @param env
	 * @return the node where to add the annotation
	 */
	protected BodyDeclaration getNode(ISolutionExecutionEnvironment env) {
		return ASTVisitorUtilities.findAncestorWithType(env.getSelectedNode(), getAnnotatedElementType());
	}
	
	/**
	 * Returns the type of the element that should be annotated.
	 * This is used for finding the node in the hierarchy.
	 * @return the type of the element that should be annotated
	 */
	protected Class<? extends BodyDeclaration> getAnnotatedElementType() {
		return BodyDeclaration.class;
	}
	
	/**
	 * Returns the fully qualified classname of the annotation.
	 * @return the fully qualified classname of the annotation 
	 */
	protected abstract String getAnnotation();

	/**
	 * Returns a map containing key-value-pairs for members.
	 * If null or an empty map is returned, the annotation gets
	 * a {@link NormalAnnotation}.
	 * @param ast Use the AST to create new {@link Expression}s
	 * @return the map containing key-value-pairs for members
	 */
	protected abstract Map<String, Expression> getAnnotationMembers(AST ast);
	
	@SuppressWarnings("unchecked")
	private Annotation createAnnotation(AST ast) {
		final Map<String, Expression> annotationMembers = getAnnotationMembers(ast);
		if(null == annotationMembers || annotationMembers.isEmpty()) {
			return ast.newMarkerAnnotation();
		} else if(annotationMembers.size() == 1/* && annotationMembers.containsKey("value")*/) {
			final SingleMemberAnnotation annotation = ast.newSingleMemberAnnotation();
			annotation.setValue(annotationMembers.values().iterator().next());
			return annotation;
		} else {
			final NormalAnnotation annotation = ast.newNormalAnnotation();
			for(Map.Entry<String, Expression> entry : annotationMembers.entrySet()) {
				final MemberValuePair pair = ast.newMemberValuePair();
				pair.setName(ast.newSimpleName(entry.getKey()));
				pair.setValue(entry.getValue());
				annotation.values().add(pair);
			}
			return annotation;
		}
	}

	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.solutions.ISolution#solve(de.ars.daojones.eclipse.jdt.markers.solutions.ISolutionExecutionEnvironment)
	 */
	@Override
	public void solve(ISolutionExecutionEnvironment env) throws CoreException {
		final AST ast = env.getASTRoot().getAST();
		final ASTRewrite rewrite = ASTRewrite.create(ast);
		final String annotationClass = getAnnotation();
		// Add annotation
		final Name annotationName = tryToImport(env, rewrite, ast.newName(annotationClass.split("\\.")));
		final BodyDeclaration decl = getNode(env);
		if(null != decl) {
			final Annotation annotation = createAnnotation(ast);
			annotation.setTypeName(annotationName);
			rewrite.getListRewrite(decl, decl.getModifiersProperty()).insertAt(annotation, 0, null);
			// computation of the text edits
			final TextEdit edits = env.rewrite(rewrite);
			env.apply(edits, null);
		}
	}

}
