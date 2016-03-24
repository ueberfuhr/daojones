package de.ars.daojones.drivers.notes;

import static de.ars.daojones.drivers.notes.LoggerConstants.DEBUG;
import static de.ars.daojones.drivers.notes.LoggerConstants.getLogger;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import de.ars.daojones.annotations.DataSourceType;
import de.ars.daojones.annotations.model.DataSourceInfo;
import de.ars.daojones.connections.AbstractAccessor;
import de.ars.daojones.connections.Accessor;
import de.ars.daojones.connections.AccessorHelper;
import de.ars.daojones.connections.Connection;
import de.ars.daojones.runtime.BeanCreationException;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.DataObject;
import de.ars.daojones.runtime.Identificator;
import de.ars.daojones.runtime.query.LogicalCombination;
import de.ars.daojones.runtime.query.LogicalSearchCriterion;
import de.ars.daojones.runtime.query.SearchCriterion;
import de.ars.daojones.runtime.query.TemplateManager;
import de.ars.daojones.runtime.query.VariableResolvingException;

/**
 * Notes driver implementation of an {@link Accessor}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 *          the DaoJones bean type
 */
class NotesAccessor<T extends Dao> extends AbstractAccessor<T> {

  private final NotesConnection<T> connection;

  /**
   * Creates an instance.
   * 
   * @param theGenericClass
   *          the DaoJones bean class
   * @param connection
   *          the {@link NotesConnection}
   */
  protected NotesAccessor( Class<T> theGenericClass,
      NotesConnection<T> connection ) {
    super( theGenericClass, connection.getContext() );
    this.connection = connection;
  }

  /**
   * Returns the {@link NotesConnection}.
   * 
   * @return the {@link NotesConnection}
   */
  protected NotesConnection<T> getNotesConnection() {
    return this.connection;
  }

  /**
   * Returns the {@link NotesConnector}.
   * 
   * @return the {@link NotesConnector}
   */
  protected NotesConnector getNotesConnector() {
    return getNotesConnection().getConnector();
  }

  /**
   * @see de.ars.daojones.events.DataAccessEventProvider#getConnection()
   */
  @Override
  public Connection<T> getConnection() {
    return getNotesConnection();
  }

  /**
   * @see de.ars.daojones.events.DataAccessEventProvider#doEventCloseConnection()
   */
  @Override
  public synchronized void doEventCloseConnection() throws DataAccessException {
    getNotesConnector().close();
  }

  /**
   * Returns the {@link Database}.
   * 
   * @return the {@link Database}
   * @throws NotesException
   */
  protected Database getDatabase() throws NotesException {
    return getNotesConnector().getDatabase();
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object o ) {
    return o.getClass().equals( this.getClass() );
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return 0;
  }

  /**
   * Creates a query and searches the database. There are multiple queries to be
   * built based on the datasources by class.
   * 
   * @param dataSources
   *          the datasources that are mapped to this connection.
   * @param maxCountOfResults
   *          the maximum count of results to be fetched
   * @param criterion
   *          the criterion
   * @return the collection of results
   * @throws DataAccessException
   *           if accessing the database failed
   */
  @Override
  protected Collection<T> findAll(
      java.util.Map<DataSourceInfo, Class<? extends T>> dataSources,
      int maxCountOfResults, SearchCriterion criterion )
      throws DataAccessException {
    final Set<T> result = new HashSet<T>();
    try {
      final Database db = getDatabase();
      final TemplateManager templateManager = getNotesConnection()
          .getTemplateManager();
      final QueryAssistant docAssi = new DocumentQueryAssistant(
          getNotesConnection().getConnectionData() );
      final QueryAssistant viewAssi = new ViewQueryAssistant(
          getNotesConnection().getConnectionData() );
      /*
       * Create one query per map entry
       */
      for ( Map.Entry<DataSourceInfo, Class<? extends T>> entry : dataSources
          .entrySet() ) {
        final DataSourceInfo ds = entry.getKey();
        final Class<? extends T> c = entry.getValue();
        // Assistance for VIEW and TABLE by using inheritance
        final QueryAssistant assi = DataSourceType.TABLE == ds.getType() ? docAssi
            : viewAssi;
        final SearchCriterion dsCrit = assi.createSearchCriterionForDataSource(
            ds, c );
        final SearchCriterion scQuery = null != dsCrit ? new LogicalSearchCriterion(
            dsCrit, LogicalCombination.AND, criterion )
            : criterion;
        final String query = new MessageFormat( templateManager
            .getTemplate( "query" ) ).format( new Object[] { scQuery.toQuery(
            templateManager, assi.createVariableResolver( ds, c ) ) } )

        ;
        getLogger().log( DEBUG,
            "Query to database " + db.getFileName() + ": " + query );
        final DocumentCollection col = db.search( query, null,
            maxCountOfResults > 0 ? maxCountOfResults
                + ( maxCountOfResults == Integer.MAX_VALUE ? 0 : 1 ) : 0 );
        try {
          getLogger().log(
              DEBUG,
              col.getCount() > maxCountOfResults ? maxCountOfResults
                  + " found, more entries existing but not read" : col
                  .getCount()
                  + " results found" );
          /*
           * Iterating through the whole collection might result in an
           * OutOfMemoryError.
           */
          for ( int i = 0; i < col.getCount()
              && result.size() < maxCountOfResults; i++ ) {
            final Document doc = col.getNthDocument( i + 1 );
            if ( !doc.isDeleted() && !doc.hasItem( "$Conflict" ) ) {
              result.add( create( c, assi.createDataObject( doc,
                  getNotesConnection().getDataSource(), c,
                  getApplicationContextId() ) ) );
            }
            // recycle document, if the collection is large
            if ( i > 20 )
              doc.recycle();
          }
          if ( result.size() >= maxCountOfResults )
            break;
        } finally {
          col.recycle();
        }
      }
    } catch ( NotesException e ) {
      throw new DataAccessException( e );
    } catch ( BeanCreationException e ) {
      throw new DataAccessException( e );
    } catch ( VariableResolvingException e ) {
      throw new DataAccessException( e );
    }
    return result;
  }

