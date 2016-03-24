package de.ars.daojones.beans.jit;

/**
 * This exception is thrown when generating some content fails.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class JITGeneratorException extends Exception {

	private static final long serialVersionUID = 7011762872290752603L;

	/**
	 * @see Exception#Exception()
	 */
	public JITGeneratorException() {
		super();
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public JITGeneratorException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public JITGeneratorException(Throwable cause) {
		super(cause);
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public JITGeneratorException(String message, Throwable cause) {
		super(message, cause);
	}

}
