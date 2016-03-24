package de.ars.daojones.drivers.notes;

import java.util.Date;

import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import de.ars.daojones.FieldAccessException;
import de.ars.daojones.FieldAccessor;
import de.ars.daojones.UnsupportedFieldTypeException;
import de.ars.daojones.annotations.DataSourceType;
import de.ars.daojones.annotations.model.DataSourceInfo;
import de.ars.daojones.connections.ConnectionData;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.Identificator;

class NotesViewObject implements NotesDataObject,
        SerializableCallable<ViewEntry> {

    private static final long serialVersionUID = 4201043916399684891L;
    private final Reference<ViewEntry> viewEntry;
    private final Reference<View> view;
    private final ViewMetadata viewMetadata;
    private final NotesDocumentObject delegate;
    private final String unid;

    NotesViewObject(final ConnectionData connectionData,
            final DataSourceInfo dataSource, String applicationContextId,
            final Document doc) throws NotesException {
        super();
        this.view = new ThreadReference<View>(new SerializableCallable<View>() {
            private static final long serialVersionUID = 278212535647826791L;

            public View call() throws Exception {
                return NotesConnectorManager.getInstance().get(connectionData)
                        .getDatabase().getView(dataSource.getValue());
            }
        });
        this.viewEntry = new ThreadReference<ViewEntry>(this);
        this.viewMetadata = new ViewMetadata(getView());
        this.unid = doc.getUniversalID();
        this.delegate = new NotesDocumentObject(connectionData, doc,
                dataSource, applicationContextId);
    }

    protected String getViewName() {
        return getViewMetadata().getViewName();
    }

    protected ViewEntry getViewEntry() {
        return this.viewEntry.get();
    }

    protected View getView() {
        return this.view.get();
    }

    protected ViewMetadata getViewMetadata() {
        return this.viewMetadata;
    }

    protected NotesDocumentObject getDelegate() {
        return this.delegate;
    }

    /**
     * @see de.ars.daojones.runtime.DataObject#refresh()
     */
    public void refresh() throws DataAccessException {
        try {
            this.view.recycle();
            this.viewEntry.recycle();
            this.delegate.refresh();
        } catch (NotesException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * @see de.ars.daojones.drivers.notes.NotesDataObject#destroy()
     */
    public void destroy() throws NotesException {
        this.delegate.destroy();
    }

    /**
     * @see de.ars.daojones.runtime.DataObject#getLastModified()
     */
    public Date getLastModified() {
        return this.delegate.getLastModified();
    }

    /**
     * @see de.ars.daojones.runtime.DataObject#getFieldAccessor()
     */
    public FieldAccessor getFieldAccessor() {
        return new FieldAccessor() {
            private static final long serialVersionUID = 7916800701916807591L;

            public <U> U getFieldValue(String fieldName, Class<U> fieldType)
                    throws UnsupportedFieldTypeException, FieldAccessException {
                final Integer col = getViewMetadata().getColumnIndex(fieldName);
                if (null == col)
                    throw new FieldAccessException("The column \"" + fieldName
                            + "\" could not be found in the view \""
                            + getViewName() + "\"!", fieldName, fieldType);
                try {
                    final Object value = getViewEntry().getColumnValues().get(
                            col);
                    return TypeMapper.java2Java(value, fieldType);
                } catch (NotesException e) {
                    throw new FieldAccessException(e, fieldName, fieldType);
                }
            }

            public <U> void setFieldValue(String fieldName, Class<U> fieldType,
                    U value, boolean commit)
                    throws UnsupportedFieldTypeException, FieldAccessException {
                final FieldAccessor original = NotesViewObject.this.delegate
                        .getFieldAccessor();
                final ViewColumnMetadata column = getViewMetadata().getColumn(
                        fieldName);
                if (null == column)
                    throw new FieldAccessException("The column \"" + fieldName
                            + "\" could not be found in the view \""
                            + getViewName() + "\"!", fieldName, fieldType);
                if (column.isFormula())
                    throw new FieldAccessException(
                            "The column \""
                                    + fieldName
                                    + "\" in the view \""
                                    + getViewName()
                                    + "\" is calculated by a formula and cannot be modified!",
                            fieldName, fieldType);
                original.setFieldValue(column.getFormula(), fieldType, value,
                        commit);
            }
        };
    }

    /**
     * @see de.ars.daojones.runtime.DataObject#getIdentificator()
     */
    public Identificator getIdentificator() {
        return new NotesViewEntryIdentificator(getViewName(), getDelegate()
                .getUnid());
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((delegate == null) ? 0 : delegate.hashCode());
        result = prime * result + ((view == null) ? 0 : view.hashCode());
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
        NotesViewObject other = (NotesViewObject) obj;
        if (delegate == null) {
            if (other.delegate != null)
                return false;
        } else if (!delegate.equals(other.delegate))
            return false;
        if (view == null) {
            if (other.view != null)
                return false;
        } else if (!view.equals(other.view))
            return false;
        return true;
    }

    /**
     * @see de.ars.daojones.drivers.notes.NotesDataObject#getDataSourceInfo()
     */
    public DataSourceInfo getDataSourceInfo() {
        return new DataSourceInfo(DataSourceType.VIEW, getViewName());
    }

    /**
     * @see de.ars.daojones.drivers.notes.NotesDataObject#update()
     */
    public void update() throws NotesException, DataAccessException {
        getDelegate().update();
    }

    /**
     * @see java.util.concurrent.Callable#call()
     */
    public ViewEntry call() throws Exception {
        return ViewHelper.findEntryByUNID(getView(), this.unid);
    }

    /**
     * @see de.ars.daojones.drivers.notes.NotesDataObject#recycle()
     */
    public void recycle() throws NotesException {
        if (null != this.delegate)
            this.delegate.recycle();
        if (null != this.view)
            this.view.recycle();
        if (null != this.viewEntry)
            this.viewEntry.recycle();
    }

}
