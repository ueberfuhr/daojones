package de.ars.daojones.eclipse.jdt.markers.solutions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.text.edits.TextEdit;

import de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities;

/**
 * Removes an annotation from the selected node (method).
 * If the selected node or its parent is an annotation, the annotation is removed,
 * otherwise the annotations
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class RemoveAnnotationsSolution implements ISolution {

	/**
	 * Returns a list of all annotations that should be removed.
	 * @return the list of all annotations that should be removed
	 */
	protected abstract String[] getAnnotationTypes();
	
	/**
	 * Returns the type of the element whose annotations should be removed.
	 * Default is {@link BodyDeclaration}.
	 * @return the type of the element whose annotations should be removed
	 */
	protected Class<? extends ASTNode> getAnnotatedElementType() {
		return BodyDeclaration.class;
	}
	
	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.solutions.ISolution#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Removes all annotations of the listed types from the selected element.";
	}

	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.solutions.ISolution#getTitle()
	 */
	@Override
	public String getTitle() {
		final StringBuffer sb = new StringBuffer();
		final String[] ann = getAnnotationTypes();
		sb.append("Remove annotation");
		if(ann.length>1) sb.append("s of type");
		for(int i=0; i<ann.length; i++) {
			if(i>0) sb.append(",");
			sb.append(" ");
			sb.append(ann[i]);
		}
		return sb.toString();
	}

	/**
	 * Returns the annotations that should be removed from its parent.
	 * @param env the environment
	 * @param annotationTypes an array of annotation types that should be removed
	 * @return the annotations that should be removed from their parent
	 */
	protected Annotation[] getAnnotations(ISolutionExecutionEnvironment env, String[] annotationTypes) {
		if(null == annotationTypes || annotationTypes.length<1) return null;
		final Set<Annotation> result = new HashSet<Annotation>();
		final Set<String> typeNames = new HashSet<String>(Arrays.asList(annotationTypes));
		final Annotation ann = ASTVisitorUtilities.findAncestorWithType(env.getSelectedNode(), Annotation.class);
		if(null != ann) {
			if(typeNames.contains(ann.resolveAnnotationBinding().getAnnotationType().getQualifiedName()))
				result.add(ann);
		} else {
			final ASTNode parent = ASTVisitorUtilities.findAncestorWithType(env.getSelectedNode(), getAnnotatedElementType());
			if(null != parent) {
				parent.accept(new ASTVisitor() {
					private boolean _visit(Annotation node) {
						if(typeNames.contains(node.resolveAnnotationBinding().getAnnotationType().getQualifiedName()))
							result.add(node);
						return true;
					}
					/**
					 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MarkerAnnotation)
					 */
					@Override
					public boolean visit(MarkerAnnotation node) {
						return _visit(node);
					}
					/**
					 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.NormalAnnotation)
					 */
					@Override
					public boolean visit(NormalAnnotation node) {
						return _visit(node);
					}
					/**
					 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SingleMemberAnnotation)
					 */
					@Override
					public boolean visit(SingleMemberAnnotation node) {
						return _visit(node);
					}

				});
			}
		}
		return result.toArray(new Annotation[result.size()]);
	}
	
	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.solutions.ISolution#solve(de.ars.daojones.eclipse.jdt.markers.solutions.ISolutionExecutionEnvironment)
	 */
	@Override
	public void solve(ISolutionExecutionEnvironment env) throws CoreException {
		final String[] annotationTypes = getAnnotationTypes();
		if(null == annotationTypes || annotationTypes.length<1) return;
		final Annotation[] annotations = getAnnotations(env, annotationTypes);
		if(null != annotations && annotations.length>0) {
			final AST ast = env.getASTRoot().getAST();
			final ASTRewrite rewrite = ASTRewrite.create(ast);
			for(Annotation a : annotations) {
				rewrite.remove(a, null);
			}
			// computation of the text edits
			final TextEdit edits = env.rewrite(rewrite);
			env.apply(edits, null);
		}
	}

}
