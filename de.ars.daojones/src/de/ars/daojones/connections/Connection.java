package de.ars.daojones.connections;

import java.util.Collection;

import de.ars.daojones.events.DataAccessListener;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.Identificator;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.query.SearchCriterion;

/**
 * A connection to a database that is responsible to read and write objects of a
 * special type.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 *          The DaoJones DAO bean that can be read or written with this
 *          connection.
 */
public abstract class Connection<T extends Dao> implements Accessor<T> {

  private final String applicationContextId;

  /**
   * Creates a new connection.
   * 
   * @param ctx
   *          the context
   */
  protected Connection( ApplicationContext ctx ) {
    super();
    this.applicationContextId = ctx.getId();
  }

  /**
   * Returns the context that created the connection.
   * 
   * @return the context
   */
  public ApplicationContext getContext() {
    return ApplicationContextFactory.getInstance().getApplicationContext(
        this.applicationContextId );
  }

  /**
   * Returns the accessor. An accessor is the object that you can use to access
   * to the database. The Connection class has a number of delegate methods, so
   * you do not have to use this method explicitely. You can use this method to
   * use an Accessor as a parameter for another method without sending the whole
   * connection. When calling this method twice, you cannot be sure to get the
   * same accessor object.
   * 
   * @return the accessor
   */
  public abstract Accessor<T> getAccessor();

  /**
   * Returns the connection data to connect to the database.
   * 
   * @return the connection data
   */
  public abstract ConnectionData getConnectionData();

  /**
   * Closes a connection.
   * 
   * @throws DataAccessException
   */
  public void close() throws DataAccessException {
    getAccessor().close();
  }

  /**
   * Returns a connection that should be used for the given DAO class. If the
   * connection does not yet exist, it is created. Otherwise you will always get
   * the same instance.
   * 
   * @param <T>
   *          the DAO type
   * @param ctx
   *          the {@link ApplicationContext}
   * @param c
   *          the DAO class
   * @return the connection
   * @throws DataAccessException
   */
  public static <T extends Dao> Connection<T> get( ApplicationContext ctx,
      Class<T> c ) throws DataAccessException {
    return ( null != ctx ? ctx : ApplicationContext.getDefault() )
        .getConnectionForClass( c );
  }

  /**
   * This is a delegate method to the accessor object.
   * 
   * @see de.ars.daojones.connections.Accessor#create()
   */
  public T create() throws DataAccessException {
    return getAccessor().create();
  }

  /**
   * This is a delegate method to the accessor object.
   * 
   * @see de.ars.daojones.connections.Accessor#delete(Dao)
   */
  public void delete( T t ) throws DataAccessException {
    getAccessor().delete( t );
  }

  /**
   * This is a delegate method to the accessor object.
   * 
   * @see de.ars.daojones.connections.Accessor#find(de.ars.daojones.runtime.query.SearchCriterion[])
   * @deprecated Use {@link #find(Query)} instead.
   */
  @Deprecated
  public T find( SearchCriterion... criterions ) throws DataAccessException {
    return getAccessor().find( criterions );
  }

  /**
   * This is a delegate method to the accessor object.
   * 
   * @see de.ars.daojones.connections.Accessor#find()
   */
  public T find() throws DataAccessException {
    return getAccessor().find();
  }

  /**
   * This is a delegate method to the accessor object.
   * 
   * @see de.ars.daojones.connections.Accessor#find(Query)
   */
  public T find( Query query ) throws DataAccessException {
    return getAccessor().find( query );
  }

  /**
   * This is a delegate method to the accessor object.
   * 
   * @see de.ars.daojones.connections.Accessor#findAll()
   */
  public Collection<T> findAll() throws DataAccessException {
    return getAccessor().findAll();
  }

  /**
   * This is a delegate method to the accessor object.
   * 
   * @see de.ars.daojones.connections.Accessor#findAll(de.ars.daojones.runtime.query.SearchCriterion[])
   * @deprecated Use {@link #findAll(Query)} instead.
   */
  @Deprecated
  public Collection<T> findAll( SearchCriterion... criterions )
      throws DataAccessException {
    return getAccessor().findAll( criterions );
  }

