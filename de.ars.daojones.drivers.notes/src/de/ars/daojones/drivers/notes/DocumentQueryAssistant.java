package de.ars.daojones.drivers.notes;

import lotus.domino.Document;
import lotus.domino.NotesException;
import de.ars.daojones.annotations.model.DataSourceInfo;
import de.ars.daojones.connections.ConnectionData;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.query.PropertySearchCriterion;
import de.ars.daojones.runtime.query.SearchCriterion;
import de.ars.daojones.runtime.query.StringComparison;
import de.ars.daojones.runtime.query.VariableResolver;

/**
 * An implementation of {@link QueryAssistant} providing assistance for
 * searching documents in the database.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class DocumentQueryAssistant extends AbstractQueryAssistant {

    public DocumentQueryAssistant(ConnectionData connectionData) {
        super(connectionData);
    }

    /**
     * @see de.ars.daojones.drivers.notes.QueryAssistant#createSearchCriterionForDataSource(DataSourceInfo,
     *      Class)
     */
    public SearchCriterion createSearchCriterionForDataSource(
            DataSourceInfo ds, Class<? extends Dao> theGenericClass)
            throws DataAccessException {
        return new PropertySearchCriterion<String>("Form",
                StringComparison.EQUALS, ds.getValue(), false);
    }

    /**
     * @see de.ars.daojones.drivers.notes.QueryAssistant#createVariableResolver(DataSourceInfo,
     *      Class)
     */
    public VariableResolver createVariableResolver(DataSourceInfo ds,
            Class<? extends Dao> theGenericClass) throws DataAccessException {
        return new TableVariableResolver(theGenericClass);
    }

    /**
     * @see de.ars.daojones.drivers.notes.QueryAssistant#createDataObject(Document,
     *      DataSourceInfo, Class, String)
     */
    public NotesDataObject createDataObject(Document doc, DataSourceInfo ds,
            Class<? extends Dao> theGenericClass, String applicationContextId)
            throws DataAccessException {
        try {
            if (null == doc) {
                doc = getNotesConnector().getDatabase().createDocument();
                doc.appendItemValue("Form", ds.getValue());
            }
            return new NotesDocumentObject(getConnectionData(), doc, ds,
                    applicationContextId);
        } catch (NotesException e) {
            throw new DataAccessException(e);
        }
    }

}
