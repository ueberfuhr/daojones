package de.ars.daojones.runtime.query;

/**
 * A wrapper for a criterion.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface SearchCriterionWrapper {

  /**
   * Returns the unwrapped search criterion.
   * 
   * @return the unwrapped search criterion
   */
  public SearchCriterion getUnwrapped();

}
