package de.ars.daojones.runtime.connections;

import java.io.Closeable;
import java.util.Iterator;
import java.util.List;

import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.connections.events.DataAccessListener;
import de.ars.daojones.runtime.query.Query;

/**
 * An Accessor is the object that allows access to the database.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @param <T>
 *          the bean type this accessor should handle
 * @since 1.2
 */
public interface Accessor<T> extends Closeable {

  /**
   * An object that provides access to the search result.<br/>
   * <br/>
   * <b>Please note:</b> A search result never returns any <tt>null</tt> value.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
   * @param <T>
   *          the bean type
   * @since 2.0
   */
  public static interface SearchResult<T> extends Iterable<T>, Closeable {

    /**
     * Returns the size of the search result.
     * 
     * @return the size of the search result
     */
    int size();

    /**
     * Returns the search result as list. This reads the whole data from the
     * database and should not be invoked for big results. This method delegates
     * to the invocation of <tt>getAsList(0, size())</tt>.<br/>
     * <br/>
     * <b>Please note:</b> A search result never returns any <tt>null</tt>
     * value.
     * 
     * @return the search result as list
     * @throws DataAccessException
     * @see {@link #getAsList(int, int)}
     */
    List<T> getAsList() throws DataAccessException;

    /**
     * Returns the search result as sub list. This reads the whole data from the
     * begin index to the end index from the database and should not be invoked
     * for big results. The list then has a size of
     * <tt>(endIndex-beginIndex)</tt>.<br/>
     * <br/>
     * <b>Please note:</b> A search result never returns any <tt>null</tt>
     * value.
     * 
     * @param beginIndex
     *          the beginning index, inclusive.
     * @param endIndex
     *          the ending index, exclusive.
     * @return the search result as list
     * @throws DataAccessException
     */
    List<T> getAsList( int beginIndex, int endIndex ) throws DataAccessException, IndexOutOfBoundsException;

    /**
     * Returns an iterator over a set of elements of type T.<br/>
     * <br/>
     * <b>Please note:</b> A search result never returns any <tt>null</tt>
     * value.
     * 
     * @return an Iterator.
     */
    @Override
    Iterator<T> iterator();

    @Override
    void close() throws DataAccessException;

  }

  /**
   * Stores objects into the database. If the object is not contained in the
   * database, a new database entry is created.
   * 
   * @param t
   *          the object to persist
   * @throws DataAccessException
   *           if accessing the database failed or if one of the objects does
   *           not provide an id
   */
  void update( T... t ) throws DataAccessException;

  /**
   * Deletes objects from the database.
   * 
   * @param t
   *          the objects to delete.
   * @throws DataAccessException
   *           if accessing the database failed or if one of the objects does
   *           not provide an id
   */
  void delete( T... t ) throws DataAccessException;

  /**
   * Deletes objects from the database based on a query. This can a
   * driver-dependent shortcut to avoid loading all objects from the database to
   * delete them.<br/>
   * <br/>
   * <b>Please notes:</b><br/>
   * This does not fire Data Access Events.
   * 
   * @param query
   *          the query that selects objects to delete.
   * @throws DataAccessException
   *           if accessing the database failed or if one of the objects does
   *           not provide an id
   */
  void delete( Query query ) throws DataAccessException;

  /* *********************************
   *  F I N D I N G   M E T H O D S  *
   ********************************* */

  /**
   * Searches for all instances.
   * 
   * @param query
   *          the query
   * @return the search result
   * @throws DataAccessException
   *           if accessing the database failed
   * @since 2.0
   */
  SearchResult<T> findAll( Query query ) throws DataAccessException;

  /**
   * Searches for a single object.
   * 
   * @return the result
   * @throws DataAccessException
   *           if accessing the database failed
   */
  T find() throws DataAccessException;

  /**
   * Searches for a single object with an object restricting the query.
   * 
   * @param query
   *          the query
   * @return the result
   * @throws DataAccessException
   *           if accessing the database failed
   */
  T find( Query query ) throws DataAccessException;

  /**
   * Searches for only one object of a special class with a special id. This id
   * is unique in the database, so only one result can be found.<br/>
   * <br/>
   * <b>Please note:</b><br/>
   * The id is the object that is given by {@link Identificator#getId(String)}
   * or it's String representation.
   * 
   * @param id
   *          the unique id
   * @return the found object
   * @throws DataAccessException
   *           if accessing the database failed
   * @see Identificator#getId(String)
   */
  T findById( Object id ) throws DataAccessException;

  /**
   * Closes the accessor.
   * 
   * @throws DataAccessException
   */
  @Override
  void close() throws DataAccessException;

  /**
   * Returns the bean model.
   * 
   * @return the bean model
   */
  BeanModel getBeanModel();

  /* ***********************************
   *  L I S T E N E R   M E T H O D S  *
   *********************************** */

  /**
   * Adds a DataAccessListener. DataAccessListeners are not invoked when a
   * cached query result is read.
   * 
   * @param listener
   *          the listener
   * @return true, if the listener was added to the listener registry
   */
  boolean addDataAccessListener( DataAccessListener<T> listener );

  /**
   * Removes a DataAccessListener.
   * 
   * @param listener
   *          the listener
   * @return true, if the listener was removed from the listener registry
   */
  boolean removeDataAccessListener( DataAccessListener<T> listener );

}