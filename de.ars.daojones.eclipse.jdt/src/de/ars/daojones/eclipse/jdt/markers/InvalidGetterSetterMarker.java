package de.ars.daojones.eclipse.jdt.markers;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import de.ars.daojones.annotations.AccessStrategy;
import de.ars.daojones.annotations.Column;

/**
 * A marker indicating that a {@link Column} or {@link AccessStrategy} annotation
 * annotates a method that is not a valid getter or setter.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class InvalidGetterSetterMarker extends AbstractMarker {

	/**
	 * The id.
	 */
	public static final String ID
		= "de.ars.daojones.eclipse.jdt.markers.invalidGetterSetterFormatMarker";
	private static final String MESSAGE
		= "The annotated method must be a valid bean getter or setter.";

	/**
	 * Creates a marker.
	 * @param node
	 */
	public InvalidGetterSetterMarker(MethodDeclaration node) {
		super(ID, Severity.ERROR, MESSAGE, node.getName());
	}
	
}
