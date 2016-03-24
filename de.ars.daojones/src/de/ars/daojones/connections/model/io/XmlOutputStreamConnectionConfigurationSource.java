package de.ars.daojones.connections.model.io;

import java.io.IOException;
import java.io.OutputStream;

import de.ars.daojones.connections.model.IConnectionConfiguration;

/**
 * A source writing into an {@link OutputStream}.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class XmlOutputStreamConnectionConfigurationSource implements IUpdateableConnectionConfigurationSource {

	private static final long serialVersionUID = -483848345222547204L;

	/**
	 * Returns the {@link OutputStream} where to write the information into.
	 * After writing into the {@link OutputStream}, it is closed.
	 * @param resource the resource name
	 * @return the {@link OutputStream}
	 * @throws IOException
	 */
	protected abstract OutputStream getOutputStream(String resource) throws IOException;
	
	/**
	 * This method is called after serializing the {@link IConnectionConfiguration}
	 * before the {@link OutputStream} is closed.
	 * @param configuration the {@link IConnectionConfiguration} 
	 * @param resource the resource name
	 * @param out the {@link OutputStream}
	 * @throws IOException
	 */
	protected void beforeClosing(IConnectionConfiguration configuration, String resource, OutputStream out) throws IOException {
		// do nothing
	}
	
	/**
	 * @see de.ars.daojones.connections.model.io.IUpdateableConnectionConfigurationSource#update(de.ars.daojones.connections.model.IConnectionConfiguration, java.lang.String)
	 */
	// TODO Java6-Migration
//	@Override
	public void update(IConnectionConfiguration configuration, String resource) throws IOException {
		final OutputStream out = getOutputStream(resource);
		if(null != out) {
			try {
				JAXBUtilities.write(configuration, out);
				beforeClosing(configuration, resource, out);
			} finally {
				out.close();
			}
		} else throw new IOException("Could not write into resource \"" + resource + "\"!");
	}
	
}
