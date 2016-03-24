package de.ars.daojones.drivers.notes.xt;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import de.ars.daojones.drivers.notes.DataHandlerProvider;
import de.ars.daojones.drivers.notes.DatabaseIdentificator;
import de.ars.daojones.drivers.notes.DocumentIdentificator;
import de.ars.daojones.drivers.notes.NotesElement;
import de.ars.daojones.drivers.notes.NotesIdentificator;
import de.ars.daojones.drivers.notes.ViewEntryIdentificator;
import de.ars.daojones.drivers.notes.ViewIdentificator;
import de.ars.daojones.internal.drivers.notes.DataHandlerProviderManager;
import de.ars.daojones.internal.drivers.notes.DocumentDatabaseEntry;
import de.ars.daojones.internal.drivers.notes.DocumentDatabaseEntry.NotesEventHandlerProvider;
import de.ars.daojones.internal.drivers.notes.Identificators;
import de.ars.daojones.internal.drivers.notes.NotesConnection;
import de.ars.daojones.internal.drivers.notes.ViewDatabaseEntry;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.beans.identification.ApplicationDependentIdentificator;
import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping.DataSourceType;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionUtility;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessor;
import de.ars.daojones.runtime.spi.beans.fields.BeanAccessorContext;
import de.ars.daojones.runtime.spi.database.DatabaseEntry;
import de.ars.daojones.runtime.spi.database.DriverProvider;

