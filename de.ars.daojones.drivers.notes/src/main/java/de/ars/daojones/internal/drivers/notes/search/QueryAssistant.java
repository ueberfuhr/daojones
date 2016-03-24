package de.ars.daojones.internal.drivers.notes.search;

import lotus.domino.Database;
import lotus.domino.NotesException;
import de.ars.daojones.drivers.notes.NotesIdentificator;
import de.ars.daojones.drivers.notes.search.DataSourceTypeAware;
import de.ars.daojones.drivers.notes.search.QueryLanguage;
import de.ars.daojones.internal.drivers.notes.DocumentDatabaseEntry.NotesEventHandlerProvider;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping.DataSourceType;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.spi.database.ConnectionContext;
import de.ars.daojones.runtime.spi.database.DatabaseEntry;

/**
 * An object that executes the query for a given data source type.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface QueryAssistant extends DataSourceTypeAware<Void> {

  /**
   * Executes the query.
   * 
   * @param connectionContext
   *          the connection context
   * @param context
   *          the context
   * @throws DataAccessException
   * @throws NotesException
   */
  SearchResult<DatabaseEntry> executeQuery( ConnectionContext<?> connectionContext, QueryContext context )
          throws DataAccessException, NotesException;

  /**
   * Finds an entry by its id.
   * 
   * @param connectionContext
   *          the connection context
   * @param context
   *          the context
   * @param identificator
   *          the identificator
   * @return the entry or <tt>null</tt>, if not found
   * @throws DataAccessException
   * @throws NotesException
   * @throws ConfigurationException
   */
  DatabaseEntry findById( ConnectionContext<?> connectionContext, FindByIdContext context,
          NotesIdentificator identificator ) throws DataAccessException, NotesException, ConfigurationException;

  /**
   * The context of a query.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  interface QueryContext {

    /**
     * Returns the document handler provider.
     * 
     * @return the document handler provider
     */
    NotesEventHandlerProvider getNotesEventHandlerProvider();

    /**
     * Returns the bean models.
     * 
     * @return the bean models
     */
    BeanModel[] getBeans();

    /**
     * Returns the query language.
     * 
     * @return the query language
     */
    QueryLanguage getQueryLanguage();

    /**
     * Returns the query.
     * 
     * @return the query
     */
    Query getQuery();

    /**
     * Returns the database.
     * 
     * @return the database
     */
    Database getDatabase();

  }

  /**
   * Context of query by id.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  interface FindByIdContext {
    /**
     * Finds a bean model by datasource name and type
     * 
     * @param datasource
     *          the datasource name
     * @param dsType
     *          the datasource type
     * @return the bean model
     * @throws ConfigurationException
     *           if no bean model could be found
     */
    BeanModel get( String datasource, DataSourceType dsType ) throws ConfigurationException;

    /**
     * Returns the database.
     * 
     * @return the database
     */
    Database getDatabase();

    /**
     * Returns the document handler provider.
     * 
     * @return the document handler provider
     */
    NotesEventHandlerProvider getNotesEventHandlerProvider();
  }

}
