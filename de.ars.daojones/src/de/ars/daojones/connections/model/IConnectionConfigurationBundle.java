package de.ars.daojones.connections.model;

import java.io.IOException;

/**
 * A bundle is a set of connection configurations that are merged to
 * one based on a kind of decision.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IConnectionConfigurationBundle {

	/**
	 * Returns the configuration.
	 * @return the configuration
	 * @throws IOException
	 */
	public IConnectionConfiguration getConfiguration() throws IOException;
	
}
