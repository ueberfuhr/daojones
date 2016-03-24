package de.ars.daojones.connections.model;

import java.io.Serializable;

/**
 * A reference to a credential.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface ICredentialReference extends ICredential, Serializable {

	/**
	 * Returns the id of the referenced credential.
	 * @return the id of the referenced credential
	 */
	public String getReferenceId();
	
}
