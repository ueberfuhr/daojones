package de.ars.daojones.connections.model;

import java.io.Serializable;
import java.util.Collection;

import de.ars.daojones.connections.ApplicationContext;
import de.ars.daojones.connections.model.io.IReadableConnectionConfigurationSource;

/**
 * A set of connections and credentials that
 * can be used to create connections in
 * one {@link ApplicationContext}.
 * It is possible to use multiple {@link ConnectionConfiguration}s
 * within one {@link ApplicationContext}.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IConnectionConfiguration extends IConnectionConfigurationElement, Serializable {

	/**
	 * Returns the source containing the configuration.
	 * @return the source
	 */
	public IReadableConnectionConfigurationSource getSource();
	/**
	 * Sets the source containing the {@link ConnectionConfiguration}.
	 * @param source the source
	 * @see de.ars.daojones.connections.model.IConnectionConfiguration#getSource()
	 */
	public void setSource(IReadableConnectionConfigurationSource source);
	/**
	 * Returns a collection of connections.
	 * This collection must be modifiable to add or remove connections.
	 * @return a collection of connections
	 */
	public Collection<IConnection> getConnections();
	/**
	 * Returns a collection of credentials
	 * This collection must be modifiable to add or remove credentials.
	 * @return a collection of credentials
	 */
	public Collection<ICredential> getCredentials();
	/**
	 * Returns the imports that are contained in the configuration file.
	 * @return the imports
	 */
	public Collection<IImportDeclaration> getImportDeclarations();
	
}
