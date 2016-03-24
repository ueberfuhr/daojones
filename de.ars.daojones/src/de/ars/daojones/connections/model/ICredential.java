package de.ars.daojones.connections.model;

import java.io.Serializable;

/**
 * An interface for a declared credential.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface ICredential extends IConnectionConfigurationElement, Serializable {
	
	/**
	 * Returns the ID of the credential.
	 * You can use this only on globally defined credentials.
	 * @return the ID of the credential
	 */
	public String getId();
	
}
