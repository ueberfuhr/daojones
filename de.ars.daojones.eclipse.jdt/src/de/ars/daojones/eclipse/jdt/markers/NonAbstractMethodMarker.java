package de.ars.daojones.eclipse.jdt.markers;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import de.ars.daojones.annotations.AccessStrategy;
import de.ars.daojones.annotations.Column;

/**
 * A marker indicating that a {@link Column} or {@link AccessStrategy} annotation
 * annotates a method that is not abstract.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class NonAbstractMethodMarker extends AbstractMarker {

	/**
	 * The id.
	 */
	public static final String ID
		= "de.ars.daojones.eclipse.jdt.markers.nonAbstractMethodMarker";
	private static final String MESSAGE
		= "The annotated method is not abstract. DaoJones annotations are ignored.";

	/**
	 * Creates a marker.
	 * @param node
	 */
	public NonAbstractMethodMarker(MethodDeclaration node) {
		super(ID, Severity.WARNING, MESSAGE, node.getName());
	}
	
}
