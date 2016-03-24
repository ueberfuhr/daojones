package de.ars.daojones.connections.model.io;

import java.io.IOException;
import java.io.InputStream;

import de.ars.daojones.connections.model.ConnectionConfiguration;
import de.ars.daojones.connections.model.IConnectionConfiguration;

/**
 * A source reading from an {@link InputStream}.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class XmlInputStreamConnectionConfigurationSource implements IReadableConnectionConfigurationSource {

	/**
	 * Returns the {@link InputStream} where to read the information.
	 * After reading the {@link InputStream}, it is closed.
	 * @param resource the resource name
	 * @return the {@link InputStream}
	 * @throws IOException
	 */
	protected abstract InputStream getInputStream(String resource) throws IOException;
	
	/**
	 * @see de.ars.daojones.connections.model.io.IReadableConnectionConfigurationSource#read(java.lang.String)
	 */
	// TODO Java6-Migration
//	@Override
	public IConnectionConfiguration read(String resource) throws InvalidFormatException, IOException {
		final InputStream in = getInputStream(resource);
		if(null != in) {
			try {
				final ConnectionConfiguration result = JAXBUtilities.read(in, ConnectionConfiguration.class);
				result.resolveParents();
				result.setSource(this);
				return result;
			} finally {
				in.close();
			}
		} else throw new IOException("Resource \"" + resource + "\" could not be found!");
	}

}
