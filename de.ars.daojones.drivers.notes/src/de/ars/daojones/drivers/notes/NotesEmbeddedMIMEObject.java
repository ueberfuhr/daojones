package de.ars.daojones.drivers.notes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Document;
import lotus.domino.MIMEEntity;
import lotus.domino.MIMEHeader;
import lotus.domino.NotesException;
import lotus.domino.Stream;
import de.ars.daojones.runtime.EmbeddedObject;

/**
 * A class to read embedded objects from a MIME entity.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class NotesEmbeddedMIMEObject implements EmbeddedObject {

	private static final Logger logger = Logger
			.getLogger(NotesEmbeddedMIMEObject.class.getName());
	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	private MIMEEntity entity;
	private final NotesDocumentObject dataObject;
	private NotesStream stream;
	private Integer length;

	/**
	 * Creates an object to read an embedded object from a MIME entity.
	 * 
	 * @param root
	 *            the root MIME entity object
	 * @param dataObject
	 *            the data object
	 */
	public NotesEmbeddedMIMEObject(final MIMEEntity root,
			final NotesDocumentObject dataObject) {
		super();
		this.dataObject = dataObject;
		this.entity = getAttachment(root);
	}

	/**
	 * @see de.ars.daojones.runtime.EmbeddedObject#getContentType()
	 */
	public String getContentType() {
		return getHeaderValue(this.entity, CONTENT_TYPE_HEADER);
	}

	/**
	 * Returns the document containing the embedded object.
	 * 
	 * @return the document containing the embedded object
	 */
	protected Document getDocument() {
		return this.dataObject.getDocument();
	}

	private static MIMEEntity getAttachment(MIMEEntity root) {
		try {
			final String contentType = getHeaderValue(root, CONTENT_TYPE_HEADER);
			if (null == contentType || contentType.startsWith("multipart/")
					|| contentType.equals("text/html")) {
				MIMEEntity child = root.getFirstChildEntity();
				MIMEEntity htmlChild = null;
				while (null != child) {
					final MIMEEntity result = getAttachment(child);
					if (null != result)
						return result;
					final String childContentType = getHeaderValue(child,
							CONTENT_TYPE_HEADER);
					if ("text/html".equals(childContentType)) {
						htmlChild = child;
					} else {
						return child;
					}
					child = child.getNextSibling();
				}
				return htmlChild;
			} else
				return root;
		} catch (NotesException e) {
			logger.log(Level.WARNING, "The MIMEEntity could not be analyzed!",
					e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static String getHeaderValue(MIMEEntity entity, String headerName) {
		try {
			for (MIMEHeader header : (Collection<MIMEHeader>) entity
					.getHeaderObjects()) {
				if (header.getHeaderName().equals(headerName))
					return header.getHeaderVal();
			}
		} catch (NotesException e) {
			logger.log(Level.WARNING,
					"The headers of a MIMEEntity could not be read!", e);
		}
		return null;
	}

	private NotesStream getStream() throws NotesException, IOException {
		if (null == stream) {
			if (null != this.entity) {
				if (this.entity.getEncoding() != MIMEEntity.ENC_NONE)
					this.entity.decodeContent();
				final Stream stream = getDocument().getParentDatabase()
						.getParent().createStream();
				this.entity.getContentAsBytes(stream);
				stream.setPosition(0);
				this.stream = new NotesStream(stream) {
					@Override
					public void close() throws IOException {
						super.close();
						NotesEmbeddedMIMEObject.this.stream = null;
					}
				};
				this.stream.mark(this.stream.available());
			}
			;
		}
		;
		return stream;
	}

	/**
	 * @see de.ars.daojones.runtime.EmbeddedObject#getInputStream()
	 */
	public InputStream getInputStream() {
		try {
			return getStream();
		} catch (NotesException e) {
			logger.log(Level.SEVERE,
					"The InputStream of the MIMEEntity could not be used!", e);
		} catch (IOException e) {
			logger.log(Level.SEVERE,
					"The InputStream of the MIMEEntity could not be used!", e);
		}
		return null;
	}

	/**
	 * @see de.ars.daojones.runtime.EmbeddedObject#getLength()
	 */
	public int getLength() {
		if (null == length) {
			try {
				final NotesStream stream = getStream();
				length = stream.available();
			} catch (NotesException e) {
				logger.log(Level.SEVERE,
						"The length of the stream could not be read!", e);
			} catch (IOException e) {
				logger.log(Level.SEVERE,
						"The length of the stream could not be read!", e);
			}
		}
		;
		return null != length ? length : 0;
	}

	/**
	 * @see de.ars.daojones.runtime.EmbeddedObject#destroy()
	 */
	public void destroy() {
		if (null != this.entity)
			try {
				try {
					this.entity.recycle();
				} finally {
					this.entity = null;
				}
			} catch (NotesException e) {
				logger.log(Level.FINER, "Could not destroy MIME entity!", e);
			}
	}

	/**
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		destroy();
		super.finalize();
	}

}
