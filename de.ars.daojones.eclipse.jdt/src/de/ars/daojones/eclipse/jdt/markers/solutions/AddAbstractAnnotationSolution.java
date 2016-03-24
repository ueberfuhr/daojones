package de.ars.daojones.eclipse.jdt.markers.solutions;

import java.util.Map;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.ars.daojones.annotations.Abstract;

/**
 * A solution that adds the {@link Abstract} annotation
 * to the bean class.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class AddAbstractAnnotationSolution extends AddAnnotationSolution {

	private static final String TITLE =
		"Declare the class as an abstract bean.";
	private static final String DESCRIPTION = 
		"This declares that the bean implementation is abstract by adding the @Abstract annotation. "
		+ "You have to create a subclass of this bean to create instances.";

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
	 * @see de.ars.daojones.eclipse.jdt.markers.solutions.AddAnnotationSolution#getAnnotation()
	 */
	@Override
	protected String getAnnotation() {
		return Abstract.class.getName();
	}

	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.solutions.AddAnnotationSolution#getAnnotationMembers(AST)
	 */
	@Override
	protected Map<String, Expression> getAnnotationMembers(AST ast) {
		return null;
	}

	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.solutions.AddAnnotationSolution#getAnnotatedElementType()
	 */
	@Override
	protected Class<? extends BodyDeclaration> getAnnotatedElementType() {
		return TypeDeclaration.class;
	}
	
}
