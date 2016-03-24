package de.ars.daojones.runtime.query;

/**
 * An enumeration handling logical combinations of {@link SearchCriterion}
 * instances.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public enum LogicalCombinationType {

  /**
   * The AND operator.
   */
  AND,
  /**
   * The OR operator.
   */
  OR;

}
