package de.ars.daojones.runtime.query;

/**
 * A common interface providing methods that can be invoked to create a search
 * criterion within the chained calls pattern at a position where no special
 * context is given (except further search criteria that have to be combined
 * with the new one). An implementation of this interface can be used as search
 * criterion directly.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public interface ExtensibleSearchCriterion extends SearchCriterion, SearchCriterionWrapper {

  /**
   * @deprecated Do not use this method for field comparisons.
   */
  @Override
  @Deprecated
  public boolean equals( Object o );

  /**
   * @deprecated Do not use this method for field comparisons.
   */
  @Override
  @Deprecated
  public int hashCode();

  /**
   * A logical AND combination to another search criterion.
   * 
   * @return the combination
   */
  SearchCriterionCombination and();

  /**
   * A logical OR combination to another search criterion.
   * 
   * @return the combination
   */
  SearchCriterionCombination or();

}
