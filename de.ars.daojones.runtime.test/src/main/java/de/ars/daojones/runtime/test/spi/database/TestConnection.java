package de.ars.daojones.runtime.test.spi.database;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import de.ars.daojones.internal.runtime.test.DatabaseEntryBeanAccessorImpl;
import de.ars.daojones.internal.runtime.test.IdentificatorImpl;
import de.ars.daojones.internal.runtime.test.utilities.Messages;
import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.ConnectionMetaData;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.ApplicationContext;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.query.SearchCriterion;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorProvider;
import de.ars.daojones.runtime.spi.database.AbstractSearchResult;
import de.ars.daojones.runtime.spi.database.Connection;
import de.ars.daojones.runtime.spi.database.ConnectionContext;
import de.ars.daojones.runtime.spi.database.DatabaseEntry;
import de.ars.daojones.runtime.test.data.DataSource;
import de.ars.daojones.runtime.test.data.DataSource.DataSourceType;
import de.ars.daojones.runtime.test.data.Entry;
import de.ars.daojones.runtime.test.data.Model;

/**
 * A connection accessing a local test file.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the bean type
 */
public class TestConnection<T> implements Connection<T>, TestModelIndex {

  private static final Messages bundle = Messages.create( "spi.database.TestConnection" );

  private final ConnectionContext<T> connectionContext;
  private final Model content;

  public TestConnection( final ConnectionContext<T> connectionContext, final Model content ) {
    super();
    this.connectionContext = connectionContext;
    this.content = content;
  }

  public DataSourceType toTestModelType( final DatabaseTypeMapping.DataSourceType coreModelType ) {
    switch ( coreModelType ) {
    case TABLE:
      return DataSourceType.TABLE;
    case VIEW:
      return DataSourceType.VIEW;
    default:
      throw new IllegalArgumentException( coreModelType.name() );
    }
  }

  public DatabaseTypeMapping.DataSourceType toCoreModelType( final DataSourceType coreModelType ) {
    switch ( coreModelType ) {
    case TABLE:
      return DatabaseTypeMapping.DataSourceType.TABLE;
    case VIEW:
      return DatabaseTypeMapping.DataSourceType.VIEW;
    default:
      throw new IllegalArgumentException( coreModelType.name() );
    }
  }

  @Override
  public DataSource getDataSource( final BeanModel beanModel, final boolean create ) {
    final DatabaseTypeMapping typeMapping = beanModel.getBean().getTypeMapping();
    final String dsName = typeMapping.getName();
    final DataSourceType testModelType = toTestModelType( typeMapping.getType() );
    // find in content
    synchronized ( content ) {
      for ( final DataSource ds : content.getDataSources() ) {
        if ( ds.getName().equals( dsName ) && ds.getType() == testModelType ) {
          return ds;
        }
      }
      // not found
      if ( create ) {
        final DataSource result = new DataSource();
        result.setName( dsName );
        result.setType( testModelType );
        return result;
      } else {
        return null;
      }
    }
  }

  /**
   * Returns the content of this database.
   * 
   * @return the content
   */
  protected Model getContent() {
    return this.content;
  }

  /**
   * Returns the connection context.
   * 
   * @return the connection context
   */
  protected ConnectionContext<T> getContext() {
    return this.connectionContext;
  }