  /**
   * @see de.ars.daojones.connections.Accessor#findAll(de.ars.daojones.runtime.query.Query)
   */
  public Collection<T> findAll( Query query ) throws DataAccessException {
    return getAccessor().findAll( query );
  }

  /**
   * This is a delegate method to the accessor object.
   * 
   * @see de.ars.daojones.connections.Accessor#findById(Identificator, Class...)
   */
  public T findById( Identificator id, Class<? extends T>... subclasses )
      throws DataAccessException {
    return getAccessor().findById( id, subclasses );
  }

  /**
   * This method returns an object by the string that was created by
   * de.ars.daojones.runtime.Identificator#toString(). Use the identificator
   * string for sending an identificator in a string based protocol (e.g. web
   * applications). This is equal to calling findById(getIdentificator(id));
   * 
   * @see de.ars.daojones.runtime.Identificator#toString()
   * @param id
   *          the string that was created by
   *          de.ars.daojones.runtime.Identificator#toString()
   * @return the object with the id
   * @throws DataAccessException
   * @deprecated Use {@link #findById(Identificator, Class...)} instead.
   */
  @Deprecated
  public T findById( String id ) throws DataAccessException {
    return findById( getIdentificator( id ) );
  }

  /**
   * This is a delegate method to the accessor object.
   * 
   * @see de.ars.daojones.connections.Accessor#getConnection()
   */
  public Connection<T> getConnection() {
    return getAccessor().getConnection();
  }

  /**
   * This is a delegate method to the accessor object.
   * 
   * @see de.ars.daojones.connections.Accessor#update(de.ars.daojones.runtime.Dao)
   */
  public void update( T t ) throws DataAccessException {
    getAccessor().update( t );
  }

  /**
   * This method returns an identificator by the string that was created its own
   * toString() method. Use the identificator string for sending an
   * identificator in a string based protocol (e.g. web applications).
   * 
   * @param id
   *          the string that was created by
   *          de.ars.daojones.runtime.Identificator#toString()
   * @return the identificator with the id
   * @throws DataAccessException
   */
  public abstract Identificator getIdentificator( String id )
      throws DataAccessException;

  /**
   * This is a delegate method to the accessor object.
   * 
   * @see de.ars.daojones.connections.Accessor#addDataAccessListener(de.ars.daojones.events.DataAccessListener)
   */
  public boolean addDataAccessListener( DataAccessListener<T> listener ) {
    return getAccessor().addDataAccessListener( listener );
  }

  /**
   * This is a delegate method to the accessor object.
   * 
   * @see de.ars.daojones.connections.Accessor#removeDataAccessListener(de.ars.daojones.events.DataAccessListener)
   */
  public boolean removeDataAccessListener( DataAccessListener<T> listener ) {
    return getAccessor().removeDataAccessListener( listener );
  }

  /**
   * Returns some meta information about this connection.
   * 
   * @return the metadata
   */
  public abstract ConnectionMetaData<T> getMetaData();

  /**
   * @see Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime
        * result
        + ( ( applicationContextId == null ) ? 0 : applicationContextId
            .hashCode() );
    result = prime
        * result
        + ( ( getConnectionData() == null ) ? 0 : getConnectionData()
            .hashCode() );
    return result;
  }

  /**
   * @see Object#equals(Object)
   */
  @SuppressWarnings( "unchecked" )
  @Override
  public boolean equals( Object obj ) {
    if ( this == obj )
      return true;
    if ( obj == null )
      return false;
    if ( getClass() != obj.getClass() )
      return false;
    Connection<T> other = ( Connection<T> ) obj;
    if ( applicationContextId == null ) {
      if ( other.applicationContextId != null )
        return false;
    } else if ( !applicationContextId.equals( other.applicationContextId ) )
      return false;
    if ( getConnectionData() == null ) {
      if ( other.getConnectionData() != null )
        return false;
    } else if ( !getConnectionData().equals( other.getConnectionData() ) )
      return false;
    return true;
  }

}
