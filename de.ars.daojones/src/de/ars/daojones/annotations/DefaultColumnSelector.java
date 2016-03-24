package de.ars.daojones.annotations;

import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;

/**
 * Default {@link ColumnSelector} implementation that simply returns the base
 * column name.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 */
public class DefaultColumnSelector implements ColumnSelector {

  private static final long serialVersionUID = 1L;

  /**
   * @see de.ars.daojones.annotations.ColumnSelector#getQueryColumns(java.lang.String)
   */
  public String[] getQueryColumns( String base ) throws DataAccessException {
    return new String[] { base };
  }

  /**
   * @see ColumnSelector#getColumns(Dao, String)
   */
  public String[] getColumns( Dao dao, String base ) throws DataAccessException {
    return new String[] { base };
  }

  /**
   * @see ColumnSelector#isValid(Dao, String, String, Object)
   */
  public boolean isValid( Dao dao, String base, String column, Object fieldValue )
      throws DataAccessException {
    return true;
  }

}
