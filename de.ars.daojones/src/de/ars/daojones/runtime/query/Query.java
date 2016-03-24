package de.ars.daojones.runtime.query;

import java.io.Serializable;
import java.util.Arrays;

import de.ars.daojones.runtime.Dao;

/**
 * A factory that creates {@link Query} objects.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@SuppressWarnings( "unchecked" )
public final class Query implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Class<? extends Dao>[] BEANTYPES_DEFAULT = new Class[0];
  private static final int MAXCOUNTOFRESULTS_DEFAULT = Integer.MAX_VALUE;
  private static final SearchCriterion SEARCHCRITERION_DEFAULT = new TautologySearchCriterion();

  private final Class<? extends Dao>[] beanTypes;
  private final SearchCriterion searchCriterion;
  private final int maxCountOfResults;

  private Query( Class<? extends Dao>[] beanTypes,
      SearchCriterion searchCriterion, int maxCountOfResults ) {
    super();
    this.beanTypes = beanTypes;
    this.searchCriterion = searchCriterion;
    this.maxCountOfResults = maxCountOfResults;
  }

  /**
   * Returns the bean types.
   * 
   * @return the bean types
   */
  public Class<? extends Dao>[] getBeanTypes() {
    return beanTypes;
  }

  /**
   * Returns the maximum number of results.
   * 
   * @return the maximum number of results
   */
  public int getMaxCountOfResults() {
    return maxCountOfResults;
  }

  /**
   * Returns the search criterion. If no criterion is specified, the
   * {@link TautologySearchCriterion} is returned.
   * 
   * @return the search criterion
   */
  public SearchCriterion getCriterion() {
    return searchCriterion;
  }

  private static SearchCriterion toSingleCriterion( SearchCriterion... criteria ) {
    return null != criteria && criteria.length > 0 ? LogicalSearchCriterion
        .toSearchCriterion( LogicalCombination.AND, criteria )
        : SEARCHCRITERION_DEFAULT;
  }

  /**
   * Restricts the query by the usage of search criteria.
   * 
   * @param criteria
   *          the criteria
   * @return the query
   */
  public static Query create( final SearchCriterion... criteria ) {
    return new Query( BEANTYPES_DEFAULT, toSingleCriterion( criteria ),
        MAXCOUNTOFRESULTS_DEFAULT );
  }

  /**
   * Restricts the query result to contain a maximum number of results.
   * 
   * @param maxCountOfResults
   *          the maximum number of results
   * @return the new query
   */
  public Query withCountOfResults( int maxCountOfResults ) {
    if ( maxCountOfResults <= 0 )
      throw new IllegalArgumentException(
          "Maximum counts of results must be greater than zero!" );
    return new Query( getBeanTypes(), getCriterion(), maxCountOfResults );
  }

  /**
   * Restricts or extends the query to a given set of types. Abstract classes
   * will occur in empty result collections.
   * 
   * @param types
   *          the types
   * @return the new query
   */
  public Query withTypes( Class<? extends Dao>... types ) {
    return new Query( types, getCriterion(), getMaxCountOfResults() );
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Query[beanTypes=" + this.beanTypes + ",criterion="
        + this.searchCriterion + ",maxCountOfResults=" + this.maxCountOfResults
        + "]";
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode( beanTypes );
    result = prime * result + maxCountOfResults;
    result = prime * result
        + ( ( searchCriterion == null ) ? 0 : searchCriterion.hashCode() );
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
    Query other = ( Query ) obj;
    if ( !Arrays.equals( beanTypes, other.beanTypes ) )
      return false;
    if ( maxCountOfResults != other.maxCountOfResults )
      return false;
    if ( searchCriterion == null ) {
      if ( other.searchCriterion != null )
        return false;
    } else if ( !searchCriterion.equals( other.searchCriterion ) )
      return false;
    return true;
  }

}
