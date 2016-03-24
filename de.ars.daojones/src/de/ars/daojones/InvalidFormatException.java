package de.ars.daojones;

import java.io.IOException;

/**
 * This exception is throws when a stream is read
 * that does not contain a valid format to unmarshal.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class InvalidFormatException extends IOException {

	private static final long serialVersionUID = -24227523636357701L;

	/**
	 * @see IOException#IOException()
	 */
	public InvalidFormatException() {
		super();
	}

	/**
	 * @see IOException#IOException(String)
	 */
	public InvalidFormatException(String message) {
		super(message);
	}

// TODO Java6-Migration
//	/**
//	 * @see IOException#IOException(Throwable)
//	 */
//	public InvalidFormatException(Throwable cause) {
//		super(cause);
//	}
//
// TODO Java6-Migration
//	/**
//	 * @see IOException#IOException(String, Throwable)
//	 */
//	public InvalidFormatException(String message, Throwable cause) {
//		super(message, cause);
//	}

}
