package de.ars.daojones.eclipse.jdt.markers;

import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.ars.daojones.annotations.AccessStrategy;
import de.ars.daojones.annotations.Column;
import de.ars.daojones.runtime.Dao;

/**
 * A marker indicating that a {@link Column} or {@link AccessStrategy} annotation
 * annotates a method that is not a valid getter or setter or is not abstract.
 * Being not abstract has the effect that there is only a warning.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class InvalidTypeHierarchyMarker extends AbstractMarker {

	/**
	 * The id.
	 */
	public static final String ID
		= "de.ars.daojones.eclipse.jdt.markers.invalidTypeHierarchyMarker";
	private static final String MESSAGE
		= "The annotated class does not implement the interface " + Dao.class.getName() + ".";

	/**
	 * Creates a marker.
	 * @param node
	 */
	public InvalidTypeHierarchyMarker(TypeDeclaration node) {
		super(ID, Severity.ERROR, MESSAGE, node.getName());
	}
	
}