  @Override
  public ConnectionMetaData<T> getMetaData() {
    return new ConnectionMetaData<T>() {

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

  protected Class<?> findBeanType( final String name ) throws ClassNotFoundException {
    return getContext().getBeanType().getClassLoader().loadClass( name );
  }

  @Override
  public BeanModel findBeanModel( final DataSource ds ) throws ConfigurationException {
    final DatabaseTypeMapping mappingToSearch = new DatabaseTypeMapping();
    mappingToSearch.setType( toCoreModelType( ds.getType() ) );
    mappingToSearch.setName( ds.getName() );
    final BeanModel[] rawBeanModels = getContext().getBeanModelManager().findModelsByTypeMapping(
            getContext().getApplicationId(), mappingToSearch );
    final Class<T> beanType = getContext().getBeanType();
    ClassNotFoundException cnfe = null;
    for ( final BeanModel model : rawBeanModels ) {
      if ( getContext().getBeanModel().getBean().getType().equals( model.getBean().getType() ) ) {
        return model;
      } else {
        try {
          final Class<?> modelType = findBeanType( model.getBean().getType() );
          if ( beanType.isAssignableFrom( modelType ) ) {
            return model;
          }
        } catch ( final ClassNotFoundException e ) {
          cnfe = e;
        }

      }
    }
    if ( null != cnfe ) {
      throw new ConfigurationException( cnfe );
    } else {
      throw new ConfigurationException( TestConnection.bundle.get( "error.no_model_for_typemapping", getContext()
              .getApplicationId(), mappingToSearch.getType().name().toLowerCase(), mappingToSearch.getName() ) );
    }
  }

  public TestDatabaseEntry createDatabaseEntry( final Entry entry, final BeanModel model ) {
    return new TestDatabaseEntry( entry, model, this );
  }

  @Override
  public DatabaseEntry doCreate( final BeanModel model ) throws DataAccessException {
    return createDatabaseEntry( new Entry(), model );
  }

  @Override
  public Identificator createIdentificator( final Entry entry ) {
    return null != entry ? new IdentificatorImpl( entry, getContext().getBeanModel() ) : null;
  }

  @Override
  public SearchResult<DatabaseEntry> doFind( final Query query, final BeanModel... beanModels )
          throws DataAccessException {
    final SearchCriterion sc = query.getCriterion();
    final Collection<DatabaseEntry> result = new LinkedList<DatabaseEntry>();
    outerLoop: for ( final BeanModel beanModel : beanModels ) {
      final DataSource ds = getDataSource( beanModel, false );
      if ( null != ds && null != sc ) {
        for ( final Entry entry : ds.getEntries() ) {
          final DatabaseEntry dbEntry = createDatabaseEntry( entry, beanModel );
          try {
            // Use custom bean accessor provider to access the Database Entry instead of the original bean
            final ConnectionProvider cp = getContext().getConnectionProvider();
            final BeanModelManager bmm = getContext().getBeanModelManager();
            final BeanAccessorProvider bap = new DatabaseEntryBeanAccessorImpl();
            final ApplicationContext appCtx = new ApplicationContext( cp, bmm, bap );
            if ( result.size() < query.getMaxCountOfResults() && sc.matches( appCtx, dbEntry ) ) {
              result.add( dbEntry );
              if ( result.size() >= query.getMaxCountOfResults() ) {
                break outerLoop;
              }
            }
          } catch ( final ConfigurationException e ) {
            throw new DataAccessException( e );
          } catch ( final FieldAccessException e ) {
            throw new DataAccessException( e );
          }
        }
      }
    }
    return new AbstractSearchResult<DatabaseEntry>() {

      @Override
      public int size() {
        return result.size();
      }

      @Override
      public Iterator<DatabaseEntry> iterator() {
        return result.iterator();
      }

      @Override
      public void close() throws DataAccessException {
        // nothing to do
      }
    };
  }

  @Override
  public DatabaseEntry doFindById( final Object id ) throws DataAccessException {
    try {
      if ( id instanceof IdentificatorImpl ) {
        final IdentificatorImpl ii = ( IdentificatorImpl ) id;
        return createDatabaseEntry( ii.getEntry(), ii.getBeanModel() );
      } else {
        final String idAsString;
        if ( id instanceof Identificator ) {
          idAsString = ( ( Identificator ) id ).getId( getContext().getApplicationId() ).toString();
        } else {
          idAsString = id.toString();
        }
        for ( final DataSource ds : content.getDataSources() ) {
          for ( final Entry entry : ds.getEntries() ) {
            if ( idAsString.equals( entry.getId() ) ) {
              final BeanModel beanModel = findBeanModel( ds );
              return createDatabaseEntry( entry, beanModel );
            }
          }
        }
        return null;
      }
    } catch ( final ConfigurationException e ) {
      throw new DataAccessException( e );
    }
  }

  @Override
  public void close() throws DataAccessException {
    // nothing to do
  }

  @Override
  public Entry findEntry( final Serializable id ) throws ConfigurationException {
    if ( id instanceof IdentificatorImpl ) {
      return ( ( IdentificatorImpl ) id ).getEntry();
    } else if ( null != id ) {
      final String idAsString = id.toString();
      for ( final DataSource ds : content.getDataSources() ) {
        for ( final Entry entry : ds.getEntries() ) {
          if ( idAsString.equals( entry.getId() ) ) {
            return entry;
          }
        }
      }
      return null;
    } else {
      return null;
    }
  }

  @Override
  public String getApplication() {
    return getContext().getApplicationId();
  }

}
