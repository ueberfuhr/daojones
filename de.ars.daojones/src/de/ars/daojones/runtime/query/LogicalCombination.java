package de.ars.daojones.runtime.query;

import java.text.MessageFormat;

/**
 * An enumeration handling logical combinations of {@link SearchCriterion}
 * instances.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public enum LogicalCombination {

  /**
   * The AND operator.
   */
  AND( "logical.and" ),
  /**
   * The OR operator.
   */
  OR( "logical.or" );

  private final String key;

  private LogicalCombination( final String key ) {
    this.key = key;
  }

  /**
   * Creates the part of the query.
   * 
   * @param templateManager
   *          the {@link TemplateManager}
   * @param c1
   *          the query for criterion 1
   * @param c2
   *          the query for criterion 2
   * @return the part of the query
   */
  public String toQuery( TemplateManager templateManager, String c1, String c2 ) {
    return new MessageFormat( templateManager.getTemplate( key ) )
        .format( new Object[] { c1, c2 } );
  }

}
