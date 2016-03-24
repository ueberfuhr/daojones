package de.ars.daojones.internal.drivers.notes.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import lotus.domino.Base;
import lotus.domino.NotesException;
import de.ars.daojones.drivers.notes.DataHandler;
import de.ars.daojones.drivers.notes.DataHandlerNotFoundException;
import de.ars.daojones.drivers.notes.DataHandlerProvider;
import de.ars.daojones.drivers.notes.search.QueryLanguage;
import de.ars.daojones.drivers.notes.search.QueryLanguageException;
import de.ars.daojones.internal.drivers.notes.DataHandlerProviderManager;
import de.ars.daojones.internal.drivers.notes.utilities.DriverSystem;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.database.AbstractSearchResult;
import de.ars.daojones.runtime.spi.database.ConnectionContext;
import de.ars.daojones.runtime.spi.database.DatabaseEntry;

/**
 * An abstract implementation for query assistants.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public abstract class AbstractQueryAssistant implements QueryAssistant, DataHandlerProvider {

  private static final Messages templates = Messages.create( "search.builders.templates" );
  private static final Messages logger = Messages.create( "search.assistants.common" );

  protected final Level loggingLevel = DriverSystem.isDebugging() ? Level.INFO : Level.FINER;
  private final DataHandlerProvider delegate = DataHandlerProviderManager.getInstance();

  @Override
  public final Class<? extends Void> getKeyType() {
    return Void.TYPE;
  }

  @Override
  public <T> DataHandler<T> findProvider( final Class<T> c ) throws DataHandlerNotFoundException {
    return delegate.findProvider( c );
  }

  protected Messages getTemplates() {
    return AbstractQueryAssistant.templates;
  }

  @Override
  public SearchResult<DatabaseEntry> executeQuery( final ConnectionContext<?> connectionContext,
          final QueryContext context ) throws DataAccessException, NotesException {
    final BeanModel[] beanModels = context.getBeans();
    final QueryLanguage ql = context.getQueryLanguage();
    // "view" or "table" as key within resource bundles
    final String dsKey;
    switch ( getDatasourceType() ) {
    case TABLE:
      dsKey = "form";
      break;
    case VIEW:
      dsKey = "view";
      break;
    default:
      throw new UnsupportedOperationException( getDatasourceType().name() );
    }
    try {

      /* **********************************
       * Prepare Query (Formula Language) *
       ********************************** */
      if ( null == beanModels || beanModels.length == 0 ) {
        throw new DataAccessException( AbstractQueryAssistant.logger.get( "error.datasource.none", context.getQuery()
                .getCriterion() ) );
      } else {
        final Map<String, BeanModel> beanModelByDatasourceName = new HashMap<String, BeanModel>();
        final Map<BeanModel, String> queries = new HashMap<BeanModel, String>();
        for ( final BeanModel beanModel : beanModels ) {
          final Bean bean = beanModel.getBean();
          final String datasourceName = bean.getTypeMapping().getName();
          if ( null != beanModelByDatasourceName.put( datasourceName, beanModel ) ) {
            throw new DataAccessException( AbstractQueryAssistant.logger.get( "error.datasource.duplicate",
                    datasourceName, dsKey ) );
          }
          final StringBuilder sb = new StringBuilder();
          ql.createQuery( sb, context.getQuery(), bean );
          queries.put( beanModel, sb.toString() );
        }
        return executeQueries( context, queries, beanModelByDatasourceName );
      }

    } catch ( final QueryLanguageException e ) {
      throw new DataAccessException( e );
    }
  }

  protected abstract SearchResult<DatabaseEntry> executeQueries( QueryContext context, Map<BeanModel, String> queries,
          Map<String, BeanModel> beanModelByDatasourceName ) throws NotesException, DataAccessException;

  protected static abstract class AbstractSearchResultImpl<T extends Base> extends AbstractSearchResult<DatabaseEntry> {

    private final T delegate;
    private final DataHandlerProvider dataHandlerProvider;
    private final int size;
    private final Set<Base> basesToRecycle = new HashSet<Base>();

    public AbstractSearchResultImpl( final T delegate, final DataHandlerProvider dataHandlerProvider )
            throws NotesException {
      super();
      this.delegate = delegate;
      this.dataHandlerProvider = dataHandlerProvider;
      size = calculateSize();
    }

    protected abstract int calculateSize() throws NotesException;

    protected T getDelegate() {
      return delegate;
    }

    protected <B extends Base> B registerForRecycling( final B base ) {
      basesToRecycle.add( base );
      return base;
    }

    protected DataHandlerProvider getDataHandlerProvider() {
      return dataHandlerProvider;
    }

    @Override
    public int size() {
      return size;
    }

    @Override
    public void close() throws DataAccessException {
      try {
        delegate.recycle();
        for ( final Iterator<Base> it = basesToRecycle.iterator(); it.hasNext(); ) {
          final Base child = it.next();
          child.recycle();
          it.remove();
        }
      } catch ( final NotesException e ) {
        throw new DataAccessException( AbstractQueryAssistant.logger.get( "error.datasource.result.recycle" ), e );
      }
    }

  }

}
