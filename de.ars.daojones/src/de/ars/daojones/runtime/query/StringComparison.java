package de.ars.daojones.runtime.query;

import java.text.MessageFormat;

/**
 * A kind of {@link Comparison} comparing strings.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public enum StringComparison implements Comparison<String> {

  /**
   * The case-sensitive equals operator.
   */
  EQUALS( "string.equals" ),
  /**
   * The case-insensitive equals operator.
   */
  EQUALS_IGNORECASE( "string.equals", false ),
  /**
   * The case-sensitive startsWith operator.
   */
  STARTSWITH( "string.starts" ),
  /**
   * The case-insensitive startsWith operator.
   */
  STARTSWITH_IGNORECASE( "string.starts", false ),
  /**
   * The case-sensitive endsWith operator.
   */
  ENDSWITH( "string.ends" ),
  /**
   * The case-insensitive endsWith operator.
   */
  ENDSWITH_IGNORECASE( "string.ends", false ),
  /**
   * The case-sensitive contains operator.
   */
  CONTAINS( "string.contains" ),
  /**
   * The case-insensitive contains operator.
   */
  CONTAINS_IGNORECASE( "string.contains", false ),
  /**
   * The case-sensitive like operator.
   */
  LIKE( "string.like" ),
  /**
   * The case-insensitive like operator.
   */
  LIKE_IGNORECASE( "string.like", false );

  private final String key;
  private final boolean caseSensitive;

  private StringComparison( final String key ) {
    this( key, true );
  }

  private StringComparison( final String key, final boolean caseSensitive ) {
    this.key = key;
    this.caseSensitive = caseSensitive;
  }

  static String getObject( final TemplateManager templateManager, String value ) {
    if ( null == value )
      return templateManager.getTemplate( "null.value" );
    return templateManager.getTemplate( "string.object" ).replaceAll(
        "\\{0\\}", value );
  }

  private String getCase( final TemplateManager templateManager,
      final String value ) {
    return this.caseSensitive ? value : new MessageFormat( templateManager
        .getTemplate( "string.case" ) ).format( new Object[] { value } );
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
      String value ) {
    return new MessageFormat( templateManager.getTemplate( key ) )
        .format( new Object[] { getCase( templateManager, fieldName ),
            getCase( templateManager, getObject( templateManager, value ) ) } );
  }

}
