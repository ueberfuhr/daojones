package de.ars.daojones.eclipse.jdt.ui.markers.handler;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import de.ars.daojones.eclipse.jdt.beans.ASTVisitorUtilities;
import de.ars.daojones.eclipse.jdt.markers.solutions.AddAbstractAnnotationSolution;
import de.ars.daojones.eclipse.jdt.markers.solutions.AddColumnAnnotationSolution;
import de.ars.daojones.eclipse.jdt.markers.solutions.ISolution;
import de.ars.daojones.eclipse.jdt.markers.solutions.ISolutionContext;

/**
 * A generator for the problem of having non-implementable
 * methods in a non-abstract class.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class AbstractMethodInNonAbstractBeanSolutionGenerator extends AbstractSolutionGenerator {

	/**
	 * @see de.ars.daojones.eclipse.jdt.ui.markers.handler.AbstractSolutionGenerator#getSolutions(ISolutionContext)
	 */
	@Override
	protected ISolution[] getSolutions(ISolutionContext ctx) {
		final List<ISolution> result = new LinkedList<ISolution>();
		if(null != ASTVisitorUtilities.findAncestorWithType(ctx.getSelectedNode(), MethodDeclaration.class)) {
			result.add(new AddColumnAnnotationSolution());
		}
		result.add(new AddAbstractAnnotationSolution());
		return result.toArray(new ISolution[result.size()]);
	}

}
