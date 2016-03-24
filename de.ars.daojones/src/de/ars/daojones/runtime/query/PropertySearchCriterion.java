package de.ars.daojones.runtime.query;

/**
 * A {@link SearchCriterion} searching for field values in the database.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * 
 * @param <T>
 *          the type of the DaoJones bean
 */
public class PropertySearchCriterion<T> extends PropertyHandlingCriterion {

  private static final long serialVersionUID = -8100827048242669056L;
  private final Comparison<T> comparison;
  private T value;

  /**
   * Creates an instance.
   * 
   * @param property
   *          the name of the property of the bean.
   * @param comparison
   *          the {@link Comparison}
   * @param value
   *          the value used for comparison
   * @param resolveColumn
   *          a flag indicating whether the property is a bean property (true)
   *          or an original column name
   */
  public PropertySearchCriterion( final String property,
      final Comparison<T> comparison, T value, boolean resolveColumn ) {
    super( property, resolveColumn );
    this.comparison = comparison;
    this.value = ( comparison instanceof TransformingComparison ? ( ( TransformingComparison<T> ) comparison )
        .transformForComparison( value )
        : value );
  }

  /**
   * Creates an instance.
   * 
   * @param property
   *          the name of the property of the bean.
   * @param comparison
   *          the {@link Comparison}
   * @param value
   *          the value used for comparison
   */
  public PropertySearchCriterion( final String property,
      final Comparison<T> comparison, T value ) {
    this( property, comparison, value, true );
  }

  /**
   * Returns the {@link Comparison}.
   * 
   * @return the {@link Comparison}
   */
  protected Comparison<T> getComparison() {
    return comparison;
  }

  /**
   * Returns the value used for comparison.
   * 
   * @return the value used for comparison
   */
  public T getValue() {
    return value;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result
        + ( ( getProperty() == null ) ? 0 : getProperty().hashCode() );
    result = PRIME * result + Boolean.valueOf( isResolveColumn() ).hashCode();
    result = PRIME * result
        + ( ( comparison == null ) ? 0 : comparison.hashCode() );
    result = PRIME * result + ( ( value == null ) ? 0 : value.hashCode() );
    return result;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @SuppressWarnings( "unchecked" )
  @Override
  public boolean equals( Object obj ) {
    if ( this == obj )
      return true;
    if ( obj == null )
      return false;
    if ( getClass() != obj.getClass() )
      return false;
    final PropertySearchCriterion other = ( PropertySearchCriterion ) obj;
    if ( getProperty() == null ) {
      if ( other.getProperty() != null )
        return false;
    } else if ( !getProperty().equals( other.getProperty() ) )
      return false;
    if ( isResolveColumn() != other.isResolveColumn() )
      return false;
    if ( comparison == null ) {
      if ( other.comparison != null )
        return false;
    } else if ( !comparison.equals( other.comparison ) )
      return false;
    if ( value == null ) {
      if ( other.value != null )
        return false;
    } else if ( !value.equals( other.value ) )
      return false;
    return true;
  }

  /**
   * @see PropertyHandlingCriterion#toQuery(TemplateManager, VariableResolver,
   *      String)
   */
  @Override
  protected String toQuery( TemplateManager templateManager,
      VariableResolver resolver, String column )
      throws VariableResolvingException {
    return getComparison().toQuery( templateManager, column, getValue() );
  }

  /**
   * Combines this {@link SearchCriterion} with another one by combining the
   * {@link Comparison}s.
   * 
   * @param c2
   *          the second {@link Comparison}
   * @return the combined {@link SearchCriterion}
   */
  public SearchCriterion or( Comparison<T> c2 ) {
    return combine( LogicalCombination.OR, c2 );
  }

  /**
   * Combines this {@link SearchCriterion} with another one by combining the
   * {@link Comparison}s. This method is private because the combination with
   * AND would not make sense.
   * 
   * @param comb
   *          the {@link LogicalCombination}
   * @param c2
   *          the second {@link Comparison}
   * @return the combined {@link SearchCriterion}
   */
  private SearchCriterion combine( LogicalCombination comb, Comparison<T> c2 ) {
    return combine( comb, c2, this.value );
  }

  /**
   * Combines this {@link SearchCriterion} with another one by comparing the
   * same property with another value and {@link Comparison}.
   * 
   * @param comb
   *          the {@link LogicalCombination} (AND, OR)
   * @param c2
   *          the second {@link Comparison}
   * @param value
   *          the second value
   * @return the combined {@link SearchCriterion}
   */
  public SearchCriterion combine( LogicalCombination comb, Comparison<T> c2,
      T value ) {
    final PropertySearchCriterion<T> crit2 = new PropertySearchCriterion<T>(
        getProperty(), c2, value );
    return new LogicalSearchCriterion( this, comb, crit2 );
  }

}
