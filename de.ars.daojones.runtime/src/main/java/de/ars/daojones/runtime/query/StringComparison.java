package de.ars.daojones.runtime.query;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.ars.daojones.internal.runtime.utilities.Messages;

/**
 * A kind of {@link Comparison} comparing strings.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public enum StringComparison implements Comparison<String> {

  /**
   * The case-sensitive equals operator.
   */
  EQUALS( true ) {

    @Override
    public boolean matches( final String left, final String right ) {
      return left == null && right == null || null != left && left.equals( right );
    }

  },
  /**
   * The case-insensitive equals operator.
   */
  EQUALS_IGNORECASE( false ) {

    @Override
    public boolean matches( final String left, final String right ) {
      return left == null && right == null || null != left && left.equalsIgnoreCase( right );
    }

  },
  /**
   * The case-sensitive startsWith operator.
   */
  STARTSWITH( true ) {

    @Override
    public boolean matches( final String left, final String right ) {
      return null != left && left.startsWith( right );
    }

  },
  /**
   * The case-insensitive startsWith operator.
   */
  STARTSWITH_IGNORECASE( false ) {

    @Override
    public boolean matches( final String left, final String right ) {
      return null != left && left.toLowerCase().startsWith( right.toLowerCase() );
    }

  },
  /**
   * The case-sensitive endsWith operator.
   */
  ENDSWITH( true ) {

    @Override
    public boolean matches( final String left, final String right ) {
      return null != left && left.endsWith( right );
    }

  },
  /**
   * The case-insensitive endsWith operator.
   */
  ENDSWITH_IGNORECASE( false ) {

    @Override
    public boolean matches( final String left, final String right ) {
      return null != left && left.toLowerCase().endsWith( right.toLowerCase() );
    }

  },
  /**
   * The case-sensitive contains operator.
   */
  CONTAINS( true ) {

    @Override
    public boolean matches( final String left, final String right ) {
      return null != left && left.contains( right );
    }

  },
  /**
   * The case-insensitive contains operator.
   */
  CONTAINS_IGNORECASE( false ) {

    @Override
    public boolean matches( final String left, final String right ) {
      return null != left && left.toLowerCase().contains( right.toLowerCase() );
    }

  },
  /**
   * The case-sensitive like operator. The following placeholders are possible:
   * <ul>
   * <li><code>"_"</code>: Matches a single character.
   * <li><code>"%"</code>: Matches multiple (also 0) characters.
   * </ul>
   * Use <code>"/"</code> as escape character.<br/>
   * <br/>
   * <b>Examples:</b><br/>
   * <ul>
   * <li><code>"My f_rst example."</code> matches "My first example."
   * <li><code>"My % example."</code> matches "My first example."
   * <li><code>"The /_ is a character."</code> matches "The _ is a character."
   * <li><code>"The // is a character."</code> matches "The / is a character."
   * </ul>
   */
  LIKE( true ) {

    @Override
    public boolean matches( final String left, final String right ) {
      return null != left && StringComparison.patternMatches( left, right );
    }

  },
  /**
   * The case-insensitive like operator.
   * 
   * @see #LIKE placeholders and examples
   */
  LIKE_IGNORECASE( false ) {

    @Override
    public boolean matches( final String left, final String right ) {
      return null != left && StringComparison.patternMatches( left.toLowerCase(), right.toLowerCase() );
    }

  };

  private static final Messages logger = Messages.create( "query.comparison.String" );

  private final boolean caseSensitive;

  private StringComparison( final boolean caseSensitive ) {
    this.caseSensitive = caseSensitive;
  }

  /**
   * Returns a flag indicating whether the comparison is case sensitive or not.
   * 
   * @return <code>true</code>, if the comparison is case sensitive
   */
  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  private static boolean patternMatches( final String left, final String right ) throws PatternSyntaxException {
    if ( null == right || null == left ) {
      return false;
    }
    // pattern is right
    final StringBuilder regex = new StringBuilder();
    boolean isEscape = false;
    for ( final char c : right.toCharArray() ) {
      if ( isEscape ) {
        regex.append( c );
        isEscape = false;
      } else {
        switch ( c ) {
        case '/':
          isEscape = true;
          break;
        case '_':
          regex.append( "\\.{1}" );
          break;
        case '%':
          regex.append( "\\.*" );
          break;
        default:
          regex.append( c );
          break;
        }
      }
    }
    if ( isEscape ) {
      throw new PatternSyntaxException( StringComparison.logger.get( "error.pattern.endsWithEscape", right, "//" ),
              right, right.length() - 1 );
    }
    return Pattern.matches( regex.toString(), left );
  }

  @Override
  public Class<String> getType() {
    return String.class;
  }

}
