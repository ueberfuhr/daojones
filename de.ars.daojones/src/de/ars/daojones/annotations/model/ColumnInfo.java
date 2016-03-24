package de.ars.daojones.annotations.model;

import java.io.Serializable;

import de.ars.daojones.annotations.Column;
import de.ars.daojones.annotations.ColumnSelector;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;

/**
 * A class containing the informations of the
 * {@link de.ars.daojones.annotations.Column} annotation. This is used for the
 * driver implementation because annotations cannot be instantiated during
 * runtime.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ColumnInfo implements Serializable {

  private static final long serialVersionUID = 4723293813811038531L;
  private final String value;
  private final Class<? extends ColumnSelector> selector;

  /**
   * Creates an instance.
   * 
   * @param value
   *          the base column name
   * @param selector
   *          the column selector
   */
  public ColumnInfo( String value, Class<? extends ColumnSelector> selector ) {
    super();
    this.value = value;
    this.selector = selector;
  }

  /**
   * Creates an instance.
   * 
   * @param column
   *          the {@link Column} annotation
   */
  public ColumnInfo( Column column ) {
    this( null != column ? column.value() : null, null != column ? column
        .selector() : null );
  }

  /**
   * Returns the value.
   * 
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * Returns the column selector class.
   * 
   * @return the column selector class
   */
  public Class<? extends ColumnSelector> getSelector() {
    return selector;
  }

  /**
   * Resolves the column name.
   * 
   * @param dao
   *          the {@link Dao}
   * @return the column name
   * @throws DataAccessException
   *           if an error occurs during column resolution
   */
  public String getColumnName( Dao dao ) throws DataAccessException {
    try {
      return null != getSelector() ? getSelector().newInstance().getColumns(
          dao, getValue() )[0] : getValue();
    } catch ( DataAccessException t ) {
      throw t;
    } catch ( Throwable t ) {
      throw new DataAccessException( t );
    }
  }

  /**
   * Returns all possible column names. This is called when searching for column
   * values.
   * 
   * @return all possible column names
   * @throws DataAccessException
   *           if an error occurs during column name calculation
   */
  public String[] getQueryColumns() throws DataAccessException {
    try {
      return null != getSelector() ? getSelector().newInstance()
          .getQueryColumns( getValue() ) : new String[] { getValue() };
    } catch ( DataAccessException t ) {
      throw t;
    } catch ( Throwable t ) {
      throw new DataAccessException( t );
    }
  }

  private String getSelectorClassName() {
    return null != getSelector() ? getSelector().getName() : null;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime
        * result
        + ( ( getSelectorClassName() == null ) ? 0 : getSelectorClassName()
            .hashCode() );
    result = prime * result
        + ( ( getValue() == null ) ? 0 : getValue().hashCode() );
    return result;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object obj ) {
    if ( this == obj )
      return true;
    if ( obj == null )
      return false;
    if ( getClass() != obj.getClass() )
      return false;
    ColumnInfo other = ( ColumnInfo ) obj;
    if ( getSelectorClassName() == null ) {
      if ( other.getSelectorClassName() != null )
        return false;
    } else if ( !getSelectorClassName().equals( other.getSelectorClassName() ) )
      return false;
    if ( getValue() == null ) {
      if ( other.getValue() != null )
        return false;
    } else if ( !getValue().equals( other.getValue() ) )
      return false;
    return true;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getValue() + " [selector=" + getSelectorClassName() + "]";
  }

}
