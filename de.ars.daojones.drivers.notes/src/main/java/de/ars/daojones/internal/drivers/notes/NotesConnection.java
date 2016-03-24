package de.ars.daojones.internal.drivers.notes;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import de.ars.daojones.drivers.notes.DataHandlerProvider;
import de.ars.daojones.drivers.notes.DatabaseIdentificator;
import de.ars.daojones.drivers.notes.DocumentIdentificator;
import de.ars.daojones.drivers.notes.NotesConnectionFactory;
import de.ars.daojones.drivers.notes.NotesDatabasePath;
import de.ars.daojones.drivers.notes.NotesIdentificator;
import de.ars.daojones.drivers.notes.ViewEntryIdentificator;
import de.ars.daojones.drivers.notes.ViewIdentificator;
import de.ars.daojones.drivers.notes.search.QueryLanguage;
import de.ars.daojones.drivers.notes.search.QueryLanguageFactory;
import de.ars.daojones.drivers.notes.xt.NotesEventHandler;
import de.ars.daojones.internal.drivers.notes.DocumentDatabaseEntry.NotesEventHandlerProvider;
import de.ars.daojones.internal.drivers.notes.search.DataSourceTypeAwareRegistry;
import de.ars.daojones.internal.drivers.notes.search.QueryAssistant;
import de.ars.daojones.internal.drivers.notes.search.QueryAssistant.FindByIdContext;
import de.ars.daojones.internal.drivers.notes.search.QueryAssistant.QueryContext;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.configuration.beans.Bean;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping.DataSourceType;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.ConnectionMetaData;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.spi.database.CompositeSearchResult;
import de.ars.daojones.runtime.spi.database.Connection;
import de.ars.daojones.runtime.spi.database.ConnectionContext;
import de.ars.daojones.runtime.spi.database.DatabaseEntry;

/**
 * A class managing a connection to a Notes database. To create an instance, use
 * {@link NotesConnectionFactory}.
 * 
 * @param <T>
 *          the type of the bean
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 */
public class NotesConnection<T> implements Connection<T>, NotesEventHandlerProvider {

  private static final DataSourceTypeAwareRegistry<Void, QueryAssistant> assistants = new DataSourceTypeAwareRegistry<Void, QueryAssistant>(
          QueryAssistant.class );
  private static final Messages bundle = Messages.create( "NotesConnection" );

  private final Collection<NotesEventHandler> notesEventHandlers = new HashSet<NotesEventHandler>();
  private final ConnectionContext<T> context;
  private final ConnectionMetaData<T> metaData;
  private final NotesConnectionModel notesConnectionModel;

  /**
   * Creates an instance ready for committing to a Notes database.
   * 
   * @param context
   *          the context
   * @throws URISyntaxException
   */
  public NotesConnection( final ConnectionContext<T> context ) throws URISyntaxException {
    super();
    this.context = context;
    final NotesDatabasePath path = NotesDatabasePath.valueOf( getModel().getConnection().getDatabase() );
    this.notesConnectionModel = new NotesConnectionModel( getModel(), path, context.getCredentialVault() );
    this.metaData = new ConnectionMetaData<T>() {

      @Override
      public boolean isUpdateAllowed( final T... t ) {
        return true;
      }

      @Override
      public boolean isDeleteAllowed( final T... t ) {
        return true;
      }

    };
  }

  protected ConnectionContext<T> getContext() {
    return context;
  }

  protected ConnectionModel getModel() {
    return getContext().getConnectionModel();
  }

  /**
   * Returns the connector.
   * 
   * @return the connector
   */
  protected NotesConnector getConnector() {
    // return connector;
    return NotesConnectorManager.getInstance().get( notesConnectionModel );
  }

  public Session getSession() throws NotesException, DataAccessException {
    return getConnector().getSession();
  }

  public Database getDatabase() throws NotesException, DataAccessException {
    return getConnector().getDatabase();
  }

  /**
   * Returns the {@link NotesDatabasePath}.
   * 
   * @return the {@link NotesDatabasePath}
   */
  public NotesDatabasePath getNotesDatabasePath() {
    return notesConnectionModel.getPath();
  }

  /*
   * I N T E R F A C E M E T H O D S
   */

  @Override
  public int hashCode() {
    return getConnector().hashCode();
  }

  @SuppressWarnings( "rawtypes" )
  @Override
  public boolean equals( final Object obj ) {
    if ( !( obj instanceof NotesConnection ) ) {
      return false;
    }
    return getConnector().equals( ( ( NotesConnection ) obj ).getConnector() );
  }

