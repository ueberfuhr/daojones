package de.ars.daojones.drivers.notes;

import de.ars.daojones.annotations.model.ColumnInfo;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.query.VariableResolver;
import de.ars.daojones.runtime.query.VariableResolvingException;

/**
 * A class resolving table names and columns. This is the simplier case because
 * reolving means only use the original name.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class TableVariableResolver extends VariableResolver {

  /**
   * Creates a table {@link VariableResolver}.
   * 
   * @param theGenericClass
   *          the class of the object to analyze.
   */
  public TableVariableResolver( Class<? extends Dao> theGenericClass ) {
    super( theGenericClass );
  }

  /**
   * Resolves the column name.
   * 
   * @param column
   *          the column
   * @see VariableResolver#resolveColumn(ColumnInfo)
   */
  @Override
  public String[] resolveColumn( ColumnInfo column )
      throws VariableResolvingException {
    try {
      return null != column ? column.getQueryColumns() : null;
    } catch ( DataAccessException e ) {
      throw new VariableResolvingException( e );
    }
  }

}
