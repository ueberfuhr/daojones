package de.ars.daojones.connections.model;

import java.io.Serializable;

/**
 * An interface for an element of a connection configuration.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IConnectionConfigurationElement extends Serializable {

	/**
	 * Returns the {@link ConnectionConfiguration} containing this element.
	 * @return the parent {@link ConnectionConfiguration}
	 */
	public IConnectionConfiguration getConnectionConfiguration();
	
}
