package de.ars.daojones.drivers.notes;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.NotesException;
import de.ars.daojones.runtime.EmbeddedObject;

/**
 * An embedded object in Notes documents.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class NotesEmbeddedObject implements EmbeddedObject {

	private static final Logger logger = Logger
			.getLogger(NotesEmbeddedObject.class.getName());
	private lotus.domino.EmbeddedObject embeddedObject;

	public NotesEmbeddedObject(lotus.domino.EmbeddedObject embeddedObject) {
		this.embeddedObject = embeddedObject;
	}

	lotus.domino.EmbeddedObject getEmbeddedObject() {
		return embeddedObject;
	}

	public int getContentLength() {
		try {
			return embeddedObject.getFileSize();
		} catch (NotesException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public InputStream getInputStream() {
		try {
			return embeddedObject.getInputStream();
		} catch (NotesException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getLength() {
		return getContentLength();
	}

	public void destroy() {
		if (null != embeddedObject)
			try {
				try {
					embeddedObject.recycle();
				} finally {
					embeddedObject = null;
				}
			} catch (NotesException e) {
				logger.log(Level.WARNING, "Unable to recycle embedded object!",
						e);
			}
	}

	@Override
	protected void finalize() throws Throwable {
		destroy();
		super.finalize();
	}

	public String getContentType() {
		return null;
	}

}
