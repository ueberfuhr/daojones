package de.ars.daojones.internal.drivers.notes.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewColumn;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;
import de.ars.daojones.drivers.notes.DataHandlerProvider;
import de.ars.daojones.drivers.notes.DocumentIdentificator;
import de.ars.daojones.drivers.notes.NotesIdentificator;
import de.ars.daojones.drivers.notes.ViewEntryIdentificator;
import de.ars.daojones.drivers.notes.xt.NotesEventHandler;
import de.ars.daojones.drivers.notes.xt.NotesEventHandler.FTSearchConfiguration;
import de.ars.daojones.internal.drivers.notes.DocumentDatabaseEntry.NotesEventHandlerProvider;
import de.ars.daojones.internal.drivers.notes.Identificators;
import de.ars.daojones.internal.drivers.notes.NotesViewHelper;
import de.ars.daojones.internal.drivers.notes.ViewDatabaseEntry;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping.DataSourceType;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.database.CompositeSearchResult;
import de.ars.daojones.runtime.spi.database.ConnectionContext;
import de.ars.daojones.runtime.spi.database.DatabaseEntry;

public class ViewQueryAssistant extends AbstractQueryAssistant {

  private static final Messages logger = Messages.create( "search.assistants.views" );
  private static final Pattern columns = Pattern.compile( Pattern.quote( "[" ) + "\\s*\\S+\\s*" + Pattern.quote( "]" ) );

  @Override
  public DataSourceType getDatasourceType() {
    return DataSourceType.VIEW;
  }

  protected View getView( final Database db, final String name ) throws NotesException, ViewNotFoundException {
    final View view = db.getView( name );
    if ( null == view ) {
      throw new ViewNotFoundException( name );
    } else {
      return view;
    }
  }

