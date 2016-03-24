package de.ars.daojones.connections.model;

/**
 * This class provides information about pooling the connection.
 * The interface provides methods to change the attributes.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IChangeableInterval extends IInterval {

	/**
	 * Sets the minimum count of connections.
	 * @param value the minimum count of connections
	 */
	public void setMinimum(int value);
	/**
	 * Sets the maximum count of connections.
	 * @param value the maximum count of connections
	 */
	public void setMaximum(int value);

}
