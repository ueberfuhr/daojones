package de.ars.daojones.connections;

import de.ars.daojones.connections.model.IConnectionConfiguration;

/**
 * An exception that is thrown when building a {@link Connection}
 * from an {@link IConnectionConfiguration} failed.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ConnectionBuildException extends Exception {

	private static final long serialVersionUID = 6371376445617519482L;

	/**
	 * @see Exception#Exception()
	 */
	public ConnectionBuildException() {
		super();
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public ConnectionBuildException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public ConnectionBuildException(Throwable cause) {
		super(cause);
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public ConnectionBuildException(String message, Throwable cause) {
		super(message, cause);
	}

}
