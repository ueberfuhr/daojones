package de.ars.daojones.runtime.query;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * A kind of {@link Comparison} comparing collections and arrays.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@SuppressWarnings( "unchecked" )
public enum CollectionComparison implements Comparison<Collection> {

  /**
   * The equals operator.
   */
  EQUALS( "list.equals" ),
  /**
   * The contains all operator.
   */
  CONTAINS_ALL( "list.contains" ),
  /**
   * The contains one operator.
   */
  CONTAINS_ONE( "list.contains", true );

  private final String key;
  private final boolean vary;

  private CollectionComparison( final String key ) {
    this( key, false );
  }

  private CollectionComparison( final String key, final boolean vary ) {
    this.key = key;
    this.vary = vary;
  }

  private static String objectToString( TemplateManager templateManager,
      Object value ) {
    if ( null == value )
      return templateManager.getTemplate( "null.value" );
    if ( value instanceof Date ) {
      return DateComparison.getObject( templateManager, ( Date ) value );
    } else if ( value instanceof Number ) {
      return NumberComparison.getObject( templateManager, ( Number ) value );
    } else {
      return StringComparison.getObject( templateManager, value.toString() );
    }
  }

  private static String listToString( final TemplateManager templateManager,
      Collection values ) {
    final StringBuffer sb = new StringBuffer();
    final String start = templateManager.getTemplate( "list.format.start" );
    final String end = templateManager.getTemplate( "list.format.end" );
    final String separator = templateManager
        .getTemplate( "list.format.separator" );
    sb.append( start );
    for ( Object value : values ) {
      if ( sb.length() > start.length() )
        sb.append( separator );
      sb.append( objectToString( templateManager, value ) );
    }
    sb.append( end );
    return sb.toString();
  }

  private String toQuery( TemplateManager templateManager, String fieldName,
      Collection value, boolean vary ) {
    if ( !vary || value.size() < 2 ) {
      final MessageFormat result = new MessageFormat( templateManager
          .getTemplate( key ) );
      return result.format( new Object[] {
          listToString( templateManager, value ), fieldName } );
    } else {
      String result = "";
      // sort by string because of caching!
      final Set<String> parts = new TreeSet<String>();
      for ( Object o : value ) {
        final Collection oCol = new HashSet();
        oCol.add( o );
        parts.add( toQuery( templateManager, fieldName, oCol, false ) );
      }
      for ( String part : parts ) {
        result = result.length() > 0 ? LogicalCombination.OR.toQuery(
            templateManager, result, part ) : part;
      }
      return result;
    }
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
      Collection value ) {
    return toQuery( templateManager, fieldName, value, this.vary );
  }

}
