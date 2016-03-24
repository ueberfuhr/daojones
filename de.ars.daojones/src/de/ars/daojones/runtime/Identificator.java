package de.ars.daojones.runtime;

import java.io.Serializable;

import de.ars.daojones.connections.Connection;

/**
 * An identificator for the object.
 * Its {@link #toString()} method create a String that can be used
 * for {@link Connection#findById(String)}.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface Identificator extends Serializable {
	
	/**
	 * Creates a unique String that can be used to create the object by ID.
	 * @see Object#toString()
	 */
	public String toString();

}
