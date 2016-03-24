package de.ars.daojones.runtime.spi.database;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * A helper class for accessing views.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public final class ViewHelper {

  private static final String COLUMN_INDEX_PREFIX = "?";

  private ViewHelper() {
    super();
  }

  /**
   * Returns the index of a column within the view.
   * 
   * @param name
   *          the name of the mapping that contains the index. Such names must
   *          consist of a <tt>?</tt> followed by the index.
   * @return the column index or <tt>null</tt>, if the name does not start with
   *         a <tt>?</tt> throws {@link ParseException} if the name starts with
   *         a <tt>?</tt> and does not contain any valid number
   */
  public static Integer getColumnIndex( final String name ) throws ParseException {
    if ( null != name && name.startsWith( ViewHelper.COLUMN_INDEX_PREFIX ) ) {
      return NumberFormat.getInstance().parse( name.substring( 1 ) ).intValue();
    } else {
      return null;
    }
  }

  /**
   * Formats the given index to the corresponding mapping name. The
   * corresponding mapping name is a <tt>?</tt> followed by the index of the
   * column.
   * 
   * @param index
   *          the column index
   * @return the corresponding mapping name
   */
  public static String toColumnName( final int index ) {
    return ViewHelper.COLUMN_INDEX_PREFIX.concat( NumberFormat.getInstance().format( index ) );
  }

}