  /**
   * @see de.ars.daojones.connections.AbstractAccessor#findDataObjectById(de.ars.daojones.runtime.Identificator)
   */
  @Override
  protected DataObject findDataObjectById( Identificator id )
      throws DataAccessException {
    try {
      if ( null == id )
        return null;
      final Database db = getDatabase();
      final String unid = ( ( NotesDocumentIdentificator ) id ).getUnid();
      if ( null == id || "".equals( unid.trim() ) )
        return null;
      final Document doc = db.getDocumentByUNID( unid );
      if ( null == doc )
        return null;
      return createDataObject( super.theGenericClass, doc );
    } catch ( NotesException e ) {
      throw new DataAccessException( e );
    }
  }

  /**
   * This method is called during the update event, when a data object does not
   * exist.
   * 
   * @see de.ars.daojones.connections.AbstractAccessor#createDataObject(java.lang.Class)
   */
  @Override
  protected synchronized DataObject createDataObject( Class<T> c )
      throws DataAccessException {
    return createDataObject( c, null );
  }

  /**
   * Creates a data object. This method is called during the find event and the
   * update event, when a data object does not exist.
   * 
   * @param c
   *          the bean class
   * @param doc
   *          the {@link Document}, null during the update event
   * @return the new data object
   * @throws DataAccessException
   */
  protected synchronized DataObject createDataObject( Class<T> c, Document doc )
      throws DataAccessException {
    try {
      final DataSourceInfo dataSource = null != doc // && !doc.isNewNote()
          && doc.hasItem( "Form" ) ? new DataSourceInfo( DataSourceType.TABLE,
          doc.getItemValueString( "Form" ) ) : AccessorHelper.getDataSource( c );
      if ( null == dataSource )
        throw new DataAccessException(
            "There is no information about the data source for instances of "
                + c.getName() );
      final boolean isView = DataSourceType.VIEW.equals( dataSource.getType() );
      final QueryAssistant assi = isView ? new ViewQueryAssistant(
          getNotesConnection().getConnectionData() )
          : new DocumentQueryAssistant( getNotesConnection()
              .getConnectionData() );
      return assi.createDataObject( doc, dataSource, c,
          getApplicationContextId() );
    } catch ( NotesException e ) {
      throw new DataAccessException( e );
    }
  }

  /**
   * @see de.ars.daojones.connections.AbstractAccessor#doEventUpdate(de.ars.daojones.runtime.Dao)
   */
  @Override
  protected void doEventUpdate( Dao dao ) throws DataAccessException {
    try {
      // use created document, if new
      final NotesDataObject ndo = ( NotesDataObject ) dao.getDataObject();
      ndo.update();
    } catch ( NotesException e ) {
      throw new DataAccessException( e );
    }
  }

  /**
   * @see de.ars.daojones.connections.AbstractAccessor#getDataSource(de.ars.daojones.runtime.DataObject)
   */
  @Override
  protected DataSourceInfo getDataSource( DataObject dataObject )
      throws DataAccessException {
    return ( ( NotesDataObject ) dataObject ).getDataSourceInfo();
  }

  /**
   * @see de.ars.daojones.connections.AbstractAccessor#doEventDelete(de.ars.daojones.runtime.Dao)
   */
  @Override
  protected void doEventDelete( T t ) throws DataAccessException {
    try {
      if ( !t.isNew() ) {
        ( ( NotesDataObject ) t.getDataObject() ).destroy();
      }
      t.setDataObject( null );
    } catch ( NotesException e ) {
      throw new DataAccessException( e );
    }
  }

}
