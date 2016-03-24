package de.ars.daojones.eclipse.jdt.ui.markers.handler;

import de.ars.daojones.annotations.AccessStrategy;
import de.ars.daojones.annotations.Column;
import de.ars.daojones.eclipse.jdt.markers.solutions.ISolution;
import de.ars.daojones.eclipse.jdt.markers.solutions.ISolutionContext;
import de.ars.daojones.eclipse.jdt.markers.solutions.RemoveAnnotationsSolution;

/**
 * Generates solutions for annotations annotating invalid methods.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class InvalidMethodFormatSolutionGenerator extends AbstractSolutionGenerator {

	/**
	 * @see de.ars.daojones.eclipse.jdt.ui.markers.handler.AbstractSolutionGenerator#getSolutions(de.ars.daojones.eclipse.jdt.markers.solutions.ISolutionContext)
	 */
	@Override
	protected ISolution[] getSolutions(ISolutionContext ctx) {
		return new ISolution[] {
			new RemoveAnnotationsSolution() {
				@Override
				protected String[] getAnnotationTypes() {
					return new String[] {
						Column.class.getName(),
						AccessStrategy.class.getName()
					};
				}
			}
		};
	}

}
