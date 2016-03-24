package de.ars.daojones.drivers.notes;

import java.util.Date;

import lotus.domino.Document;
import lotus.domino.NotesException;
import de.ars.daojones.FieldAccessor;
import de.ars.daojones.annotations.model.DataSourceInfo;
import de.ars.daojones.connections.ConnectionData;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.Identificator;

class NotesDocumentObject implements NotesDataObject,
        SerializableCallable<Document> {

    private static final long serialVersionUID = 6781303681675096553L;
    private Reference<Document> document;
    private String unid;
    private Date lastModifiedDate;
    private final DataSourceInfo dataSourceInfo;
    private final ConnectionData connectionData;

    NotesDocumentObject(ConnectionData connectionData, Document document,
            DataSourceInfo dataSource, String applicationContextId)
            throws NotesException {
        super();
        this.connectionData = connectionData;
        this.dataSourceInfo = dataSource;
        this.accessor = new NotesFieldAccessor(this, applicationContextId);
        if (null != document)
            setDocument(document);
    }

    /**
     * Updates universal id and last modified date.
     * 
     * @throws NotesException
     */
    protected void updateDocumentInformation() throws NotesException {
        final Document doc = getDocument();
        boolean isValidDocument = null != doc && !doc.isNewNote()
                && !doc.isDeleted();
        try {
            try {
                if (isValidDocument) {
                    this.unid = doc.getUniversalID();
                    this.lastModifiedDate = doc.getLastModified().toJavaDate();
                }
            } catch (NotesException e) {
                isValidDocument = false;
                throw e;
            }
        } finally {
            if (!isValidDocument) {
                this.unid = null;
                this.lastModifiedDate = null;
            }
        }
    }

    public String getUnid() {
        return this.unid;
    }

    /**
     * @see de.ars.daojones.runtime.DataObject#getLastModified()
     */
    public Date getLastModified() {
        return this.lastModifiedDate;
    }

    /**
     * Returns a flag indicating whether the connection to the database is local
     * or not.
     * 
     * @return true, if the database is a local database
     */
    public boolean isLocal() {
        return getConnector().isLocal();
    }

    /**
     * Returns the {@link NotesConnector}.
     * 
     * @return the {@link NotesConnector}
     */
    protected NotesConnector getConnector() {
        return NotesConnectorManager.getInstance().get(connectionData);
    }

    /**
     * @see de.ars.daojones.drivers.notes.NotesDataObject#getDataSourceInfo()
     */
    public DataSourceInfo getDataSourceInfo() {
        return this.dataSourceInfo;
    }

    /**
     * Returns the {@link Document}.
     * 
     * @return the {@link Document}
     */
    Document getDocument() {
        return this.document.get();
    }

    /**
     * Sets the document to this data object. This will affect all threads.
     * 
     * @param doc
     * @throws NotesException
     */
    void setDocument(Document doc) throws NotesException {
        this.document = new ThreadReference<Document>(this, doc) {
            private static final long serialVersionUID = -5873116930187481552L;

            @Override
            protected boolean isValid(Document t) {
                try {
                    return super.isValid(t) && t.isValid() && !t.isDeleted();
                } catch (NotesException e) {
                    return false;
                }
            }
        };
        updateDocumentInformation();
    }

    /**
     * @see de.ars.daojones.runtime.DataObject#refresh()
     */
    public void refresh() throws DataAccessException {
        try {
            if (null != this.document)
                this.document.recycle();
        } catch (NotesException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * @see de.ars.daojones.drivers.notes.NotesDataObject#destroy()
     */
    public void destroy() throws NotesException {
        final Document doc = getDocument();
        if (null != doc)
            doc.remove(true);
        this.document.recycle();
        this.document = null;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((unid == null) ? 0 : unid.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final NotesDocumentObject other = (NotesDocumentObject) obj;
        if (unid == null) {
            return obj == this;
        } else if (!unid.equals(other.unid))
            return false;
        return true;
    }

    /**
     * @see de.ars.daojones.runtime.DataObject#getIdentificator()
     */
    public Identificator getIdentificator() {
        return new NotesDocumentIdentificator(unid);
    }

    private final FieldAccessor accessor;

    /**
     * @see de.ars.daojones.runtime.DataObject#getFieldAccessor()
     */
    public FieldAccessor getFieldAccessor() {
        return accessor;
    }

    private final SerializableCallable<Document> ORIGINAL_CALLABLE = new SerializableCallable<Document>() {
        private static final long serialVersionUID = -4883412674229438531L;

        public Document call() throws Exception {
            return null != getUnid() ? getConnector().getDatabase()
                    .getDocumentByUNID(getUnid()) : null;
        }
    };

    /**
     * @see java.util.concurrent.Callable#call()
     */
    public Document call() throws Exception {
        final Document result = ORIGINAL_CALLABLE.call();
        return null != result ? new RestorableDocument(ORIGINAL_CALLABLE,
                result) : null;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Notes DataObject with unid " + getUnid();
    }

    /**
     * @see de.ars.daojones.drivers.notes.NotesDataObject#update()
     */
    public void update() throws NotesException, DataAccessException {
        final Document doc = getDocument();
        if (null == doc)
            throw new DataAccessException(
                    "This data object does not have a reference to a Notes document!");
        doc.save(true, false);
        updateDocumentInformation();
    }

    /**
     * @see de.ars.daojones.drivers.notes.NotesDataObject#recycle()
     */
    public void recycle() throws NotesException {
        if (null != this.document)
            this.document.recycle();
    }

}
