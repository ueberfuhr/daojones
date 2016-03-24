package de.ars.daojones.connections.model;

import java.io.Serializable;

/**
 * This class provides information about pooling the connection.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IInterval extends Serializable {

	/**
	 * Returns the minimum count of connections.
	 * @return the minimum count of connections
	 */
	public int getMinimum();
	/**
	 * Returns the maximum count of connections.
	 * @return the maximum count of connections
	 */
	public int getMaximum();

}
