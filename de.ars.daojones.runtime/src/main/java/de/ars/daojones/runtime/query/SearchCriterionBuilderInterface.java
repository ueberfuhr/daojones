package de.ars.daojones.runtime.query;

/**
 * A common interface providing methods that can be invoked to create a search
 * criterion within the chained calls pattern at a position where no special
 * context is given.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
interface SearchCriterionBuilderInterface {

  /**
   * Returns a tautology.
   * 
   * @return the tautology
   */
  ExtensibleSearchCriterion TRUE();

  /**
   * Returns a negative tautology.
   * 
   * @return the negative tautology
   */
  ExtensibleSearchCriterion FALSE();

  /**
   * Creates a negotiation of a search criterion.
   * 
   * @param c
   *          the search criterion that is negotiated
   * @return the negotiation of a search criterion
   */
  ExtensibleSearchCriterion not( SearchCriterion c );

  /**
   * Binds a search criterion for the usage with this chained calls API. This
   * can also be used to parenthesize multiple criteria for logical combination.
   * 
   * @param c
   *          the search criterion
   * @return the search criterion with chained calls API
   */
  ExtensibleSearchCriterion with( SearchCriterion c );

  /**
   * Creates a search criterion that is related to a single field of the bean.
   * 
   * @param field
   *          the name of the field
   * @return the search criterion builder to create the search criterion
   */
  SingleFieldSearchCriterionBuilder field( String field );

}
