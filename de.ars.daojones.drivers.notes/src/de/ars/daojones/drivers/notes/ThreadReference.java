package de.ars.daojones.drivers.notes;

import lotus.domino.Base;

/**
 * A {@link Reference} managing objects per thread, so they are thread-safe.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 */
class ThreadReference<T extends Base> extends CallbackReference<T> {

	private static final long serialVersionUID = 7150494937741485273L;
	private transient ThreadLocal<T> t;

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
	public ThreadReference(SerializableCallable<T> creator, T t) {
		super(creator, t);
	}

	/**
	 * Creates a reference to a {@link Base} object.
	 * 
	 * @param creator
	 *            a command object that is able to create the base object.
	 */
	public ThreadReference(SerializableCallable<T> creator) {
		super(creator);
	}

	/**
	 * @see de.ars.daojones.drivers.notes.CallbackReference#getT()
	 */
	@Override
	protected T getT() {
		synchronized (this) {
			return null != t ? t.get() : null;
		}
	}

	/**
	 * @see de.ars.daojones.drivers.notes.CallbackReference#setT(lotus.domino.Base)
	 */
	@Override
	protected void setT(T t) {
		synchronized (this) {
			if (null == this.t) {
				this.t = new ThreadLocal<T>();
			}
			this.t.set(t);
		}
	}

}