/**
 * An extension of the driver's interface to access the objects of the Notes
 * API. This can be used to work with the Notes objects directly to implement
 * functionality that is not provided by the DaoJones Notes driver.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public final class NotesAPI {

  private static final Messages bundle = Messages.create( "xt.NotesAPI" );

  private NotesAPI() {
    super();
  }

  private static <T> NotesConnection<T> getDriver( final Connection<T> con ) {
    final de.ars.daojones.runtime.spi.database.Connection<T> driver = ConnectionUtility.getDriver( con );
    if ( null != driver ) {
      if ( driver instanceof NotesConnection ) {
        return ( NotesConnection<T> ) driver;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  private static <T> BeanAccessor getBeanAccessor( final Connection<T> connection ) throws DataAccessException,
          ConfigurationException {
    // get identificator from document
    final DriverProvider<T> driverProvider = ConnectionUtility.getDriverProvider( connection );
    if ( null != driverProvider ) {
      return driverProvider.getBeanAccessor();
    } else {
      return null;
    }
  }

  private static <T> Identificator getIdentificator( final Connection<T> connection, final T bean )
          throws DataAccessException, ConfigurationException {
    if ( null != bean ) {
      final BeanAccessor accessor = NotesAPI.getBeanAccessor( connection );
      if ( null != accessor ) {
        return accessor.getIdentificator( connection.getBeanModel(), bean );
      }
    }
    return null;
  }

  private static Identificator getRawIdentificator( final Identificator identificator, final String application ) {
    if ( identificator instanceof ApplicationDependentIdentificator ) {
      return ( ( ApplicationDependentIdentificator ) identificator ).get( application );
    } else {
      return identificator;
    }
  }

  private static NotesEventHandlerProvider getNotesEventHandlerProvider( final Connection<?> connection ) {
    return NotesAPI.getDriver( connection );
  }

  /**
   * Returns the session object that is assigned to a given connection.
   * 
   * @param connection
   *          the connection
   * @return the session object or <tt>null</tt>, if the connection is not a
   *         Notes connection
   * @throws NotesException
   * @throws DataAccessException
   */
  public static Session getSession( final Connection<?> connection ) throws NotesException, DataAccessException {
    final NotesConnection<?> driver = NotesAPI.getDriver( connection );
    return null != driver ? driver.getSession() : null;
  }

  /**
   * Returns the database object that is assigned to a given connection.
   * 
   * @param connection
   *          the connection
   * @return the database object or <tt>null</tt>, if the connection is not a
   *         Notes connection
   * @throws NotesException
   * @throws DataAccessException
   */
  public static Database getDatabase( final Connection<?> connection ) throws NotesException, DataAccessException {
    final NotesConnection<?> driver = NotesAPI.getDriver( connection );
    return null != driver ? driver.getDatabase() : null;
  }

  /**
   * Returns the database object that an identificator addresses.
   * 
   * @param identificator
   *          the identificator
   * @param application
   *          the application id
   * @param session
   *          the session
   * @return the database object or <tt>null</tt>, if the session is
   *         <tt>null</tt> or the identificator does not address any Notes
   *         database (or any element within a Notes database)
   * @throws NotesException
   * @throws DataAccessException
   */
  public static Database getDatabase( final Identificator identificator, final String application, final Session session )
          throws NotesException, DataAccessException {
    final Identificator rawId = NotesAPI.getRawIdentificator( identificator, application );
    if ( null != session && rawId instanceof NotesIdentificator ) {
      final NotesIdentificator ni = ( NotesIdentificator ) rawId;
      final DatabaseIdentificator db = ( DatabaseIdentificator ) NotesIdentificator.to( ni, NotesElement.DATABASE );
      return Identificators.findReference( session, null, db );
    }
    return null;
  }

  /**
   * Returns the document object that an identificator addresses.
   * 
   * @param identificator
   *          the identificator
   * @param application
   *          the application id
   * @param session
   *          the session
   * @return the document object or <tt>null</tt>, if the session is
   *         <tt>null</tt> or the identificator does not address any Notes
   *         document or any view entry
   * @throws NotesException
   * @throws DataAccessException
   */
  public static Document getDocument( final Identificator identificator, final String application, final Session session )
          throws NotesException, DataAccessException {
    final Identificator rawId = NotesAPI.getRawIdentificator( identificator, application );
    if ( null != session && rawId instanceof NotesIdentificator ) {
      final NotesIdentificator ni = ( NotesIdentificator ) rawId;
      final DocumentIdentificator doc = ( DocumentIdentificator ) NotesIdentificator.to( ni, NotesElement.DOCUMENT );
      return Identificators.findReference( session, null, doc );
    }
    return null;
  }

  /**
   * Returns the document object that a bean is assigned to.
   * 
   * @param connection
   *          the connection
   * @param bean
   *          the bean
   * @param <T>
   *          the bean type
   * @return the document object or <tt>null</tt>, if the connection is not a
   *         Notes connection, the bean is <tt>null</tt> or does not have any
   *         identificator addressing a Notes document
   * @throws NotesException
   * @throws DataAccessException
   * @throws ConfigurationException
   * @see {@link #createBean(Connection, Document)}
   */
  public static <T> Document getDocument( final Connection<T> connection, final T bean ) throws NotesException,
          DataAccessException, ConfigurationException {
    final Identificator identificator = NotesAPI.getIdentificator( connection, bean );
    if ( null != identificator ) {
      // Only open session, if any identificator was found
      final Session session = NotesAPI.getSession( connection );
      return NotesAPI.getDocument( identificator, connection.getModel().getId().getApplicationId(), session );
    } else {
      return null;
    }
  }

  /**
   * Returns the view entry that an identificator addresses.
   * 
   * @param session
   *          the session
   * @param application
   *          the application id
   * @param identificator
   *          the identificator
   * @return the view entry or <tt>null</tt>, if the session is <tt>null</tt> or
   *         the identificator does not address any Notes view entry
   * @throws NotesException
   * @throws DataAccessException
   */
  public static ViewEntry getViewEntry( final Identificator identificator, final String application,
          final Session session ) throws NotesException, DataAccessException {
    final Identificator rawId = NotesAPI.getRawIdentificator( identificator, application );
    if ( null != session && rawId instanceof NotesIdentificator ) {
      final NotesIdentificator ni = ( NotesIdentificator ) rawId;
      final ViewEntryIdentificator e = ( ViewEntryIdentificator ) NotesIdentificator.to( ni, NotesElement.VIEW_ENTRY );
      return Identificators.findReference( session, null, e );
    }
    return null;
  }

  /**
   * Returns the view entry that a bean is assigned to.
   * 
   * @param connection
   *          the connection
   * @param bean
   *          the bean
   * @param <T>
   *          the bean type
   * @return the view entry or <tt>null</tt>, if the connection is not a Notes
   *         connection, the bean is <tt>null</tt> or does not have any
   *         identificator addressing a Notes view entry
   * @throws NotesException
   * @throws DataAccessException
   * @throws ConfigurationException
   */
  public static <T> ViewEntry getViewEntry( final Connection<T> connection, final T bean ) throws NotesException,
          DataAccessException, ConfigurationException {
    final Identificator identificator = NotesAPI.getIdentificator( connection, bean );
    if ( null != identificator ) {
      // Only open session, if any identificator was found
      final Session session = NotesAPI.getSession( connection );
      return NotesAPI.getViewEntry( identificator, connection.getModel().getId().getApplicationId(), session );
    } else {
      return null;
    }
  }

  /**
   * Returns the view object that an identificator addresses.
   * 
   * @param identificator
   *          the identificator
   * @param application
   *          the application id
   * @param session
   *          the session
   * @return the view object or <tt>null</tt>, if the session is <tt>null</tt>
   *         or the identificator does not address any Notes view or any view
   *         entry
   * @throws NotesException
   * @throws DataAccessException
   */
  public static View getView( final Identificator identificator, final String application, final Session session )
          throws NotesException, DataAccessException {
    final Identificator rawId = NotesAPI.getRawIdentificator( identificator, application );
    if ( null != session && rawId instanceof NotesIdentificator ) {
      final NotesIdentificator ni = ( NotesIdentificator ) rawId;
      final ViewIdentificator doc = ( ViewIdentificator ) NotesIdentificator.to( ni, NotesElement.VIEW );
      return Identificators.findReference( session, null, doc );
    }
    return null;
  }

  /**
   * Returns the view object that a bean is assigned to.
   * 
   * @param connection
   *          the connection
   * @param bean
   *          the bean
   * @param <T>
   *          the bean type
   * @return the view object or <tt>null</tt>, if the connection is not a Notes
   *         connection, the bean is <tt>null</tt> or does not have any
   *         identificator addressing a Notes view or a view entry
   * @throws NotesException
   * @throws DataAccessException
   * @throws ConfigurationException
   */
  public static <T> View getView( final Connection<T> connection, final T bean ) throws NotesException,
          DataAccessException, ConfigurationException {
    final Identificator identificator = NotesAPI.getIdentificator( connection, bean );
    if ( null != identificator ) {
      // Only open session, if any identificator was found
      final Session session = NotesAPI.getSession( connection );
      return NotesAPI.getView( identificator, connection.getModel().getId().getApplicationId(), session );
    } else {
      return null;
    }
  }

  /**
   * Creates a bean and injects the values from the document.
   * 
   * @param <T>
   *          the bean type
   * @param connection
   *          the connection
   * @param document
   *          the document
   * @return the bean instance
   * @throws DataAccessException
   * @throws FieldAccessException
   * @throws ConfigurationException
   *           if the bean is mapped to a view entry
   * @throws NotesException
   * @see #getDocument(Connection, Object)
   * @see #createBean(Connection, View, ViewEntry)
   */
  public static <T> T createBean( final Connection<T> connection, final Document document ) throws DataAccessException,
          FieldAccessException, ConfigurationException, NotesException {
    try {
      final DataSourceType dsType = connection.getBeanModel().getBean().getTypeMapping().getType();
      if ( dsType == DataSourceType.TABLE ) {
        final DriverProvider<T> driverProvider = ConnectionUtility.getDriverProvider( connection );
        if ( null != driverProvider ) {
          final BeanAccessor accessor = driverProvider.getBeanAccessor();
          final DataHandlerProvider dahp = DataHandlerProviderManager.getInstance();
          final NotesEventHandlerProvider dohp = NotesAPI.getNotesEventHandlerProvider( connection );
          final BeanModel beanModel = connection.getBeanModel();
          final DatabaseEntry databaseEntry = new DocumentDatabaseEntry( document, beanModel, dahp, dohp );
          final Class<? extends T> beanClass = driverProvider.getBeanClass( beanModel );
          final BeanAccessorContext<T> context = driverProvider.createBeanAccessorContext( databaseEntry, beanClass );
          final T t = accessor.createBeanInstance( context );
          return t;
        } else {
          throw new ConfigurationException();
        }
      } else {
        throw new ConfigurationException( NotesAPI.bundle.get( "error.createbean.mapping."
                + dsType.name().toLowerCase() ) );
      }
    } catch ( final ClassNotFoundException e ) {
      throw new ConfigurationException( e );
    }
  }

  /**
   * Creates a bean and injects the values from the view entry. If the bean is
   * mapped to a document, the bean is created by injecting the values directly
   * from the corresponding document.
   * 
   * @param <T>
   *          the bean type
   * @param connection
   *          the connection
   * @param view
   *          the view
   * @param entry
   *          the view entry
   * @return the bean instance
   * @throws DataAccessException
   * @throws FieldAccessException
   * @throws ConfigurationException
   *           if the bean is mapped to a document
   * @throws NotesException
   * @see #getViewEntry(Connection, Object)
   * @see #createBean(Connection, Document)
   */
  public static <T> T createBean( final Connection<T> connection, final View view, final ViewEntry entry )
          throws DataAccessException, FieldAccessException, ConfigurationException, NotesException {
    try {
      final DataSourceType dsType = connection.getBeanModel().getBean().getTypeMapping().getType();
      if ( dsType == DataSourceType.VIEW ) {
        final DriverProvider<T> driverProvider = ConnectionUtility.getDriverProvider( connection );
        if ( null != driverProvider ) {
          final BeanAccessor accessor = driverProvider.getBeanAccessor();
          final DataHandlerProvider dahp = DataHandlerProviderManager.getInstance();
          final NotesEventHandlerProvider dohp = NotesAPI.getNotesEventHandlerProvider( connection );
          final BeanModel beanModel = connection.getBeanModel();
          final DatabaseEntry databaseEntry = new ViewDatabaseEntry( view, entry, beanModel, dahp, dohp );
          final Class<? extends T> beanClass = driverProvider.getBeanClass( beanModel );
          final BeanAccessorContext<T> context = driverProvider.createBeanAccessorContext( databaseEntry, beanClass );
          final T t = accessor.createBeanInstance( context );
          return t;
        } else {
          throw new ConfigurationException();
        }
      } else {
        if ( entry.isDocument() ) {
          return NotesAPI.createBean( connection, entry.getDocument() );
        } else {
          throw new ConfigurationException( NotesAPI.bundle.get( "error.createbean.mapping."
                  + dsType.name().toLowerCase() ) );
        }
      }
    } catch ( final ClassNotFoundException e ) {
      throw new ConfigurationException( e );
    }
  }

  /**
   * Adds a notes event handler to a connection. This handler is invoked for
   * each document event that is assigned to the connection's bean type.
   * 
   * @param connection
   *          the connection
   * @param handler
   *          the handler
   */
  public static void addNotesEventHandler( final Connection<?> connection, final NotesEventHandler handler ) {
    final NotesConnection<?> driver = NotesAPI.getDriver( connection );
    if ( null != driver ) {
      driver.addNotesEventHandler( handler );
    }
  }

  /**
   * Removes a Notes event handler from the connection.
   * 
   * @param connection
   *          the connection
   * @param handler
   *          the handler
   */
  public static void removeNotesEventHandler( final Connection<?> connection, final NotesEventHandler handler ) {
    final NotesConnection<?> driver = NotesAPI.getDriver( connection );
    if ( null != driver ) {
      driver.removeNotesEventHandler( handler );
    }
  }

  /**
   * Returns all Notes event handlers that are assigned to a connection's bean
   * type.
   * 
   * @param connection
   *          the connection
   * @return the event handlers
   */
  public static NotesEventHandler[] getNotesEventHandlers( final Connection<?> connection ) {
    final NotesEventHandlerProvider dhp = NotesAPI.getNotesEventHandlerProvider( connection );
    if ( null != dhp ) {
      return dhp.getNotesEventHandlers();
    } else {
      return new NotesEventHandler[0];
    }

  }

}
