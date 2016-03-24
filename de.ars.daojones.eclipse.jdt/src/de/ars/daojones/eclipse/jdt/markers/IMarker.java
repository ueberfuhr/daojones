package de.ars.daojones.eclipse.jdt.markers;

/**
 * An interface for a marker.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IMarker {

	/**
	 * The severity of a marker.
	 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
	 */
	public static enum Severity {
		/**
		 * Indicates that the marker is for informal purposes.
		 */
		INFO, 
		/**
		 * Indicates that the marker is for warning purposes.
		 */
		WARNING, 
		/**
		 * Indicates that the marker is for error purposes.
		 */
		ERROR
	}
	
	/**
	 * Returns the problem ID of the marker.
	 * @return the problem ID.
	 */
	public int getProblemId();
	/**
	 * Returns the message of the marker.
	 * @return the message
	 */
	public String getMessage();
	/**
	 * Returns the line number of the message or -1,
	 * if there is no line number.
	 * @return the line number
	 */
	public int getLineNumber();
	/**
	 * @see org.eclipse.core.resources.IMarker#CHAR_START
	 * @return -1 or the char index
	 */
	public int getCharStart();
	/**
	 * @see org.eclipse.core.resources.IMarker#CHAR_END
	 * @return -1 or the char index
	 */
	public int getCharEnd();
	/**
	 * Returns the severity of the marker.
	 * @return the severity
	 */
	public Severity getSeverity();
	/**
	 * Returns the ID of the marker that
	 * is used in the plugin.xml file.
	 * @return the ID
	 */
	public String getId();
	
}
