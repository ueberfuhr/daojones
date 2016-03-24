package de.ars.daojones.runtime.query;

import java.util.Arrays;

import de.ars.daojones.runtime.beans.fields.FieldAccessException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.context.ApplicationContext;

/**
 * The criterion combining two criteria with a logical combination (<tt>AND</tt>
 * , <tt>OR</tt>).
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 1.0
 */
public class LogicalCombination extends AbstractSearchCriterion {

  private static final long serialVersionUID = 1L;

  private final SearchCriterion left;
  private final SearchCriterion right;
  private final LogicalCombinationType type;

  /**
   * Creates an instance.
   * 
   * @param left
   *          the first {@link SearchCriterion}
   * @param type
   *          the {@link LogicalCombinationType}
   * @param right
   *          the second {@link SearchCriterion}
   */
  public LogicalCombination( final SearchCriterion left, final LogicalCombinationType type, final SearchCriterion right ) {
    super();
    this.left = AbstractSearchCriterion.unwrap( left );
    this.type = type;
    this.right = AbstractSearchCriterion.unwrap( right );
  }

  /**
   * Returns the left search criterion.
   * 
   * @return the left search criterion
   */
  public SearchCriterion getLeft() {
    return left;
  }

  /**
   * Returns the right search criterion.
   * 
   * @return the right search criterion
   */
  public SearchCriterion getRight() {
    return right;
  }

  /**
   * Returns the type.
   * 
   * @return the type
   */
  public LogicalCombinationType getType() {
    return type;
  }

  private static SearchCriterion toSearchCriterion( final int index, final LogicalCombinationType type,
          final SearchCriterion... criterions ) {
    if ( index == criterions.length - 1 ) {
      return criterions[index];
    }
    if ( index >= criterions.length ) {
      return new Tautology();
    }
    return new LogicalCombination( criterions[index], type, LogicalCombination.toSearchCriterion( index + 1, type,
            criterions ) );
  }

  /**
   * Combines a couple of search criteria instances with one type
   * 
   * @param type
   *          the type
   * @param criteria
   *          the search criteria
   * @return the combined search criterion
   */
  public static SearchCriterion toSearchCriterion( final LogicalCombinationType type, final SearchCriterion... criteria ) {
    if ( null == criteria || criteria.length < 1 ) {
      return new Tautology();
    }
    return LogicalCombination.toSearchCriterion( 0, type, criteria );
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + getType().name().hashCode();
    result = PRIME * result + ( ( left == null ) ? 0 : left.hashCode() ) + ( ( right == null ) ? 0 : right.hashCode() );
    return result;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( final Object obj ) {
    if ( this == obj ) {
      return true;
    }
    if ( obj == null ) {
      return false;
    }
    if ( getClass() != obj.getClass() ) {
      return false;
    }
    final LogicalCombination other = ( LogicalCombination ) obj;
    return getType().equals( other.getType() )
            && ( Arrays.equals( new SearchCriterion[] { left, right },
                    new SearchCriterion[] { other.left, other.right } ) || Arrays.equals( new SearchCriterion[] { left,
                right }, new SearchCriterion[] { other.right, other.left } ) );
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append( " ( " ).append( left.toString() ).append( " " ).append( type.name() ).append( " " )
            .append( right.toString() ).append( " ) " );
    return builder.toString();
  }

  @Override
  public boolean matches( final ApplicationContext ctx, final Object bean ) throws ConfigurationException,
          FieldAccessException, DataAccessException {
    final boolean firstMatches = getLeft().matches( ctx, bean );
    boolean secondMatchNecessary = true;
    final LogicalCombinationType type = getType();
    if ( LogicalCombinationType.AND == type ) {
      secondMatchNecessary = firstMatches;
    } else {
      secondMatchNecessary = !firstMatches;
    }
    if ( secondMatchNecessary ) {
      final boolean secondMatches = getRight().matches( ctx, bean );
      return secondMatches;
    } else {
      return firstMatches;
    }
  }

  @Override
  protected void acceptChildren( final SearchCriterionVisitor visitor ) {
    super.acceptChildren( visitor );
    getLeft().accept( visitor );
    getRight().accept( visitor );
  }

}
