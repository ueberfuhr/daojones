package de.ars.daojones.runtime.query;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A type of {@link Comparison} comparing date values.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public enum DateComparison implements TransformingComparison<Date> {

  /**
   * The after operator only comparing the date, not the time.
   */
  DATE_AFTER( "datetime.after", "datetime.format", true ),
  /**
   * The equals operator only comparing the date, not the time.
   */
  DATE_EQUALS( "datetime.equals", "datetime.format", true ),
  /**
   * The before operator only comparing the date, not the time.
   */
  DATE_BEFORE( "datetime.before", "datetime.format", true ),
  /**
   * The after operator comparing both date and time.
   */
  TIME_AFTER( "datetime.after", "time.format", false ),
  /**
   * The equals operator comparing both date and time.
   */
  TIME_EQUALS( "datetime.equals", "time.format", false ),
  /**
   * The before operator comparing both date and time.
   */
  TIME_BEFORE( "datetime.before", "time.format", false );

  private final String key;
  private final String formatterKey;
  private final boolean isOnlyDateComparison;

  private DateComparison( final String key, final String formatterKey,
      final boolean isOnlyDateComparison ) {
    this.key = key;
    this.formatterKey = formatterKey;
    this.isOnlyDateComparison = isOnlyDateComparison;
  }

  static String getObject( final TemplateManager templateManager, Date value ) {
    return getObject( templateManager, value, "datelist.format" );
  }

  private static String getObject( final TemplateManager templateManager,
      Date value, String formatterKey ) {
    if ( null == value )
      return templateManager.getTemplate( "null.value" );
    return templateManager.getTemplate( "datetime.object" ).replaceAll(
        "\\{0\\}",
        new SimpleDateFormat( templateManager.getTemplate( formatterKey ) )
            .format( value ) );
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
  public String toQuery( final TemplateManager templateManager,
      final String fieldName, final Date value ) {
    return new MessageFormat( templateManager.getTemplate( key ) )
        .format( new Object[] { fieldName,
            getObject( templateManager, value, formatterKey ) } );
  }

  /*
   * T R A N S F O R M I N G M E C H A N I S M
   */
  private static Date removeTime( Date value ) {
    final Calendar cal = new GregorianCalendar();
    cal.setTime( value );
    cal.set( Calendar.HOUR_OF_DAY, 0 );
    cal.set( Calendar.MINUTE, 0 );
    cal.set( Calendar.SECOND, 0 );
    cal.set( Calendar.MILLISECOND, 0 );
    return cal.getTime();
  }

  /**
   * Transforms the value for comparison.
   * 
   * @param value
   *          the value
   * @return the transformed value
   * @see de.ars.daojones.runtime.query.TransformingComparison#transformForComparison(java.lang.Object)
   */
  public Date transformForComparison( Date value ) {
    return this.isOnlyDateComparison ? removeTime( value ) : value;
  }

}