  @Override
  public ConnectionMetaData<T> getMetaData() {
    return metaData;
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public SearchResult<DatabaseEntry> doFind( final Query query, final BeanModel... beanModels )
          throws DataAccessException {

    final Query internalQuery = query.clone();

    // Document are searched using a query language, Views are iterated and tested for a criterion

    final QueryLanguageFactory qlf = new QueryLanguageFactory();
    final QueryLanguage ql = qlf.createQueryLanguage();

    // Build Query Plan
    final Map<DataSourceType, Collection<BeanModel>> beansByDataSourceType = new HashMap<DataSourceType, Collection<BeanModel>>();
    for ( final BeanModel beanModel : beanModels ) {
      final Bean bean = beanModel.getBean();
      final DataSourceType key = bean.getTypeMapping().getType();
      Collection<BeanModel> beans = beansByDataSourceType.get( key );
      if ( null == beans ) {
        beans = new HashSet<BeanModel>();
        beansByDataSourceType.put( key, beans );
      }
      beans.add( beanModel );
    }
    // Execute Queries
    final Collection<SearchResult<DatabaseEntry>> results = new LinkedList<SearchResult<DatabaseEntry>>();
    try {
      for ( final Map.Entry<DataSourceType, Collection<BeanModel>> entry : beansByDataSourceType.entrySet() ) {
        final DataSourceType dsType = entry.getKey();
        final QueryAssistant assistant = findQueryAssistant( dsType );
        final BeanModel[] beans = entry.getValue().toArray( new BeanModel[entry.getValue().size()] );
        final QueryContext ctx = new QueryContextImpl( getDatabase(), ql, internalQuery, beans, this );
        final SearchResult<DatabaseEntry> result = assistant.executeQuery( getContext(), ctx );
        results.add( result );
        final int size = result.size();
        final int maxCount = internalQuery.getMaxCountOfResults();
        if ( maxCount > size ) {
          internalQuery.only( maxCount - size );
        } else {
          break;
        }
      }
    } catch ( final AwareNotFoundException e ) {
      throw new DataAccessException( e );
    } catch ( final NotesException e ) {
      throw new DataAccessException( e );
    }
    // Return composite result
    return new CompositeSearchResult<DatabaseEntry>( query.getMaxCountOfResults(),
            results.toArray( new SearchResult[results.size()] ) );
  }

  private QueryAssistant findQueryAssistant( final DataSourceType dsType ) throws AwareNotFoundException {
    return NotesConnection.assistants.findAwareNotNull( Void.TYPE, dsType );
  }

  private static class QueryContextImpl implements QueryAssistant.QueryContext {

    private final Database database;
    private final Query query;
    private final QueryLanguage queryLanguage;
    private final BeanModel[] beanModels;
    private final NotesEventHandlerProvider notesEventHandlerProvider;

    public QueryContextImpl( final Database database, final QueryLanguage queryLanguage, final Query query,
            final BeanModel[] beanModels, final NotesEventHandlerProvider notesEventHandlerProvider ) {
      super();
      this.query = query;
      this.database = database;
      this.queryLanguage = queryLanguage;
      this.beanModels = beanModels;
      this.notesEventHandlerProvider = notesEventHandlerProvider;
    }

    @Override
    public QueryLanguage getQueryLanguage() {
      return queryLanguage;
    }

    @Override
    public BeanModel[] getBeans() {
      return beanModels;
    }

    @Override
    public Database getDatabase() {
      return database;
    }

    @Override
    public NotesEventHandlerProvider getNotesEventHandlerProvider() {
      return notesEventHandlerProvider;
    }

    @Override
    public Query getQuery() {
      return query;
    }

  }

  @Override
  public DatabaseEntry doCreate( final BeanModel model ) throws DataAccessException {
    try {
      final DatabaseTypeMapping typeMapping = model.getBean().getTypeMapping();
      if ( typeMapping.getType() == DataSourceType.VIEW ) {
        throw new UnsupportedOperationException();
      } else {
        final Document document = getDatabase().createDocument();
        document.appendItemValue( "Form", typeMapping.getName() );
        final DataHandlerProvider dataHandlerProvider = DataHandlerProviderManager.getInstance();
        return new DocumentDatabaseEntry( document, model, dataHandlerProvider, this );
      }
    } catch ( final NotesException e ) {
      throw new DataAccessException( e );
    }
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public DatabaseEntry doFindById( final Object id ) throws DataAccessException {
    try {
      DatabaseEntry result = null;
      final String app = getContext().getApplicationId();
      if ( null != id ) {
        final DatabaseTypeMapping mapping = getContext().getBeanModel().getBean().getTypeMapping();
        if ( id instanceof Identificator ) {
          // Find application-specific id and search with this one 
          final Identificator identificator = ( Identificator ) id;
          final Serializable innerId = identificator.getId( app );
          result = doFindById( innerId );
        } else {
          final String id$ = String.valueOf( id );
          // Parse id to identificator object
          NotesIdentificator identificator = Identificators.valueOf( id$ );
          if ( null == identificator ) {
            // Not a valid format, just a simple universal id -> create identificator
            final DatabaseIdentificator dbIdentificator = Identificators.createIdentificator( getDatabase() );
            switch ( mapping.getType() ) {
            case TABLE:
              // Document Identificator for table mapping
              identificator = new DocumentIdentificator( id$, dbIdentificator );
              break;
            case VIEW:
              // View Entry Identificator for view mapping
              final ViewIdentificator viewIdentificator = new ViewIdentificator( mapping.getName(), dbIdentificator );
              identificator = new ViewEntryIdentificator( id$, viewIdentificator );
              break;
            default:
              throw new IllegalArgumentException( id$ );
            }
          }
          final QueryAssistant queryAssistant = findQueryAssistant( mapping.getType() );
          final Database database = getDatabase();
          result = queryAssistant.findById( getContext(), new FindByIdContext() {

            @Override
            public Database getDatabase() {
              return database;
            }

            @Override
            public BeanModel get( final String datasource, final DataSourceType dsType ) throws ConfigurationException {
              final BeanModelManager bmm = getContext().getBeanModelManager();
              final String applicationId = getContext().getApplicationId();
              // connectionBeanModel could be the model of a super type --> find model by form
              final BeanModel contextBeanModel = getContext().getBeanModel();

              BeanModel rawBeanModel = null;
              // <START>
              // find model by searching all bean models
              final DatabaseTypeMapping mappingToSearch = new DatabaseTypeMapping();
              mappingToSearch.setType( dsType );
              mappingToSearch.setName( datasource );
              final BeanModel[] rawBeanModels = bmm.findModelsByTypeMapping( applicationId, mappingToSearch );
              final Class<T> beanType = getContext().getBeanType();
              ClassNotFoundException cnfe = null;
              for ( final BeanModel model : rawBeanModels ) {
                if ( contextBeanModel.getBean().getType().equals( model.getBean().getType() ) ) {
                  // found -> finish
                  rawBeanModel = model;
                  break;
                } else {
                  try {
                    final Class<?> modelType = findBeanType( model.getBean().getType() );
                    if ( beanType.isAssignableFrom( modelType ) ) {
                      rawBeanModel = model;
                      break;
                    }
                  } catch ( final ClassNotFoundException e ) {
                    cnfe = e;
                  }

                }
              }
              if ( null != cnfe ) {
                throw new ConfigurationException( cnfe );
              } else if ( null == rawBeanModel ) {
                throw new ConfigurationException( NotesConnection.bundle.get( "error.no_model_for_typemapping",
                        getContext().getApplicationId(), mappingToSearch.getType().name().toLowerCase(),
                        mappingToSearch.getName() ) );
              }
              // <END>

              // find bean type and bean model of subtype
              final BeanModel result;
              if ( contextBeanModel.getBean().getType().equals( rawBeanModel.getBean().getType() ) ) {
                // no sub class
                result = contextBeanModel;
              } else {
                // result is of sub class type
                final String subTypeName = rawBeanModel.getBean().getType();
                final Class<? extends T> type;
                try {
                  type = ( Class<? extends T> ) findBeanType( subTypeName );
                } catch ( final ClassNotFoundException e ) {
                  throw new ConfigurationException( e );
                }
                result = bmm.getEffectiveModel( applicationId, type );
              }
              return result;
            }

            @Override
            public NotesEventHandlerProvider getNotesEventHandlerProvider() {
              return NotesConnection.this;
            }
          }, identificator );
        }
      }
      return result;
    } catch ( final ConfigurationException e ) {
      throw new DataAccessException( e );
    } catch ( final AwareNotFoundException e ) {
      throw new DataAccessException( e );
    } catch ( final NotesException e ) {
      throw new DataAccessException( e );
    }
  }

  protected Class<?> findBeanType( final String name ) throws ClassNotFoundException {
    return getContext().getBeanType().getClassLoader().loadClass( name );
  }

  @Override
  public void close() throws DataAccessException {
    // TODO Session Handling
    //    try {
    //      getSession().recycle();
    //    } catch ( final NotesException e ) {
    //      throw new DataAccessException( e );
    //    }
  }

  @Override
  public NotesEventHandler[] getNotesEventHandlers() {
    return notesEventHandlers.toArray( new NotesEventHandler[notesEventHandlers.size()] );
  }

  public void addNotesEventHandler( final NotesEventHandler handler ) {
    notesEventHandlers.add( handler );
  }

  public void removeNotesEventHandler( final NotesEventHandler handler ) {
    notesEventHandlers.remove( handler );
  }

}
