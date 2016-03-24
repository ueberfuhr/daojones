package de.ars.daojones.connections.model.io;

import java.io.IOException;

import de.ars.daojones.connections.model.ConnectionConfiguration;
import de.ars.daojones.connections.model.IConnectionConfiguration;

/**
 * A connection configuration that can be changed.
 * At the moment, there is only the file type to be updated.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IUpdateableConnectionConfigurationSource extends IConnectionConfigurationSource {

	/**
	 * Updates the {@link ConnectionConfiguration}.
	 * @param configuration the {@link ConnectionConfiguration}
	 * @param resource the name of the resource (depends on the use case, e.g. file name)
	 * @throws IOException if writing the configuration failed
	 */
	public void update(IConnectionConfiguration configuration, String resource) throws IOException;
	
}
