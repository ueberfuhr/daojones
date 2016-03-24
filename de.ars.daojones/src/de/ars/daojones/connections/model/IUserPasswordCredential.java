package de.ars.daojones.connections.model;

/**
 * A credential containing a username and a password. 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IUserPasswordCredential extends ICredential {

	/**
	 * Returns the username.
	 * @return the username
	 */
	public String getUsername();
	/**
	 * Returns the password.
	 * @return the password
	 */
	public String getPassword();
	
}
