package de.ars.daojones.runtime.spi.database;

import de.ars.daojones.runtime.beans.identification.Identificator;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.ConnectionMetaData;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.query.Query;

/**
 * A connection to a database that is responsible to read and write objects of a
 * special type.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the bean type that is handled by this connection
 */
public interface Connection<T> {

  /**
   * Returns some meta information about this connection.
   * 
   * @return the metadata
   */
  ConnectionMetaData<T> getMetaData();

  /**
   * Executes a query.
   * 
   * @param query
   *          the query
   * @param beanModels
   *          the bean models
   * @return the search result
   * @throws DataAccessException
   */
  SearchResult<DatabaseEntry> doFind( Query query, BeanModel... beanModels ) throws DataAccessException;

  /**
   * Creates an entry. The entry is not existing within the database unless it
   * is stored for the first time.
   * 
   * @param model
   *          the model
   * @return the entry
   * @throws DataAccessException
   */
  DatabaseEntry doCreate( final BeanModel model ) throws DataAccessException;

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
  DatabaseEntry doFindById( final Object id ) throws DataAccessException;

  /**
   * Closes the connection.
   * 
   * @throws DataAccessException
   */
  void close() throws DataAccessException;

}
