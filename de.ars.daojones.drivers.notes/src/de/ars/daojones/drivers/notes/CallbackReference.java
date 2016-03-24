package de.ars.daojones.drivers.notes;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Base;
import lotus.domino.NotesException;

/**
 * An abstract {@link Reference} providing common functionality.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 */
abstract class CallbackReference<T extends Base> implements Reference<T> {

	private static final long serialVersionUID = 7934647379413506511L;
	private static final Logger logger = Logger
			.getLogger(CallbackReference.class.getName());
	private final SerializableCallable<T> creator;

	/**
	 * Creates a reference to a {@link Base} object.
	 * 
	 * @param creator
	 *            a command object that is able to create the base object. This
	 *            command must implement {@link #hashCode()} and
	 *            {@link #equals(Object)} to identify this reference and
	 *            {@link #toString()} for formatting the reference's output.
	 * @param t
	 *            the base object, if already existing
	 */
	public CallbackReference(SerializableCallable<T> creator, T t) {
		this(creator);
		setT(t);
	}

	/**
	 * Creates a reference to a {@link Base} object.
	 * 
	 * @param creator
	 *            a command object that is able to create the base object.
	 */
	public CallbackReference(SerializableCallable<T> creator) {
		super();
		this.creator = creator;
	}

	/**
	 * Sets the value for the base object.
	 * 
	 * @param t
	 *            the base object
	 */
	protected abstract void setT(T t);

	/**
	 * Returns the value for the base object.
	 * 
	 * @return the base object
	 */
	protected abstract T getT();

	/**
	 * Checks if the object is a valid result for the method {@link #get()}. If
	 * this method returns false, the creator's method
	 * {@link SerializableCallable#call()} is called.
	 * 
	 * @param t
	 *            the object
	 * @return true, if the object is a valid result for the method
	 *         {@link #get()}
	 */
	protected boolean isValid(T t) {
		return null != t;
	}

	/**
	 * Returns the referenced object.
	 * 
	 * @return the referenced object
	 */
	public T get() {
		T result = getT();
		if (!isValid(result)) {
			try {
				result = this.creator.call();
				setT(result);
				ThreadManager.getInstance().register(this);
			} catch (Exception e) {
				logger.log(Level.SEVERE,
						"Unable to create the referenced object!", e);
			}
		}
		return result;
	}

	/**
	 * @see Base#recycle()
	 * @throws NotesException
	 */
	public void recycle() throws NotesException {
		try {
			final T t = getT();
			if (null != t)
				t.recycle();
		} finally {
			setT(null);
			ThreadManager.getInstance().unregister(this);
		}
	}

	/**
	 * @see Base#recycle(Vector)
	 */
	@SuppressWarnings("unchecked")
	public void recycle(Vector args) throws NotesException {
		try {
			final T t = getT();
			if (null != t)
				t.recycle(args);
		} finally {
			setT(null);
			ThreadManager.getInstance().unregister(this);
		}
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CallbackReference))
			return false;
		final SerializableCallable<T> otherCreator = ((CallbackReference<T>) o).creator;
		return this.creator.equals(otherCreator);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.creator.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Reference to " + this.creator.toString();
	}

}
