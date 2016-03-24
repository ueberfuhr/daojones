package de.ars.daojones.internal.drivers.notes.search;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.Session;
import de.ars.daojones.drivers.notes.DataHandlerProvider;
import de.ars.daojones.drivers.notes.DocumentIdentificator;
import de.ars.daojones.drivers.notes.NotesIdentificator;
import de.ars.daojones.drivers.notes.ViewEntryIdentificator;
import de.ars.daojones.drivers.notes.search.SearchType;
import de.ars.daojones.drivers.notes.xt.NotesEventHandler;
import de.ars.daojones.drivers.notes.xt.NotesEventHandler.FTSearchConfiguration;
import de.ars.daojones.internal.drivers.notes.DocumentDatabaseEntry;
import de.ars.daojones.internal.drivers.notes.DocumentDatabaseEntry.NotesEventHandlerProvider;
import de.ars.daojones.internal.drivers.notes.Identificators;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping.DataSourceType;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.database.AbstractSearchResult;
import de.ars.daojones.runtime.spi.database.ConnectionContext;
import de.ars.daojones.runtime.spi.database.DatabaseEntry;

public class DocumentQueryAssistant extends AbstractQueryAssistant {

  private static final Messages templates = Messages.create( "search.builders.templates" );
  private static final Messages logger = Messages.create( "search.assistants.documents" );

  @Override
  public DataSourceType getDatasourceType() {
    return DataSourceType.TABLE;
  }

  @SuppressWarnings( "unchecked" )
  @Override
  protected SearchResult<DatabaseEntry> executeQueries( final QueryContext context,
          final Map<BeanModel, String> queries, final Map<String, BeanModel> beanModelByDatasourceName )
          throws NotesException, DataAccessException {
    // Build single query
    String query = null;
    final SearchType searchType = SearchTypeHelper.getSearchType( context.getQuery(), DataSourceType.TABLE );
    final String keyPrefix = searchType.getValue() + ".";
    for ( final Map.Entry<BeanModel, String> entry : queries.entrySet() ) {
      final DatabaseTypeMapping typeMapping = entry.getKey().getBean().getTypeMapping();
      final String formName = typeMapping.getName();
      final String formQuery = DocumentQueryAssistant.templates.get( keyPrefix + "form.datasource", formName,
              entry.getValue() );
      query = null == query ? formQuery : DocumentQueryAssistant.templates.get( keyPrefix + "form.query.separator",
              query, formQuery );
    }
    if ( null == query ) {
      return AbstractSearchResult.EMPTY_SEARCH_RESULT;
    } else {
      query = DocumentQueryAssistant.templates.get( keyPrefix + "form.query.template", query );
    }
    /* ***************
     * Execute Query *
     *************** */
    final Database db = context.getDatabase();
    DocumentQueryAssistant.logger.log( loggingLevel, keyPrefix + "query", query, context.getDatabase().getFilePath(),
            context.getDatabase().getParent().getUserName() );
    final long timestampBegin = System.currentTimeMillis();
    final DocumentCollection col;
    switch ( searchType ) {
    case FORMULA:
      col = db.search( query, null, context.getQuery().getMaxCountOfResults() );
      break;
    case FT_SEARCH:
      final NotesEventHandler[] handlers = context.getNotesEventHandlerProvider().getNotesEventHandlers();
      final NotesEventHandler.NotesEventHandlerContext<Database> handlerContext = new NotesEventHandler.NotesEventHandlerContext<Database>() {

        @Override
        public Database getSource() throws NotesException {
          return db;
        }

        @Override
        public Database getDatabase() throws NotesException {
          return db;
        }
      };
      final NotesEventHandler.FTSearchConfiguration handlerConfig = new NotesEventHandler.FTSearchConfiguration();
      for ( final NotesEventHandler handler : handlers ) {
        try {
          handler.beforeFTSearch( handlerContext, handlerConfig );
        } catch ( final ConfigurationException e ) {
          throw new DataAccessException( e );
        }
      }
      col = db.FTSearch( query, context.getQuery().getMaxCountOfResults() );
      final int count = col.getCount();
      final NotesEventHandler.FTSearchResult handlerResult = new NotesEventHandler.FTSearchResult() {
        @Override
        public FTSearchConfiguration getConfiguration() {
          return handlerConfig;
        }

        @Override
        public int getCount() {
          return count;
        }

      };
      // Invoke Notes event handlers
      for ( final NotesEventHandler handler : handlers ) {
        handler.afterFTSearch( handlerContext, handlerResult );
      }
      break;
    default:
      throw new DataAccessException( new UnsupportedOperationException( DocumentQueryAssistant.logger.get(
              "error.searchtype.invalid", searchType.name() ) ) );
    }
    final long timestampEnd = System.currentTimeMillis();
    DocumentQueryAssistant.logger.log( loggingLevel, keyPrefix + "query.time", timestampEnd - timestampBegin,
            col.getCount() );

    /* ***************
     *     Done.     *
     *************** */
    return new DocumentCollectionResultImpl( col, this, beanModelByDatasourceName,
            context.getNotesEventHandlerProvider() );

  }

