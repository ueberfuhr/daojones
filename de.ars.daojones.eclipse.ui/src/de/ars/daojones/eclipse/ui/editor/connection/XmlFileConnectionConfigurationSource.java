package de.ars.daojones.eclipse.ui.editor.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import de.ars.daojones.connections.model.IConnectionConfiguration;
import de.ars.daojones.connections.model.io.IReadableConnectionConfigurationSource;
import de.ars.daojones.connections.model.io.IUpdateableConnectionConfigurationSource;
import de.ars.daojones.connections.model.io.XmlInputStreamConnectionConfigurationSource;
import de.ars.daojones.connections.model.io.XmlOutputStreamConnectionConfigurationSource;

/**
 * A source for reading and writing {@link IConnectionConfiguration}s
 * from/into xml files.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class XmlFileConnectionConfigurationSource extends XmlInputStreamConnectionConfigurationSource implements IReadableConnectionConfigurationSource, IUpdateableConnectionConfigurationSource {

	private static final long serialVersionUID = -2631404248774949274L;
	
	private final IFile file;
	
	/**
	 * Creates an instance.
	 * @param file the file
	 */
	public XmlFileConnectionConfigurationSource(IFile file) {
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
			return new ByteArrayOutputStream();
		}
		@Override
		protected void beforeClosing(IConnectionConfiguration configuration, String resource, OutputStream out) throws IOException {
			super.beforeClosing(configuration, resource, out);
			final ByteArrayInputStream in = new ByteArrayInputStream(((ByteArrayOutputStream)out).toByteArray());
			try {
				getFile(resource).setContents(in, IFile.FORCE, null);
			} catch (CoreException e) {
				throw new IOException(e);
			}
		}
		
	};
	
	private IFile getFile(String resource) throws IOException {
		if(null == resource || resource.equals(getFile().getFullPath().toOSString())) return getFile();
		return getFile().getParent().getFile(new Path(resource));
	}

	/**
	 * Returns the file object.
	 * @return the file object
	 * @throws IOException
	 */
	public IFile getFile() throws IOException {
		return this.file;
	}
	
	/**
	 * @see de.ars.daojones.connections.model.io.XmlInputStreamConnectionConfigurationSource#getInputStream(java.lang.String)
	 */
	@Override
	protected InputStream getInputStream(String resource) throws IOException {
		try {
			final IFile file = getFile(resource);
			return null != file ? file.getContents() : null;
		} catch (CoreException e) {
			throw new IOException(e);
		}
	}

	/**
	 * @see de.ars.daojones.connections.model.io.IUpdateableConnectionConfigurationSource#update(de.ars.daojones.connections.model.IConnectionConfiguration, java.lang.String)
	 */
	// TODO Java6-Migration
	// @Override
	public void update(IConnectionConfiguration configuration, String resource) throws IOException {
		outSource.update(configuration, resource);
	}

}
