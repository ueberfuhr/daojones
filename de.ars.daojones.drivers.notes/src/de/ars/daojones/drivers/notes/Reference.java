package de.ars.daojones.drivers.notes;

import java.io.Serializable;

import lotus.domino.Base;

/**
 * A reference to a base object. Use this and do not use references to Notes
 * objects directly to avoid memory leaks.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
interface Reference<T extends Base> extends Base, Serializable {

	/**
	 * Returns the referenced object.
	 * 
	 * @return the referenced object
	 */
	public T get();

}
