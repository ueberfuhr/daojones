package de.ars.daojones.eclipse.jdt.ui.markers.handler;

import de.ars.daojones.annotations.Abstract;
import de.ars.daojones.annotations.DataSource;
import de.ars.daojones.eclipse.jdt.markers.solutions.ISolution;
import de.ars.daojones.eclipse.jdt.markers.solutions.ISolutionContext;
import de.ars.daojones.eclipse.jdt.markers.solutions.RemoveAnnotationsSolution;
import de.ars.daojones.eclipse.jdt.markers.solutions.SetTypeHierarchySolution;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataObjectContainer;

/**
 * Generates solutions for annotations annotating classes that are not
 * instance of {@link Dao}.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class InvalidTypeHierarchySolutionGenerator extends AbstractSolutionGenerator {

	/**
	 * @see de.ars.daojones.eclipse.jdt.ui.markers.handler.AbstractSolutionGenerator#getSolutions(de.ars.daojones.eclipse.jdt.markers.solutions.ISolutionContext)
	 */
	@Override
	protected ISolution[] getSolutions(ISolutionContext ctx) {
		return new ISolution[] {
			new SetTypeHierarchySolution() {
				@Override
				protected Class<?> getSuperType() {
					return DataObjectContainer.class;
				}
			},
			new SetTypeHierarchySolution() {
				@Override
				protected Class<?> getSuperType() {
					return Dao.class;
				}
			},
			new RemoveAnnotationsSolution() {
				@Override
				protected String[] getAnnotationTypes() {
					return new String[] {
						DataSource.class.getName(),
						Abstract.class.getName()
					};
				}
			}
		};
	}

}
