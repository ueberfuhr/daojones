package de.ars.daojones.eclipse.jdt.ui.markers.handler;

import java.util.LinkedList;
import java.util.List;

import de.ars.daojones.eclipse.jdt.markers.solutions.ISolution;
import de.ars.daojones.eclipse.jdt.markers.solutions.ISolutionContext;
import de.ars.daojones.eclipse.jdt.markers.solutions.RemoveSelectedNodeSolution;

/**
 * A generator for the problem of having doubled-annotated
 * methods in DaoJones beans.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class DoubledAnnotedMethodSolutionGenerator extends AbstractSolutionGenerator {

	/**
	 * @see de.ars.daojones.eclipse.jdt.ui.markers.handler.AbstractSolutionGenerator#getSolutions(de.ars.daojones.eclipse.jdt.markers.solutions.ISolutionContext)
	 */
	@Override
	protected ISolution[] getSolutions(ISolutionContext ctx) {
		final List<ISolution> result = new LinkedList<ISolution>();
		result.add(new RemoveSelectedNodeSolution(
			"Remove selected annotation.", 
			"Removes the selected annotation from the annotated element."
		));
		return result.toArray(new ISolution[result.size()]);
	}

}
