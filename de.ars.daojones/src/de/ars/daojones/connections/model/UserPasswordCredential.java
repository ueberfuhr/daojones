package de.ars.daojones.connections.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.*;

/**
 * A default implementation of {@link IUserPasswordCredential}.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class UserPasswordCredential extends ConcreteCredential implements IUserPasswordCredential {

	private static final long serialVersionUID = 5334031675791246218L;
	private static final String TYPE = "UserPassword";

	@XmlElement(name=USERPASSWORDCREDENTIAL_USERNAME, required=true)
	private String username;
	@XmlElement(name=USERPASSWORDCREDENTIAL_PASSWORD, required=true)
	private String password;

	/**
	 * Creates an instance.
	 */
	public UserPasswordCredential() {
		super();
		setType(TYPE);
	}
	
	/**
	 * @see de.ars.daojones.connections.model.IUserPasswordCredential#getUsername()
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * Sets the username.
	 * @param username the username
	 * @see de.ars.daojones.connections.model.IUserPasswordCredential#getUsername()
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @see de.ars.daojones.connections.model.IUserPasswordCredential#getPassword()
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * Sets the password.
	 * @param password the password
	 * @see de.ars.daojones.connections.model.IUserPasswordCredential#getPassword()
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserPasswordCredential other = (UserPasswordCredential) obj;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
	
	
}
