package de.ars.daojones.sdk.codegen.generators;

/**
 * This exception is thrown when generating some content fails.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class GeneratorException extends Exception {

	private static final long serialVersionUID = 7011762872290752603L;

	/**
	 * @see Exception#Exception()
	 */
	public GeneratorException() {
		super();
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public GeneratorException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public GeneratorException(Throwable cause) {
		super(cause);
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public GeneratorException(String message, Throwable cause) {
		super(message, cause);
	}

}
