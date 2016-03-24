package de.ars.daojones.runtime;

/**
 * An exception that occurs when instantiating a DaoJones bean failed.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class BeanCreationException extends Exception {

	private static final long serialVersionUID = 8832499422143792519L;

	/**
	 * Creates an instance.
	 */
	public BeanCreationException() {
		super();
	}

	/**
	 * Creates an instance.
	 * 
	 * @param message
	 *            the message
	 */
	public BeanCreationException(String message) {
		super(message);
	}

	/**
	 * Creates an instance.
	 * 
	 * @param cause
	 *            the nested exception
	 */
	public BeanCreationException(Throwable cause) {
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
	public BeanCreationException(String message, Throwable cause) {
		super(message, cause);
	}

}
