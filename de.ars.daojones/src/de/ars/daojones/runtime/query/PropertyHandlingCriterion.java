package de.ars.daojones.runtime.query;

import static de.ars.daojones.runtime.query.LoggerConstants.DEBUG;
import static de.ars.daojones.runtime.query.LoggerConstants.getLogger;
import de.ars.daojones.annotations.model.ColumnInfo;
import de.ars.daojones.beans.model.IBean;
import de.ars.daojones.beans.model.IProperty;
import de.ars.daojones.beans.model.impl.reflect.Bean;
import de.ars.daojones.runtime.Dao;

/**
 * A {@link SearchCriterion} to search for field values in the database.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
abstract class PropertyHandlingCriterion implements SearchCriterion {

  private static final long serialVersionUID = -5259386253037301289L;
  private final String property;
  private final boolean resolveColumn;

  private static String toFirstLowerCase( String text ) {
    return null == text || text.length() < 2 ? text : Character
        .toLowerCase( text.charAt( 0 ) )
        + text.substring( 1 );
  }

  /**
   * Creates an instance.
   * 
   * @param property
   *          the name of the property of the bean.
   */
  public PropertyHandlingCriterion( final String property ) {
    this( property, true );
  }

  /**
   * Creates an instance.
   * 
   * @param property
   *          the name of the property of the bean.
   * @param resolveColumn
   *          a flag indicating whether the property is a bean property (true)
   *          or an original column name
   */
  public PropertyHandlingCriterion( final String property,
      final boolean resolveColumn ) {
    super();
    this.property = resolveColumn ? toFirstLowerCase( property ) : property;
    this.resolveColumn = resolveColumn;
  }

  /**
   * Returns the name of the property of the bean.
   * 
   * @return the name of the property of the bean
   */
  protected String getProperty() {
    return this.property;
  }

  /**
   * Returns the flag indicating whether the property is a bean property (true)
   * or an original column name.
   * 
   * @return the flag indicating whether the property is a bean property (true)
   *         or an original column name
   */
  protected boolean isResolveColumn() {
    return this.resolveColumn;
  }

  private ColumnInfo getColumnFromModel( Class<? extends Dao> theGenericClass ) {
    final IBean bean = new Bean( theGenericClass );
    for ( IProperty prop : bean.getProperties() ) {
      if ( prop.getName().equals( getProperty() ) ) {
        return prop.getColumn();
      }
    }
    return new ColumnInfo( getProperty(), null );
  }

  /**
   * Resolves the {@link ColumnInfo} of a field by searching the properties of
   * the bean class.
   * 
   * @param theGenericClass
   *          the class of the bean
   * @return the {@link ColumnInfo}
   */
  protected ColumnInfo getColumn( Class<? extends Dao> theGenericClass ) {
    return isResolveColumn() ? getColumnFromModel( theGenericClass )
        : new ColumnInfo( getProperty(), null );
  }

  /**
   * Returns the name of the column of the property.
   * 
   * @param resolver
   *          the {@link VariableResolver}
   * @return the name of the column of the property
   * @throws VariableResolvingException
   */
  protected String[] getQueryColumns( final VariableResolver resolver )
      throws VariableResolvingException {
    String[] columnResolved = resolver.resolveColumn( getColumn( resolver
        .getTheGenericClass() ) );
    if ( null == columnResolved || columnResolved.length == 0 ) {
      columnResolved = new String[] { getProperty() };
      getLogger().log(
          DEBUG,
          "The property \"" + getProperty()
              + "\" does not exist or is not annotated with @Column, so \""
              + getProperty() + "\" is used." );
    }
    return columnResolved;
  }

  /**
   * Creates a query string for a single column.
   * 
   * @param templateManager
   *          the {@link TemplateManager}
   * @param resolver
   *          the {@link VariableResolver}
   * @param column
   *          the column
   * @return the query string
   * @throws VariableResolvingException
   */
  protected abstract String toQuery( final TemplateManager templateManager,
      final VariableResolver resolver, String column )
      throws VariableResolvingException;

  /**
   * @see de.ars.daojones.runtime.query.SearchCriterion#toQuery(de.ars.daojones.runtime.query.TemplateManager,
   *      de.ars.daojones.runtime.query.VariableResolver)
   */
  public String toQuery( final TemplateManager templateManager,
      final VariableResolver resolver ) throws VariableResolvingException {
    final String[] columns = getQueryColumns( resolver );
    if ( null == columns || columns.length == 0 ) {
      throw new VariableResolvingException( "No column could be resolved!" );
    } else {
      String result = toQuery( templateManager, resolver, columns[0] );
      for ( int i = 1; i < columns.length; i++ ) {
        final String part2 = toQuery( templateManager, resolver, columns[i] );
        result = LogicalCombination.OR.toQuery( templateManager, result, part2 );
      }
      return result;
    }
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + ( ( property == null ) ? 0 : property.hashCode() );
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
    final PropertyHandlingCriterion other = ( PropertyHandlingCriterion ) obj;
    if ( property == null ) {
      if ( other.property != null )
        return false;
    } else if ( !property.equals( other.property ) )
      return false;
    return true;
  }

}
