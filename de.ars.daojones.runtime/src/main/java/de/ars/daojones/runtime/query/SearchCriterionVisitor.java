package de.ars.daojones.runtime.query;

/**
 * A visitor of search criteria. The visitor provides callback methods while
 * stepping through the criterion hierarchy.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 2.0
 */
public abstract class SearchCriterionVisitor {

  /**
   * This method is invoked before visiting the children of the criterion. For
   * each invocation of {@link #preVisit(SearchCriterion)},
   * {@link #postVisit(SearchCriterion)} is invoked too after visiting the
   * children of the search criterion.
   * 
   * @param criterion
   *          the criterion
   * @return <tt>true</tt>, if the children should be visited too
   */
  public boolean preVisit( final SearchCriterion criterion ) {
    return true;
  }

  /**
   * This method is invoked after visiting the criterion. This is independent
   * from the result of {@link #preVisit(SearchCriterion)}.
   * 
   * @param criterion
   *          the criterion
   */
  public void postVisit( final SearchCriterion criterion ) {
    // empty
  }

}
