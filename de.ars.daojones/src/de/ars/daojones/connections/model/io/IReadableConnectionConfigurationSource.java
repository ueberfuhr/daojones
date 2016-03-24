package de.ars.daojones.connections.model.io;

import java.io.IOException;

import de.ars.daojones.connections.model.ConnectionConfiguration;
import de.ars.daojones.connections.model.IConnectionConfiguration;

/**
 * A connection configuration that can be read from a source.
 * Such a source can be an XML file or the plug-in extension registry.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IReadableConnectionConfigurationSource extends IConnectionConfigurationSource {

	/**
	 * Reads the configuration from the source.
	 * @param resource the name of the resource (depends on the use case, e.g. file name)
	 * @return the {@link ConnectionConfiguration}
	 * @throws InvalidFormatException
	 * @throws IOException if reading the configuration failed
	 */
	public IConnectionConfiguration read(String resource) throws InvalidFormatException, IOException;
	
}
