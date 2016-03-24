package de.ars.daojones.runtime.query;

import java.text.MessageFormat;

/**
 * A kind of {@link Comparison} comparing boolean values.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public enum BooleanComparison implements Comparison<Boolean> {

  /**
   * The equals operator.
   */
  EQUALS( "boolean.equals" );

  private final String key;

  private BooleanComparison( final String key ) {
    this.key = key;
  }

  static String getObject( final TemplateManager templateManager, Boolean value ) {
    if ( null == value )
      return templateManager.getTemplate( "null.value" );
    return templateManager.getTemplate( "boolean.format."
        + ( value.booleanValue() ? "true" : "false" ) );
  }

  /**
   * Returns a string that is part of the query language of the target system.
   * 
   * @param templateManager
   *          the {@link TemplateManager}
   * @param fieldName
   *          the name of the field
   * @param value
   *          the value for comparison
   * @return the part of the query language
   * @see de.ars.daojones.runtime.query.Comparison#toQuery(TemplateManager,
   *      String, Object)
   */
  public String toQuery( TemplateManager templateManager, String fieldName,
      Boolean value ) {
    return new MessageFormat( templateManager.getTemplate( key ) )
        .format( new Object[] { fieldName, getObject( templateManager, value ) } );
  }

}