  private class DocumentCollectionResultImpl extends AbstractSearchResultImpl<DocumentCollection> {

    private static final int ITERATOR_THRESHOLD = 2;
    // if there is one iterator, getNextDocument can be used, otherwise, getNthDocument, which might be less performant, but thread-safe
    private transient int iteratorCount = 0;
    private final Map<String, BeanModel> beanModelByDatasourceName;
    private final NotesEventHandlerProvider notesEventHandlerProvider;

    public DocumentCollectionResultImpl( final DocumentCollection delegate,
            final DataHandlerProvider dataHandlerProvider, final Map<String, BeanModel> beanModelByDatasourceName,
            final NotesEventHandlerProvider notesEventHandlerProvider ) throws NotesException {
      super( delegate, dataHandlerProvider );
      this.beanModelByDatasourceName = beanModelByDatasourceName;
      this.notesEventHandlerProvider = notesEventHandlerProvider;
    }

    @Override
    protected int calculateSize() throws NotesException {
      return getDelegate().getCount();
    }

    @Override
    public Iterator<DatabaseEntry> iterator() {
      iteratorCount = Math.min( DocumentCollectionResultImpl.ITERATOR_THRESHOLD, iteratorCount + 1 );
      return new Iterator<DatabaseEntry>() {

        private int position = 0;

        @Override
        public boolean hasNext() {
          return position < size();
        }

        @Override
        public DatabaseEntry next() {
          try {
            final Document document;
            if ( iteratorCount < DocumentCollectionResultImpl.ITERATOR_THRESHOLD ) {
              document = position == 0 ? getDelegate().getFirstDocument() : getDelegate().getNextDocument();
            } else {
              document = getDelegate().getNthDocument( position + 1 );
            }
            registerForRecycling( document );
            final String form = getDatasource( document );
            final BeanModel beanModel = beanModelByDatasourceName.get( form );
            return new DocumentDatabaseEntry( document, beanModel, getDataHandlerProvider(), notesEventHandlerProvider );
          } catch ( final NotesException e ) {
            DocumentQueryAssistant.logger.log( Level.SEVERE, e, "error.document.collection.notes" );
            return null;
          } catch ( final ConfigurationException e ) {
            DocumentQueryAssistant.logger.log( Level.SEVERE, e, "error.document.collection.notes" );
            return null;
          } finally {
            position++;
          }
        }

        @Override
        public void remove() {
          throw new UnsupportedOperationException(
                  DocumentQueryAssistant.logger.get( "error.document.collection.remove" ) );
        }
      };
    }

  }

  protected String getDatasource( final Document doc ) throws ConfigurationException, NotesException {
    return doc.getItemValueString( "Form" );
  }

  @Override
  public DatabaseEntry findById( final ConnectionContext<?> connectionContext, final FindByIdContext context,
          final NotesIdentificator identificator ) throws DataAccessException, NotesException, ConfigurationException {
    try {
      final Database database = context.getDatabase();
      final Session session = database.getParent();
      final DocumentIdentificator docId;
      switch ( identificator.getElementType() ) {
      case DOCUMENT:
        docId = ( DocumentIdentificator ) identificator;
        break;
      case VIEW_ENTRY:
        docId = ( ( ViewEntryIdentificator ) identificator ).toDocumentIdentificator();
        break;
      default:
        throw new DataAccessException( DocumentQueryAssistant.logger.get( "error.document.identificator.invalid",
                identificator.getElementType().name().toLowerCase() ) );
      }
      DocumentQueryAssistant.logger.log( loggingLevel, "findById", docId.getUniversalId(), context.getDatabase()
              .getFilePath(), context.getDatabase().getParent().getUserName() );
      final long timestampBegin = System.currentTimeMillis();
      final Document doc = Identificators.findReference( session, database, docId );
      final long timestampEnd = System.currentTimeMillis();
      DocumentQueryAssistant.logger.log( loggingLevel, "findById.time", timestampEnd - timestampBegin, null != doc ? 1
              : 0, null != doc ? doc.getItemValueString( "Form" ) : "-", null != doc ? doc.getUniversalID() : "-" );

      final BeanModel beanModel = context.get( getDatasource( doc ), DataSourceType.TABLE );
      return new DocumentDatabaseEntry( doc, beanModel, this, context.getNotesEventHandlerProvider() );
    } catch ( final IOException e ) {
      throw new DataAccessException( e );
    }
  }
}
