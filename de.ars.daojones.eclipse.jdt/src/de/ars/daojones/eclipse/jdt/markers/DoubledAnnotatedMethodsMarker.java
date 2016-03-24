package de.ars.daojones.eclipse.jdt.markers;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * A marker that indicates that there are getters und setters
 * that are both annotated. If they are annotated equally,
 * the marker is a warning, otherwise it is an error.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class DoubledAnnotatedMethodsMarker extends AbstractMarker {

	/**
	 * The ID of the marker used in the plugin.xml config file.
	 */
	public static final String ID 
		= "de.ars.daojones.eclipse.jdt.markers.doubledAnnotatedMethod";
	private static final String MESSAGE_WARNING
		= "Getter and Setter do not both need to be annotated.";
	private static final String MESSAGE_ERROR
	= "Getter and Setter have different annotations.";

	/**
	 * Creates a marker.
	 * @param node
	 * @param isError
	 */
	public DoubledAnnotatedMethodsMarker(ASTNode node, boolean isError) {
		super(ID, isError ? Severity.ERROR : Severity.WARNING, isError ? MESSAGE_ERROR : MESSAGE_WARNING, node);
	}

}
