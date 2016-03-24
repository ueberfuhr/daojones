package de.ars.daojones.runtime.query;

/**
 * This exception is thrown when resolving columns or tables by the {@link VariableResolver}
 * failed.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class VariableResolvingException extends Exception {

	private static final long serialVersionUID = -3964818553385647336L;

	/**
	 * @see Exception#Exception()
	 */
	public VariableResolvingException() {
		super();
	}

	/**
	 * @see Exception#Exception(String)
	 * @param message
	 */
	public VariableResolvingException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 * @param cause
	 */
	public VariableResolvingException(Throwable cause) {
		super(cause);
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 * @param message
	 * @param cause
	 */
	public VariableResolvingException(String message, Throwable cause) {
		super(message, cause);
	}

}
