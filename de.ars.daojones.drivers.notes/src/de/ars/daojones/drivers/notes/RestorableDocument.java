package de.ars.daojones.drivers.notes;

import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

import lotus.domino.Base;
import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.EmbeddedObject;
import lotus.domino.Item;
import lotus.domino.MIMEEntity;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.View;
import lotus.domino.XSLTResultTarget;

/**
 * A {@link Document} that can be accessed again after recycling, if necessary.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 */
@SuppressWarnings("unchecked")
class RestorableDocument implements Document {

	private Document delegate;
	private final SerializableCallable<Document> creator;

	public RestorableDocument(SerializableCallable<Document> creator,
			Document delegate) {
		this(creator);
		this.delegate = delegate;
	}

	public RestorableDocument(SerializableCallable<Document> creator) {
		super();
		this.creator = creator;
	}

	private Document getDelegate() throws NotesException {
		return getDelegate(false);
	}

	private Document getDelegate(boolean original) throws NotesException {
		if (!original && (null == this.delegate || !isValid() || isDeleted())) {
			try {
				this.delegate = creator.call();
			} catch (NotesException e) {
				throw e;
			} catch (Exception e) {
				throw new NotesException(0, "Error during document refresh!", e);
			}
		}
		return this.delegate;
	}

	// ------------------------------------------

	public Item appendItemValue(String arg0, double arg1) throws NotesException {
		return getDelegate().appendItemValue(arg0, arg1);
	}

	public Item appendItemValue(String arg0, int arg1) throws NotesException {
		return getDelegate().appendItemValue(arg0, arg1);
	}

	public Item appendItemValue(String arg0, Object arg1) throws NotesException {
		return getDelegate().appendItemValue(arg0, arg1);
	}

	public Item appendItemValue(String arg0) throws NotesException {
		return getDelegate().appendItemValue(arg0);
	}

	public void attachVCard(Base arg0) throws NotesException {
		getDelegate().attachVCard(arg0);
	}

	public boolean closeMIMEEntities() throws NotesException {
		return getDelegate().closeMIMEEntities();
	}

	public boolean closeMIMEEntities(boolean arg0, String arg1)
			throws NotesException {
		return getDelegate().closeMIMEEntities(arg0, arg1);
	}

	public boolean closeMIMEEntities(boolean arg0) throws NotesException {
		return getDelegate().closeMIMEEntities(arg0);
	}

	public boolean computeWithForm(boolean arg0, boolean arg1)
			throws NotesException {
		return getDelegate().computeWithForm(arg0, arg1);
	}

	public void copyAllItems(Document arg0, boolean arg1) throws NotesException {
		getDelegate().copyAllItems(arg0, arg1);
	}

	public Item copyItem(Item arg0, String arg1) throws NotesException {
		return getDelegate().copyItem(arg0, arg1);
	}

	public Item copyItem(Item arg0) throws NotesException {
		return getDelegate().copyItem(arg0);
	}

	public Document copyToDatabase(Database arg0) throws NotesException {
		return getDelegate().copyToDatabase(arg0);
	}

	public MIMEEntity createMIMEEntity() throws NotesException {
		return getDelegate().createMIMEEntity();
	}

	public MIMEEntity createMIMEEntity(String arg0) throws NotesException {
		return getDelegate().createMIMEEntity(arg0);
	}

	public Document createReplyMessage(boolean arg0) throws NotesException {
		return getDelegate().createReplyMessage(arg0);
	}

	public RichTextItem createRichTextItem(String arg0) throws NotesException {
		return getDelegate().createRichTextItem(arg0);
	}

	public void encrypt() throws NotesException {
		getDelegate().encrypt();
	}

	public String generateXML() throws NotesException {
		return getDelegate().generateXML();
	}

	public void generateXML(Object arg0, XSLTResultTarget arg1)
			throws IOException, NotesException {
		getDelegate().generateXML(arg0, arg1);
	}

	public void generateXML(Writer arg0) throws NotesException, IOException {
		getDelegate().generateXML(arg0);
	}

	public EmbeddedObject getAttachment(String arg0) throws NotesException {
		return getDelegate().getAttachment(arg0);
	}

	public Vector getAuthors() throws NotesException {
		return getDelegate().getAuthors();
	}

	public Vector getColumnValues() throws NotesException {
		return getDelegate().getColumnValues();
	}

	public DateTime getCreated() throws NotesException {
		return getDelegate().getCreated();
	}

	public Vector getEmbeddedObjects() throws NotesException {
		return getDelegate().getEmbeddedObjects();
	}

	public Vector getEncryptionKeys() throws NotesException {
		return getDelegate().getEncryptionKeys();
	}

	public Item getFirstItem(String arg0) throws NotesException {
		return getDelegate().getFirstItem(arg0);
	}

	public Vector getFolderReferences() throws NotesException {
		return getDelegate().getFolderReferences();
	}

	public int getFTSearchScore() throws NotesException {
		return getDelegate().getFTSearchScore();
	}

