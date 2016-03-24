package de.ars.daojones.runtime.query;

/**
 * A helper class that can be used to build search criteria for queries. This
 * helper class provides chained calls, so you can build a query within a single
 * line, as the following example shows:<br/>
 * <br/>
 * <code>
 * <b>import static</b> de.ars.daojones.runtime.query.SearchCriterionBuilder.*;<br/>
 * <b>import</b> de.ars.daojones.runtime.query.Query;<br/>
 * ...<br/>
 * SearchCriterion c = field("title").asString().isEqualTo("Faust").<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;and().not(field("owner").isEmpty())<br/>
 * </code> <br/>
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public final class SearchCriterionBuilder {

  // Cached instance.
  private static final SearchCriterionBuilderInterface theInstance = new SearchCriterionBuilderImpl();

  private SearchCriterionBuilder() {
    super();
  }

  /**
   * Returns a tautology.
   * 
   * @return the tautology
   */
  public static final ExtensibleSearchCriterion TRUE() {
    return SearchCriterionBuilder.theInstance.TRUE();
  }

  /**
   * Returns a negative tautology.
   * 
   * @return the negative tautology
   */
  public static ExtensibleSearchCriterion FALSE() {
    return SearchCriterionBuilder.theInstance.FALSE();
  }

  /**
   * Creates a negotiation of a search criterion.
   * 
   * @param c
   *          the search criterion that is negotiated
   * @return the negotiation of a search criterion
   */
  public static ExtensibleSearchCriterion not( final SearchCriterion c ) {
    return SearchCriterionBuilder.theInstance.not( c );
  }

  /**
   * Binds a search criterion for the usage with this chained calls API. This
   * can also be used to parenthesize multiple criteria for logical combination.
   * 
   * @param c
   *          the search criterion
   * @return the search criterion with chained calls API
   */
  public static ExtensibleSearchCriterion with( final SearchCriterion c ) {
    return SearchCriterionBuilder.theInstance.with( c );
  }

  /**
   * Creates a search criterion that is related to a single field of a bean.
   * 
   * @param field
   *          the name of the field
   * @return the search criterion builder to create the search criterion
   */
  public static SingleFieldSearchCriterionBuilder field( final String field ) {
    return SearchCriterionBuilder.theInstance.field( field );
  }

  static SearchCriterion unwrap( final SearchCriterion c ) {
    SearchCriterion result = c;
    while ( result instanceof SearchCriterionWrapper ) {
      result = ( ( SearchCriterionWrapper ) result ).getUnwrapped();
    }
    return result;
  }

}
