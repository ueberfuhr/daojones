package de.ars.daojones.annotations;

import java.io.Serializable;

import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;

/**
 * A selector that finds the column name based on the default column name.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface ColumnSelector extends Serializable {

  /**
   * Returns the column names that should be used.
   * 
   * @param dao
   *          the Dao on which the selector is called
   * 
   * @param base
   *          the base column name
   * @return the column name or null, if the method is called as fallback, but
   *         there is no further column
   * @throws DataAccessException
   *           if an error occurs during column name calculation
   */
  public String[] getColumns( Dao dao, String base ) throws DataAccessException;

  /**
   * Returns a flag indicating whether the database value is valid or the next
   * column should be used.
   * 
   * @param dao
   *          the dao
   * @param base
   *          the base column name
   * @param column
   *          the resolved column name
   * @param fieldValue
   *          the field value
   * @return true, if the database value is valid
   * @throws DataAccessException
   */
  public boolean isValid( Dao dao, String base, String column, Object fieldValue )
      throws DataAccessException;

  /**
   * Returns all possible column names. This is called when searching for column
   * values.
   * 
   * @param base
   *          the base column name
   * @return all possible column names
   * @throws DataAccessException
   *           if an error occurs during column name calculation
   */
  public String[] getQueryColumns( String base ) throws DataAccessException;
}
