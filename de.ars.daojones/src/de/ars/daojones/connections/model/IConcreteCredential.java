package de.ars.daojones.connections.model;

/**
 * An interface for a concrete credential.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IConcreteCredential extends ICredential {
	
	/**
	 * Returns the type.
	 * @return the type
	 */
	public String getType();
	
}
