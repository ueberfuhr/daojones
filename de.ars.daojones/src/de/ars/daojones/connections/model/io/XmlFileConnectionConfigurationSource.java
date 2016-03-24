package de.ars.daojones.connections.model.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.ars.daojones.connections.model.IConnectionConfiguration;

/**
 * A source for reading and writing {@link IConnectionConfiguration}s
 * from/into xml files.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class XmlFileConnectionConfigurationSource extends XmlInputStreamConnectionConfigurationSource implements IReadableConnectionConfigurationSource, IUpdateableConnectionConfigurationSource {

	private final File file;
	
	/**
	 * Creates an instance.
	 * @param file the file
	 */
	public XmlFileConnectionConfigurationSource(File file) {
		super();
		this.file = file;
	}

	/**
	 * @see de.ars.daojones.connections.model.io.XmlInputStreamConnectionConfigurationSource#read(java.lang.String)
	 */
	@Override
	public IConnectionConfiguration read(String resource) throws IOException {
		final IConnectionConfiguration result = super.read(resource);
		result.setSource(new XmlFileConnectionConfigurationSource(getFile(resource)));
		return result;
	}

	private final XmlOutputStreamConnectionConfigurationSource outSource = new XmlOutputStreamConnectionConfigurationSource() {
		private static final long serialVersionUID = -3625829615316036444L;
		@Override
		protected OutputStream getOutputStream(String resource) throws IOException {
			final File file = getFile(resource);
			return null != file ? new FileOutputStream(file) : null;
		}
	};
	
	private File getFile(String resource) throws IOException {
		if(null == resource || resource.equals(getFile().getAbsolutePath())) return getFile();
		return new File(getFile(), resource);
	}

	/**
	 * Returns the file object.
	 * @return the file object
	 * @throws IOException
	 */
	public File getFile() throws IOException {
		return this.file;
	}
	
	/**
	 * @see de.ars.daojones.connections.model.io.XmlInputStreamConnectionConfigurationSource#getInputStream(java.lang.String)
	 */
	@Override
	protected InputStream getInputStream(String resource) throws IOException {
		final File file = getFile(resource);
		return null != file ? new FileInputStream(file) : null;
	}

	/**
	 * @see de.ars.daojones.connections.model.io.IUpdateableConnectionConfigurationSource#update(de.ars.daojones.connections.model.IConnectionConfiguration, java.lang.String)
	 */
	// TODO Java6-Migration
//	@Override
	public void update(IConnectionConfiguration configuration, String resource) throws IOException {
		outSource.update(configuration, resource);
	}

}