	public String getHttpURL() throws NotesException {
		return getDelegate().getHttpURL();
	}

	public Vector getItems() throws NotesException {
		return getDelegate().getItems();
	}

	public Vector getItemValue(String arg0) throws NotesException {
		return getDelegate().getItemValue(arg0);
	}

	public Object getItemValueCustomData(String arg0, String arg1)
			throws IOException, ClassNotFoundException, NotesException {
		return getDelegate().getItemValueCustomData(arg0, arg1);
	}

	public Object getItemValueCustomData(String arg0) throws IOException,
			ClassNotFoundException, NotesException {
		return getDelegate().getItemValueCustomData(arg0);
	}

	public byte[] getItemValueCustomDataBytes(String arg0, String arg1)
			throws IOException, NotesException {
		return getDelegate().getItemValueCustomDataBytes(arg0, arg1);
	}

	public Vector getItemValueDateTimeArray(String arg0) throws NotesException {
		return getDelegate().getItemValueDateTimeArray(arg0);
	}

	public double getItemValueDouble(String arg0) throws NotesException {
		return getDelegate().getItemValueDouble(arg0);
	}

	public int getItemValueInteger(String arg0) throws NotesException {
		return getDelegate().getItemValueInteger(arg0);
	}

	public String getItemValueString(String arg0) throws NotesException {
		return getDelegate().getItemValueString(arg0);
	}

	public String getKey() throws NotesException {
		return getDelegate().getKey();
	}

	public DateTime getLastAccessed() throws NotesException {
		return getDelegate().getLastAccessed();
	}

	public DateTime getLastModified() throws NotesException {
		return getDelegate().getLastModified();
	}

	public Vector getLockHolders() throws NotesException {
		return getDelegate().getLockHolders();
	}

	public MIMEEntity getMIMEEntity() throws NotesException {
		return getDelegate().getMIMEEntity();
	}

	public MIMEEntity getMIMEEntity(String arg0) throws NotesException {
		return getDelegate().getMIMEEntity(arg0);
	}

	public String getNameOfProfile() throws NotesException {
		return getDelegate().getNameOfProfile();
	}

	public String getNoteID() throws NotesException {
		return getDelegate().getNoteID();
	}

	public String getNotesURL() throws NotesException {
		return getDelegate().getNotesURL();
	}

	public Database getParentDatabase() throws NotesException {
		return getDelegate().getParentDatabase();
	}

	public String getParentDocumentUNID() throws NotesException {
		return getDelegate().getParentDocumentUNID();
	}

	public View getParentView() throws NotesException {
		return getDelegate().getParentView();
	}

	public Vector getReceivedItemText() throws NotesException {
		return getDelegate().getReceivedItemText();
	}

	public DocumentCollection getResponses() throws NotesException {
		return getDelegate().getResponses();
	}

	public String getSigner() throws NotesException {
		return getDelegate().getSigner();
	}

	public int getSize() throws NotesException {
		return getDelegate().getSize();
	}

	public String getUniversalID() throws NotesException {
		return getDelegate().getUniversalID();
	}

	public String getURL() throws NotesException {
		return getDelegate().getURL();
	}

	public String getVerifier() throws NotesException {
		return getDelegate().getVerifier();
	}

	public boolean hasEmbedded() throws NotesException {
		return getDelegate().hasEmbedded();
	}

	public boolean hasItem(String arg0) throws NotesException {
		return getDelegate().hasItem(arg0);
	}

	public boolean isDeleted() throws NotesException {
		final Document delegate = getDelegate(true);
		try {
			return null != delegate && delegate.isDeleted();
		} catch (NotesException e) {
			// avoid CORBA exceptions
			return true;
		}
	}

	public boolean isEncrypted() throws NotesException {
		return getDelegate().isEncrypted();
	}

	public boolean isEncryptOnSend() throws NotesException {
		return getDelegate().isEncryptOnSend();
	}

	public boolean isNewNote() throws NotesException {
		return getDelegate().isNewNote();
	}

	public boolean isProfile() throws NotesException {
		return getDelegate().isProfile();
	}

	public boolean isResponse() throws NotesException {
		return getDelegate().isResponse();
	}

	public boolean isSaveMessageOnSend() throws NotesException {
		return getDelegate().isSaveMessageOnSend();
	}

	public boolean isSentByAgent() throws NotesException {
		return getDelegate().isSentByAgent();
	}

	public boolean isSigned() throws NotesException {
		return getDelegate().isSigned();
	}

	public boolean isSignOnSend() throws NotesException {
		return getDelegate().isSignOnSend();
	}

	public boolean isValid() throws NotesException {
		final Document delegate = getDelegate(true);
		return null != delegate && delegate.isValid();
	}

	public boolean lock() throws NotesException {
		return getDelegate().lock();
	}

	public boolean lock(boolean arg0) throws NotesException {
		return getDelegate().lock(arg0);
	}

	public boolean lock(String arg0, boolean arg1) throws NotesException {
		return getDelegate().lock(arg0, arg1);
	}

