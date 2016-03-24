package de.ars.daojones.runtime.query;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.Locale;

/**
 * A kind of {@link Comparison} comparing numbers.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public enum NumberComparison implements Comparison<Number> {

  /**
   * The equals operator.
   */
  EQUALS( "number.equals" ),
  /**
   * The lower than operator.
   */
  LOWERTHAN( "number.lower" ),
  /**
   * The greater that operator.
   */
  GREATERTHAN( "number.greater" );

  private final String key;

  private NumberComparison( final String key ) {
    this.key = key;
  }

  static String getObject( final TemplateManager templateManager, Number value ) {
    if ( null == value )
      return templateManager.getTemplate( "null.value" );
    return templateManager.getTemplate( "number.object" ).replaceAll(
        "\\{0\\}",
        new DecimalFormat( templateManager.getTemplate( "number.format" ),
            new DecimalFormatSymbols( Locale.ENGLISH ) ).format( value ) );
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
   * @see de.ars.daojones.runtime.query.Comparison#toQuery(de.ars.daojones.runtime.query.TemplateManager,
   *      java.lang.String, java.lang.Object)
   */
  public String toQuery( TemplateManager templateManager, String fieldName,
      Number value ) {
    return new MessageFormat( templateManager.getTemplate( key ) )
        .format( new Object[] { fieldName, getObject( templateManager, value ) } );
  }

}
