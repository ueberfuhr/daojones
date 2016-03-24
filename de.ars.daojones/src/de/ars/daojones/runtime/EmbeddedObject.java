package de.ars.daojones.runtime;

import java.io.InputStream;

/**
 * A couple of bytes that represent a special information
 * that cannot be stored into text or any other prmitive form.
 * This can be used to store attachments/BLOBs/CLOBs.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface EmbeddedObject {

	/**
	 * Returns the size of the object.
	 * @return the size
	 */
	public int getLength();
	/**
	 * Returns the {@link InputStream} to read the bytes.
	 * @return the {@link InputStream}
	 */
	public InputStream getInputStream();
	/**
	 * Returns the content-type of the embedded object or
	 * null, if the content-type is unknown.
	 * @return the content-type
	 */
	public String getContentType();
	/**
	 * Call this method when you do not need the
	 * {@link EmbeddedObject} anymore. This releases
	 * resources.
	 */
	public void destroy();
    
}