  @SuppressWarnings( "unchecked" )
  @Override
  protected SearchResult<DatabaseEntry> executeQueries( final QueryContext context,
          final Map<BeanModel, String> queries, final Map<String, BeanModel> beanModelByDatasourceName )
          throws NotesException, DataAccessException {
    final List<SearchResult<DatabaseEntry>> results = new ArrayList<SearchResult<DatabaseEntry>>( queries.size() );
    // One query per view name 
    // One view name is unique, there are not multiple bean models per view name
    for ( final Map.Entry<BeanModel, String> entry : queries.entrySet() ) {
      final BeanModel beanModel = entry.getKey();
      final String viewName = beanModel.getBean().getTypeMapping().getName();
      final View view = getView( context.getDatabase(), viewName );
      // Parse query for [ColumnNames] and replace them by the mapped document fields in the view
      final Matcher matcher = ViewQueryAssistant.columns.matcher( entry.getValue() );
      final StringBuffer sbQuery = new StringBuffer();
      while ( matcher.find() ) {
        final String columnName = matcher.group(); // [?1] or [name]
        final String columnNameTrimmed = columnName.substring( 1, columnName.length() - 1 ).trim();
        try {
          final ViewColumn column = NotesViewHelper.findColumn( view, columnNameTrimmed );
          final String fieldName;
          if ( null != column ) {
            if ( column.isField() ) {
              fieldName = column.getItemName();
            } else {
              throw new ConfigurationException( ViewQueryAssistant.logger.get( "error.column.formula",
                      columnNameTrimmed, viewName ) );
            }
          } else {
            ViewQueryAssistant.logger.log( Level.WARNING, "warn.column.notfound", columnNameTrimmed, viewName );
            fieldName = columnNameTrimmed;
          }
          matcher.appendReplacement( sbQuery, "[".concat( fieldName ).concat( "]" ) );
        } catch ( final ConfigurationException e ) {
          throw new DataAccessException( e );
        }
      }
      matcher.appendTail( sbQuery );
      final String query = sbQuery.toString();
      /* ***************
       * Execute Query *
       *************** */
      // Clear full text search
      view.clear();
      final NotesEventHandler[] handlers = context.getNotesEventHandlerProvider().getNotesEventHandlers();
      try {
        ViewQueryAssistant.logger.log( loggingLevel, "query", query, context.getDatabase().getFilePath(), context
                .getDatabase().getParent().getUserName(), view.getName() );
        final long timestampBegin = System.currentTimeMillis();
        // Invoke Notes event handlers
        final NotesEventHandler.NotesEventHandlerContext<View> handlerContext = new NotesEventHandler.NotesEventHandlerContext<View>() {

          @Override
          public View getSource() throws NotesException {
            return view;
          }

          @Override
          public Database getDatabase() throws NotesException {
            return view.getParent();
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
        // FTSearch will result in only document entries (no summaries, categories and so on)
        final int count = view.FTSearch( query, context.getQuery().getMaxCountOfResults() );

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

        // Create result set
        final ViewEntryCollection entries = view.getAllEntries();
        final long timestampEnd = System.currentTimeMillis();
        ViewQueryAssistant.logger.log( loggingLevel, "query.time", timestampEnd - timestampBegin, entries.getCount() );
        results.add( new ViewEntryCollectionResultImpl( entries, this, beanModel, view, context
                .getNotesEventHandlerProvider() ) );
      } finally {
        // Clear full text search
        view.clear();
      }

    }
    return new CompositeSearchResult<DatabaseEntry>( context.getQuery().getMaxCountOfResults(),
            results.toArray( new SearchResult[results.size()] ) );

  }

  private static class ViewEntryCollectionResultImpl extends AbstractSearchResultImpl<ViewEntryCollection> {

    private static final int ITERATOR_THRESHOLD = 2;
    // if there is one iterator, getNextDocument can be used, otherwise, getNthDocument, which might be less performant, but thread-safe
    private transient int iteratorCount = 0;
    private final View view;
    private final BeanModel beanModel;
    private final NotesEventHandlerProvider notesEventHandlerProvider;

    public ViewEntryCollectionResultImpl( final ViewEntryCollection delegate,
            final DataHandlerProvider dataHandlerProvider, final BeanModel beanModel, final View view,
            final NotesEventHandlerProvider notesEventHandlerProvider ) throws NotesException {
      super( delegate, dataHandlerProvider );
      this.beanModel = beanModel;
      this.view = view;
      this.notesEventHandlerProvider = notesEventHandlerProvider;
    }

    @Override
    protected int calculateSize() throws NotesException {
      return getDelegate().getCount();
    }

    @Override
    public Iterator<DatabaseEntry> iterator() {
      iteratorCount = Math.min( ViewEntryCollectionResultImpl.ITERATOR_THRESHOLD, iteratorCount + 1 );
      return new Iterator<DatabaseEntry>() {

        private int position = 0;

        @Override
        public boolean hasNext() {
          return position < size();
        }

        @Override
        public DatabaseEntry next() {
          try {
            final ViewEntry entry;
            if ( iteratorCount < ViewEntryCollectionResultImpl.ITERATOR_THRESHOLD ) {
              entry = position == 0 ? getDelegate().getFirstEntry() : getDelegate().getNextEntry();
            } else {
              entry = getDelegate().getNthEntry( position + 1 );
            }
            registerForRecycling( entry );
            return new ViewDatabaseEntry( view, entry, beanModel, getDataHandlerProvider(), notesEventHandlerProvider );
          } catch ( final NotesException e ) {
            ViewQueryAssistant.logger.log( Level.SEVERE, e, "error.document.collection.notes" );
            return null;
          } finally {
            position++;
          }
        }

        @Override
        public void remove() {
          throw new UnsupportedOperationException( ViewQueryAssistant.logger.get( "error.viewentry.collection.remove" ) );
        }
      };
    }

  }

  @Override
  public DatabaseEntry findById( final ConnectionContext<?> connectionContext, final FindByIdContext context,
          final NotesIdentificator identificator ) throws DataAccessException, NotesException, ConfigurationException {
    final Database database = context.getDatabase();
    final Session session = database.getParent();
    final View view;
    final ViewEntry entry;
    switch ( identificator.getElementType() ) {
    case VIEW_ENTRY:
      entry = Identificators.findReference( session, database, ( ViewEntryIdentificator ) identificator );
      view = Identificators.findReference( session, database, ( ( ViewEntryIdentificator ) identificator ).getView() );
      break;
    case DOCUMENT:
      final String viewName = connectionContext.getBeanModel().getBean().getTypeMapping().getName();
      view = getView( database, viewName );
      final Document doc = Identificators.findReference( session, database, ( DocumentIdentificator ) identificator );
      entry = NotesViewHelper.findViewEntry( view, doc );
      break;
    default:
      throw new DataAccessException( ViewQueryAssistant.logger.get( "error.viewentry.identificator.invalid",
              identificator.getElementType().name().toLowerCase() ) );
    }
    return new ViewDatabaseEntry( view, entry, connectionContext.getBeanModel(), this,
            context.getNotesEventHandlerProvider() );
  }
}
