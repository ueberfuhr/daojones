package de.ars.daojones.drivers.notes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;
import de.ars.daojones.annotations.model.ColumnInfo;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.query.VariableResolver;
import de.ars.daojones.runtime.query.VariableResolvingException;

/**
 * A class resolving table names and columns by searching a view for column and
 * selection formulas.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class ViewVariableResolver extends VariableResolver {

  private final View view;

  /**
   * Creates a {@link ViewVariableResolver} instance.
   * 
   * @param theGenericClass
   *          the class to be analyzed
   * @param view
   *          the view to be searched for column and selection formulas
   */
  public ViewVariableResolver( Class<? extends Dao> theGenericClass,
      final View view ) {
    super( theGenericClass );
    this.view = view;
  }

  private View getView() {
    return this.view;
  }

  /**
   * Resolves the column name.
   * 
   * @param column
   *          the column
   * @see VariableResolver#resolveColumn(ColumnInfo)
   */
  @SuppressWarnings( "unchecked" )
  @Override
  public String[] resolveColumn( ColumnInfo column )
      throws VariableResolvingException {
    try {
      if ( null == column )
        return null;
      final String[] queryColumns = column.getQueryColumns();
      final Set<String> result = new HashSet<String>();
      for ( int i = 0; i < queryColumns.length; i++ ) {
        final String queryColumn = queryColumns[i];
        for ( ViewColumn col : ( Vector<ViewColumn> ) getView().getColumns() ) {
          if ( queryColumn.equalsIgnoreCase( col.getTitle() ) ) {
            if ( col.isField() ) {
              result.add( col.getItemName() );
              break;
            } else if ( col.isFormula() ) {
              result.add( "(" + col.getFormula() + ")" );
              break;
            } else
              throw new VariableResolvingException(
                  "The column with the name \""
                      + queryColumns
                      + "\" in view \""
                      + getView().getName()
                      + "\" has the wrong format! Only formulas or fields are allowed!" );
          }
        }
      }
      if ( result.isEmpty() ) {
        throw new VariableResolvingException(
            "No column with one of the names " + Arrays.asList( queryColumns )
                + " found in view \"" + getView().getName() + "\"!" );
      } else {
        return result.toArray( new String[result.size()] );
      }
    } catch ( NotesException e ) {
      throw new VariableResolvingException( e );
    } catch ( DataAccessException e ) {
      throw new VariableResolvingException( e );
    }
  }

}
