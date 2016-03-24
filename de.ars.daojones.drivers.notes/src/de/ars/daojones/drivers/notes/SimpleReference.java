package de.ars.daojones.drivers.notes;

import lotus.domino.Base;

/**
 * A {@link Reference} holding a single object that is shared between multiple
 * threads. Be careful using this kind because the {@link ThreadManager} will
 * not be able to recycle such objects.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 */
class SimpleReference<T extends Base> extends CallbackReference<T> {

	private static final long serialVersionUID = 3002472853633650152L;
	private transient T t;

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
	public SimpleReference(SerializableCallable<T> creator, T t) {
		super(creator, t);
	}

	/**
	 * Creates a reference to a {@link Base} object.
	 * 
	 * @param creator
	 *            a command object that is able to create the base object.
	 */
	public SimpleReference(SerializableCallable<T> creator) {
		super(creator);
	}

	/**
	 * @see de.ars.daojones.drivers.notes.CallbackReference#getT()
	 */
	@Override
	protected T getT() {
		return t;
	}

	/**
	 * @see de.ars.daojones.drivers.notes.CallbackReference#setT(lotus.domino.Base)
	 */
	@Override
	protected void setT(T t) {
		this.t = t;
	}

}
