package de.ars.daojones.connections;

import java.util.Collection;

import de.ars.daojones.events.DataAccessListener;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.Identificator;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.query.SearchCriterion;

/**
 * An Accessor is the object that allows access to the database.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <T>
 *          the type this accessor should handle
 */
public interface Accessor<T extends Dao> {

  /**
   * Returns the connection that the accessor uses for database accesses.
   * 
   * @return the connection
   */
  Connection<T> getConnection();

  /**
   * Creates a new object in the database.
   * 
   * @return the new object containing information about how to find it in the
   *         database again
   * @throws DataAccessException
   *           if accessing the database failed
   */
  public T create() throws DataAccessException;

  /**
   * Deletes an object from the database. After calling this method, the object
   * will be recycled.
   * 
   * @param t
   *          the object to delete.
   * @throws DataAccessException
   */
  public void delete( T t ) throws DataAccessException;

  /**
   * Stores changed values in the database. If the object is not contained in
   * the database, it is inserted and the identificator is set.
   * 
   * @param t
   *          the object containing the persistent information
   * @throws DataAccessException
   *           if accessing the database failed
   */
  public void update( T t ) throws DataAccessException;

  /* *********************************
   *  F I N D I N G   M E T H O D S  *
   ********************************* */

  /**
   * Searches for all appearances of instances. Instances of subclasses of T
   * won't be added to the result collection. If you set an abstract class as
   * parameter, you will get an empty collection.
   * 
   * @return the found objects
   * @throws DataAccessException
   *           if accessing the database failed
   */
  public Collection<T> findAll() throws DataAccessException;

  /**
   * Searches for all appearances of instances. Instances of subclasses of T
   * won't be added to the result collection.
   * 
   * @param query
   *          the query
   * @return the found objects
   * @throws DataAccessException
   *           if accessing the database failed
   */
  public Collection<T> findAll( Query query ) throws DataAccessException;

  /**
   * Searches for all appearances of instances.
   * 
   * @param criteria
   *          an array of criteria affecting the query.
   * @return the found objects
   * @throws DataAccessException
   *           if accessing the database failed
   * @deprecated Use {@link #findAll(Query)} instead.
   */
  @Deprecated
  public Collection<T> findAll( SearchCriterion... criteria )
      throws DataAccessException;

  /**
   * Searches for a single object.
   * 
   * @return the result
   * @throws DataAccessException
   *           if accessing the database failed
   */
  public T find() throws DataAccessException;

  /**
   * Searches for only one object of a special class. If there is more than one
   * result, the first is returned. Instances of subclasses won't be included to
   * the search.
   * 
   * @param criteria
   *          an array of criteria affecting the query.
   * @return the found object
   * @throws DataAccessException
   *           if accessing the database failed
   * @deprecated use {@link #find(Query)}
   */
  @Deprecated
  public T find( SearchCriterion... criteria ) throws DataAccessException;

  /**
   * Searches for a single object with an object restricting the query.
   * 
   * @param query
   *          the query
   * @return the result
   * @throws DataAccessException
   *           if accessing the database failed
   */
  public T find( Query query ) throws DataAccessException;

  /**
   * Searches for only one object of a special class with a special
   * identificator. This identificator is unique in the database, so only max.
   * one result will be found.
   * 
   * @param id
   *          the unique identificator
   * @param subclasses
   *          the subclasses that can be returned (if not specified, only the
   *          connection class is valid)
   * @return the found object
   * @throws DataAccessException
   *           if accessing the database failed
   */
  public T findById( Identificator id, Class<? extends T>... subclasses )
      throws DataAccessException;

  /**
   * Closes a connection.
   * 
   * @throws DataAccessException
   */
  public void close() throws DataAccessException;

  /* ***********************************
   *  L I S T E N E R   M E T H O D S  *
   *********************************** */

  /**
   * Adds a DataAccessListener.
   * 
   * @param listener
   *          the listener
   * @return true, if the listener was added to the listener registry
   */
  public boolean addDataAccessListener( DataAccessListener<T> listener );

  /**
   * Removes a DataAccessListener.
   * 
   * @param listener
   *          the listener
   * @return true, if the listener was removed from the listener registry
   */
  public boolean removeDataAccessListener( DataAccessListener<T> listener );

}