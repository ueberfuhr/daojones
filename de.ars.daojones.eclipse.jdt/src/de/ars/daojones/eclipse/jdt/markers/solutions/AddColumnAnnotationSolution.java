package de.ars.daojones.eclipse.jdt.markers.solutions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;

import de.ars.daojones.annotations.Column;

/**
 * Adds the column annotation to the selected node (method).
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class AddColumnAnnotationSolution extends AddAnnotationSolution {

	private static final String TITLE = "Add column information";
	private static final String DESCRIPTION = "Add the @Column annotation to make the abstract method implementable.";
	
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
		return Column.class.getName();
	}

	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.solutions.AddAnnotationSolution#getAnnotationMembers(AST)
	 */
	@Override
	protected Map<String, Expression> getAnnotationMembers(AST ast) {
		final Map<String, Expression> map = new HashMap<String, Expression>();
		final StringLiteral column = ast.newStringLiteral();
		column.setLiteralValue("");
		map.put("value", column);
		return map;
	}

	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.solutions.AddAnnotationSolution#getAnnotatedElementType()
	 */
	@Override
	protected Class<? extends BodyDeclaration> getAnnotatedElementType() {
		return MethodDeclaration.class;
	}

}
