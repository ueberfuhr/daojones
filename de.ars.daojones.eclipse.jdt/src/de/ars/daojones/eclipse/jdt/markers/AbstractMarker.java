package de.ars.daojones.eclipse.jdt.markers;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * A default implementation of {@link IMarker}
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 *
 */
public class AbstractMarker implements IMarker {

	private final String id;
	private final int lineNumber;
	private final int charEnd;
	private final int charStart;
	private final String message;
	private final Severity severity;
	
	/**
	 * Creates a marker underlining a special node in the AST.
	 * @param id
	 * @param severity
	 * @param message
	 * @param node
	 */
	protected AbstractMarker(String id, Severity severity, String message, ASTNode node) {
		this(id, severity, message, node.getStartPosition(), node.getStartPosition()+node.getLength(), ((CompilationUnit)node.getRoot()).getLineNumber(node.getStartPosition()));
	}
	
	/**
	 * Creates an instance.
	 * @param id
	 * @param severity
	 * @param message
	 */
	protected AbstractMarker(String id, Severity severity, String message) {
		this(id, severity, message, -1, -1, -1);
	}
	
	/**
	 * Creates an instance.
	 * @param id
	 * @param severity
	 * @param message
	 * @param charEnd
	 * @param charStart
	 * @param lineNumber
	 */
	protected AbstractMarker(String id, Severity severity, String message, int charStart, int charEnd, int lineNumber) {
		super();
		this.charEnd = charEnd;
		this.charStart = charStart;
		this.id = id;
		this.lineNumber = lineNumber;
		this.message = message;
		this.severity = severity;
	}

	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.IMarker#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.IMarker#getLineNumber()
	 */
	@Override
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.IMarker#getCharEnd()
	 */
	@Override
	public int getCharEnd() {
		return charEnd;
	}

	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.IMarker#getCharStart()
	 */
	@Override
	public int getCharStart() {
		return charStart;
	}

	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.IMarker#getMessage()
	 */
	@Override
	public String getMessage() {
		return message;
	}

	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.IMarker#getSeverity()
	 */
	@Override
	public Severity getSeverity() {
		return severity;
	}

	/**
	 * @see de.ars.daojones.eclipse.jdt.markers.IMarker#getProblemId()
	 */
	@Override
	public int getProblemId() {
		return null != getId() ? getId().hashCode() : -1;
	}
	
	
	
}
