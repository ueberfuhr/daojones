package de.ars.daojones.drivers.notes;

import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;
import de.ars.daojones.annotations.DataSourceType;
import de.ars.daojones.annotations.model.DataSourceInfo;
import de.ars.daojones.connections.ConnectionData;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.query.SearchCriterion;
import de.ars.daojones.runtime.query.TemplateManager;
import de.ars.daojones.runtime.query.VariableResolver;
import de.ars.daojones.runtime.query.VariableResolvingException;

/**
 * An implementation of {@link QueryAssistant} providing assistance for
 * searching documents within a view.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class ViewQueryAssistant extends AbstractQueryAssistant {

    public ViewQueryAssistant(ConnectionData connectionData) {
        super(connectionData);
    }

    /**
     * Returns the {@link View}.
     * 
     * @param name
     *            the name of the view
     * 
     * @return the {@link View}
     * @throws DataAccessException
     */
    protected View getView(String name) throws DataAccessException {
        try {
            return getNotesConnector().getDatabase().getView(name);
        } catch (NotesException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * Returns the {@link View}.
     * 
     * @param ds
     *            the {@link DataSourceInfo}
     * 
     * @return the {@link View}
     * @throws DataAccessException
     */
    protected View getView(DataSourceInfo ds) throws DataAccessException {
        return DataSourceType.VIEW.equals(ds.getType()) ? getView(ds.getValue())
                : null;
    }

    /**
     * @see de.ars.daojones.drivers.notes.QueryAssistant#createSearchCriterionForDataSource(DataSourceInfo,
     *      Class)
     */
    public SearchCriterion createSearchCriterionForDataSource(
            final DataSourceInfo ds, Class<? extends Dao> theGenericClass)
            throws DataAccessException {
        return new SearchCriterion() {
            private static final long serialVersionUID = -1769096311309632229L;

            // TODO Java6-Migration
            // @Override
            public String toQuery(TemplateManager templateManager,
                    VariableResolver resolver)
                    throws VariableResolvingException {
                try {
                    String result = getView(ds).getSelectionFormula();
                    if (null == result)
                        return null;
                    result = result.trim();
                    if (result.toUpperCase().startsWith("SELECT".toUpperCase()))
                        result = result.substring("SELECT".length());
                    return result.trim();
                } catch (NotesException e) {
                    throw new VariableResolvingException(e);
                } catch (DataAccessException e) {
                    throw new VariableResolvingException(e);
                }
            }
        };
    }

    /**
     * @see de.ars.daojones.drivers.notes.QueryAssistant#createVariableResolver(DataSourceInfo,
     *      Class)
     */
    public VariableResolver createVariableResolver(DataSourceInfo ds,
            Class<? extends Dao> theGenericClass) throws DataAccessException {
        return new ViewVariableResolver(theGenericClass, getView(ds.getValue()));
    }

    /**
     * @see de.ars.daojones.drivers.notes.QueryAssistant#createDataObject(Document,
     *      DataSourceInfo, Class, String)
     */
    public NotesDataObject createDataObject(final Document doc,
            final DataSourceInfo ds,
            final Class<? extends Dao> theGenericClass,
            final String applicationContextId) throws DataAccessException {
        try {
            if (null == doc || doc.isNewNote())
                throw new DataAccessException(
                        "Creating entries for views is not supported!");
            return new NotesViewObject(getConnectionData(), ds,
                    applicationContextId, doc);
        } catch (NotesException e) {
            throw new DataAccessException(e);
        }
    }

}
