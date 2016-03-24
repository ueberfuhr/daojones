package de.ars.daojones.runtime.search;

/**
 * An exception that occurs when searching the database failed, e.g. because of
 * a query syntax error that then would be a bug of the driver.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 */
public class SearchException extends Exception {

	private static final long serialVersionUID = 1998822057039312369L;

	/**
	 * Creates an instance.
	 */
	public SearchException() {
		super();
	}

	/**
	 * Creates an instance.
	 * 
	 * @param message
	 *            the message
	 */
	public SearchException(String message) {
		super(message);
	}

	/**
	 * Creates an instance.
	 * 
	 * @param cause
	 *            the nested exception
	 */
	public SearchException(Throwable cause) {
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
	public SearchException(String message, Throwable cause) {
		super(message, cause);
	}

}