	public boolean lock(String arg0) throws NotesException {
		return getDelegate().lock(arg0);
	}

	public boolean lock(Vector arg0, boolean arg1) throws NotesException {
		return getDelegate().lock(arg0, arg1);
	}

	public boolean lock(Vector arg0) throws NotesException {
		return getDelegate().lock(arg0);
	}

	public boolean lockProvisional() throws NotesException {
		return getDelegate().lockProvisional();
	}

	public boolean lockProvisional(String arg0) throws NotesException {
		return getDelegate().lockProvisional(arg0);
	}

	public boolean lockProvisional(Vector arg0) throws NotesException {
		return getDelegate().lockProvisional(arg0);
	}

	public void makeResponse(Document arg0) throws NotesException {
		getDelegate().makeResponse(arg0);
	}

	public void markRead() throws NotesException {
		getDelegate().markRead();
	}

	public void markRead(String arg0) throws NotesException {
		getDelegate().markRead(arg0);
	}

	public void markUnread() throws NotesException {
		getDelegate().markUnread();
	}

	public void markUnread(String arg0) throws NotesException {
		getDelegate().markUnread(arg0);
	}

	public void putInFolder(String arg0, boolean arg1) throws NotesException {
		getDelegate().putInFolder(arg0, arg1);
	}

	public void putInFolder(String arg0) throws NotesException {
		getDelegate().putInFolder(arg0);
	}

	public void recycle() throws NotesException {
		if (isValid() && !isDeleted()) {
			try {
				getDelegate(true).recycle();
			} finally {
				this.delegate = null;
			}
		}
	}

	public void recycle(Vector arg0) throws NotesException {
		if (isValid() && !isDeleted()) {
			try {
				getDelegate(true).recycle(arg0);
			} finally {
				this.delegate = null;
			}
		}
	}

	public boolean remove(boolean arg0) throws NotesException {
		try {
			return getDelegate().remove(arg0);
		} finally {
			this.delegate = null;
		}
	}

	public void removeFromFolder(String arg0) throws NotesException {
		getDelegate().removeFromFolder(arg0);
	}

	public void removeItem(String arg0) throws NotesException {
		getDelegate().removeItem(arg0);
	}

	public boolean removePermanently(boolean arg0) throws NotesException {
		return getDelegate().removePermanently(arg0);
	}

	public boolean renderToRTItem(RichTextItem arg0) throws NotesException {
		return getDelegate().renderToRTItem(arg0);
	}

	public Item replaceItemValue(String arg0, Object arg1)
			throws NotesException {
		return getDelegate().replaceItemValue(arg0, arg1);
	}

	public Item replaceItemValueCustomData(String arg0, Object arg1)
			throws IOException, NotesException {
		return getDelegate().replaceItemValueCustomData(arg0, arg1);
	}

	public Item replaceItemValueCustomData(String arg0, String arg1, Object arg2)
			throws IOException, NotesException {
		return getDelegate().replaceItemValueCustomData(arg0, arg1, arg2);
	}

	public Item replaceItemValueCustomDataBytes(String arg0, String arg1,
			byte[] arg2) throws IOException, NotesException {
		return getDelegate().replaceItemValueCustomDataBytes(arg0, arg1, arg2);
	}

	public boolean save() throws NotesException {
		return getDelegate().save();
	}

	public boolean save(boolean arg0, boolean arg1, boolean arg2)
			throws NotesException {
		return getDelegate().save(arg0, arg1, arg2);
	}

	public boolean save(boolean arg0, boolean arg1) throws NotesException {
		return getDelegate().save(arg0, arg1);
	}

	public boolean save(boolean arg0) throws NotesException {
		return getDelegate().save(arg0);
	}

	public void send() throws NotesException {
		getDelegate().send();
	}

	public void send(boolean arg0, String arg1) throws NotesException {
		getDelegate().send(arg0, arg1);
	}

	public void send(boolean arg0, Vector arg1) throws NotesException {
		getDelegate().send(arg0, arg1);
	}

	public void send(boolean arg0) throws NotesException {
		getDelegate().send(arg0);
	}

	public void send(String arg0) throws NotesException {
		getDelegate().send(arg0);
	}

	public void send(Vector arg0) throws NotesException {
		getDelegate().send(arg0);
	}

	public void setEncryptionKeys(Vector arg0) throws NotesException {
		getDelegate().setEncryptionKeys(arg0);
	}

	public void setEncryptOnSend(boolean arg0) throws NotesException {
		getDelegate().setEncryptOnSend(arg0);
	}

	public void setSaveMessageOnSend(boolean arg0) throws NotesException {
		getDelegate().setSaveMessageOnSend(arg0);
	}

	public void setSignOnSend(boolean arg0) throws NotesException {
		getDelegate().setSignOnSend(arg0);
	}

	public void setUniversalID(String arg0) throws NotesException {
		getDelegate().setUniversalID(arg0);
	}

	public void sign() throws NotesException {
		getDelegate().sign();
	}

	public void unlock() throws NotesException {
		getDelegate().unlock();
	}

}
