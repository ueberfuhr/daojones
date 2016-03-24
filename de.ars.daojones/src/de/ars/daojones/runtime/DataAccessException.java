package de.ars.daojones.runtime;

/**
 * An exception that occurs when accessing the database failed.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class DataAccessException extends Exception {

	private static final long serialVersionUID = 5621364630433706411L;

	/**
	 * Creates an instance.
	 */
	public DataAccessException() {
		super();
	}

	/**
	 * Creates an instance.
	 * 
	 * @param message
	 *            the message
	 */
	public DataAccessException(String message) {
		super(message);
	}

	/**
	 * Creates an instance.
	 * 
	 * @param cause
	 *            the nested exception
	 */
	public DataAccessException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates an instance.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the nested exception
	 */
	public DataAccessException(String message, Throwable cause) {
		super(message, cause);
	}

}
