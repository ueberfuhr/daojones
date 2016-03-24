package de.ars.daojones.runtime.query;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

/**
 * A factory that creates query objects.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public final class Query implements Serializable, Cloneable {

  /**
   * The default value of bean types. This indicates that there is only one bean
   * type to be searched for - the bean type that the connection handles.
   */
  public static final Class<?>[] BEANTYPES_DEFAULT = new Class[0];
  private static final long serialVersionUID = 1L;
  private static final int MAXCOUNTOFRESULTS_DEFAULT = Integer.MAX_VALUE;
  private static final SearchCriterion SEARCHCRITERION_DEFAULT = new Tautology();
  private static final Parameter[] PARAMETERS_DEFAULT = new Parameter[0];

  private Class<?>[] beanTypes = Query.BEANTYPES_DEFAULT;
  private Parameter[] parameters = Query.PARAMETERS_DEFAULT;
  private SearchCriterion searchCriterion = Query.SEARCHCRITERION_DEFAULT;
  private int maxCountOfResults = Query.MAXCOUNTOFRESULTS_DEFAULT;

  /**
   * A driver-specific query parameter that affects the execution of the query.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
   * @since 2.0
   */
  public static interface Parameter extends Serializable {
  }

  // Use create() method and withXXX() methods (Chained Calls) instead
  private Query() {
    super();
  }

  /**
   * Returns the bean types.
   * 
   * @return the bean types
   */
  public Class<?>[] getBeanTypes() {
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
   * {@link Tautology} is returned.
   * 
   * @return the search criterion
   */
  public SearchCriterion getCriterion() {
    return searchCriterion;
  }

  /**
   * Returns the query parameters. Those parameters are typically
   * driver-specific and can affect the execution of the query.
   * 
   * @return the query parameters
   */
  public Parameter[] getParameters() {
    return parameters;
  }

  private static SearchCriterion toSingleCriterion( final SearchCriterion... criteria ) {
    return null != criteria && criteria.length > 0 ? LogicalCombination.toSearchCriterion( LogicalCombinationType.AND,
            criteria ) : Query.SEARCHCRITERION_DEFAULT;
  }

  /**
   * Creates a query.
   * 
   * The query will by default search for the bean type and all subtypes and
   * does not restrict the maximum count of search results.
   * 
   * @return the query
   */
  public static Query create() {
    return new Query();
  }

  /**
   * Restricts the query result to contain a maximum number of results.
   * 
   * @param maxCountOfResults
   *          the maximum number of results
   * @return the query (Chained Calls)
   */
  public Query only( final int maxCountOfResults ) {
    if ( maxCountOfResults > 0 ) {
      this.maxCountOfResults = maxCountOfResults;
      return this;
    } else {
      throw new IllegalArgumentException( "Maximum counts of results must be greater than zero!" );
    }
  }

  /**
   * Restricts the query to a given set of types. Abstract classes will occur in
   * empty result collections.
   * 
   * @param beanTypes
   *          the types
   * @return the query (Chained Calls)
   */
  public Query only( final Class<?>... beanTypes ) {
    this.beanTypes = beanTypes;
    // Necessary for comparison
    Arrays.sort( this.beanTypes, new Comparator<Class<?>>() {

      @Override
      public int compare( final Class<?> c1, final Class<?> c2 ) {
        return c1.getName().compareTo( c2.getName() );
      }
    } );
    return this;
  }

  /**
   * Sets query parameters to the query. Those parameters are typically
   * driver-specific and can affect the execution of the query.
   * 
   * @param parameters
   *          the query parameters
   * @return the query (Chained Calls)
   */
  public Query withParameters( final Parameter... parameters ) {
    this.parameters = parameters;
    return this;
  }

  /**
   * Restricts the query to a given set of search criteria.
   * 
   * @param criteria
   *          the search criteria
   * @return the query (Chained Calls)
   */
  public Query only( final SearchCriterion... criteria ) {
    Query.normalize( criteria );
    searchCriterion = Query.toSingleCriterion( criteria );
    return this;
  }

  private static void normalize( final SearchCriterion[] criteria ) {
    for ( int i = 0; i < criteria.length; i++ ) {
      criteria[i] = SearchCriterionBuilder.unwrap( criteria[i] );
    }
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Query[beanTypes=" + Arrays.toString( beanTypes ) + ", criterion=" + searchCriterion
            + ", maxCountOfResults=" + maxCountOfResults + "]";
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
    result = prime * result + ( ( searchCriterion == null ) ? 0 : searchCriterion.hashCode() );
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
    final Query other = ( Query ) obj;
    if ( !Arrays.equals( beanTypes, other.beanTypes ) ) {
      return false;
    }
    if ( maxCountOfResults != other.maxCountOfResults ) {
      return false;
    }
    if ( searchCriterion == null ) {
      if ( other.searchCriterion != null ) {
        return false;
      }
    } else if ( !searchCriterion.equals( other.searchCriterion ) ) {
      return false;
    }
    return true;
  }

  @Override
  public Query clone() {
    return Query.create().only( getBeanTypes() ).only( getMaxCountOfResults() ).only( getCriterion() )
            .withParameters( getParameters() );
  }

}
