package de.ars.daojones.runtime;

/**
 * An exception that occurs when loading a DaoJones bean implementation failed.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class BeanLoadingException extends Exception {

    private static final long serialVersionUID = 8832499422143792519L;

    /**
     * Creates an instance.
     */
    public BeanLoadingException() {
        super();
    }

    /**
     * Creates an instance.
     * 
     * @param message
     *            the message
     */
    public BeanLoadingException(String message) {
        super(message);
    }

    /**
     * Creates an instance.
     * 
     * @param cause
     *            the nested exception
     */
    public BeanLoadingException(Throwable cause) {
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
    public BeanLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

}
