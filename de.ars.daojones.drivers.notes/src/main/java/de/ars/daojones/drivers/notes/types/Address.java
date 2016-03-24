package de.ars.daojones.drivers.notes.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Instances of this class represent an RFC (2)822 internet address. An internet
 * address consists of
 * <ul>
 * <li>An (email) address. (immutable)</li>
 * <li>A phrase that names the receiver. (optional)</li>
 * <li>Up to 3 comments. (optional)</li>
 * </ul>
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public final class Address extends Principal {

  private static final long serialVersionUID = 1L;

  private static final String ADDR = "ADDR";
  private static final String PHRASE = "PHRASE";
  private static final String COMMENT1 = "COMMENT1";
  private static final String COMMENT2 = "COMMENT2";
  private static final String COMMENT3 = "COMMENT3";

  /**
   * Creates an address.
   * 
   * @param address
   *          the address
   * @throws NullPointerException
   *           if the address is <tt>null</tt>
   */
  public Address( final String address ) {
    super();
    if ( null == address ) {
      throw new NullPointerException();
    }
    setProperty( Address.ADDR, address );
  }

  @Override
  public PrincipalType getType() {
    return PrincipalType.ADDRESS;
  }

  /**
   * Returns the address.
   * 
   * @return the address
   */
  public String getAddress() {
    return getProperty( Address.ADDR );
  }

  /**
   * Sets the name of the receiver.
   * 
   * @param phrase
   *          the name of the receiver
   */
  public void setPhrase( final String phrase ) {
    setProperty( Address.PHRASE, phrase );
  }

  /**
   * Returns the name of the receiver.
   * 
   * @return the name of the receiver
   */
  public String getPhrase() {
    return getProperty( Address.PHRASE );
  }

  /**
   * Sets the comments.
   * 
   * @param comment1
   *          the first comment
   * @param comment2
   *          the second comment
   * @param comment3
   *          the third comment
   * @throws NullPointerException
   *           <ul>
   *           <li>if comment1 is <tt>null</tt>, but comment2 or comment3 are
   *           not <tt>null</tt></li>
   *           <li>if comment2 is <tt>null</tt>, but comment3 is not
   *           <tt>null</tt></li>
   *           </ul>
   */
  public void setComments( final String comment1, final String comment2, final String comment3 ) {
    if ( null == comment1 && ( null != comment2 || null != comment3 ) || null == comment2 && null != comment3 ) {
      throw new NullPointerException();
    }
    setProperty( Address.COMMENT1, comment1 );
    setProperty( Address.COMMENT2, comment2 );
    setProperty( Address.COMMENT3, comment3 );
  }

  /**
   * Sets the comments.
   * 
   * @param comment1
   *          the first comment
   * @param comment2
   *          the second comment
   * @throws NullPointerException
   *           if comment1 is <tt>null</tt>, but comment2 is not <tt>null</tt>
   */
  public void setComments( final String comment1, final String comment2 ) {
    setComments( comment1, comment2, null );
  }

  /**
   * Sets the first comments.
   * 
   * @param comment
   *          the first comment
   */
  public void setComment( final String comment ) {
    setComments( comment, null );
  }

  /**
   * Removes all comments.
   */
  public void removeComments() {
    setComments( null, null, null );
  }

  /**
   * Returns the comments. If there isn't any comment, an empty array is
   * returned.
   * 
   * @return the comments or an empty array, if there isn't any comment
   */
  public String[] getComments() {
    final List<String> comments = new LinkedList<String>();
    final String comment1 = getProperty( Address.COMMENT1 );
    final String comment2 = getProperty( Address.COMMENT2 );
    final String comment3 = getProperty( Address.COMMENT3 );
    if ( null != comment1 ) {
      comments.add( comment1 );
      if ( null != comment2 ) {
        comments.add( comment2 );
        if ( null != comment3 ) {
          comments.add( comment3 );
        }
      }
    }
    return comments.toArray( new String[comments.size()] );
  }

  @Override
  public final String getValue() {
    return getAddress();
  }

  @Override
  public boolean isSimple() {
    return null == getProperty( Address.PHRASE ) && null == getProperty( Address.COMMENT1 );
  }

  /**
   * Generates the string representation of the address.
   * 
   * @see #valueOf(String)
   */
  @Override
  public final String toString() {
    if ( isSimple() ) {
      return getValue();
    } else {
      final StringBuffer sb = new StringBuffer();
      if ( null != getPhrase() ) {
        sb.append( "\"" ).append( getPhrase() ).append( "\" " );
      }
      sb.append( "<" ).append( getAddress() ).append( ">" );
      final String[] comments = getComments();
      if ( null != comments ) {
        for ( final String comment : comments ) {
          sb.append( " (" ).append( comment ).append( ")" );
        }
      }
      return sb.toString();
    }
  }

  private static String trim( final String text, final Character... characters ) {
    final StringBuffer sb = new StringBuffer( text );
    final Collection<Character> trimChars = new HashSet<Character>( Arrays.asList( characters ) );
    while ( sb.length() > 0 && ( Character.isWhitespace( sb.charAt( 0 ) ) || trimChars.contains( sb.charAt( 0 ) ) ) ) {
      sb.deleteCharAt( 0 );
    }
    while ( sb.length() > 0
            && ( Character.isWhitespace( sb.charAt( sb.length() - 1 ) ) || trimChars
                    .contains( sb.charAt( sb.length() - 1 ) ) ) ) {
      sb.deleteCharAt( sb.length() - 1 );
    }
    return sb.toString();
  }

  private static final Pattern commentsPattern = Pattern.compile( "\\([^\\(^\\)]*\\)" );

  private static String getComment( final Matcher matcher ) {
    return matcher.find() ? Address.trim( matcher.group(), '(', ')' ) : null;
  }

  /**
   * Parses the string representation of an address.
   * 
   * @param value
   *          the string representation
   * @return the address
   * @see #toString()
   */
  public static Address valueOf( final String value ) {
    if ( null == value || value.length() == 0 ) {
      return null;
    } else {
      if ( !Pattern.matches( ".*" + Pattern.quote( "<" ) + ".*" + Pattern.quote( ">" ) + "(\\s*" + Pattern.quote( "(" )
              + ".*" + Pattern.quote( ")" ) + "){0,3}\\s*", value ) ) {
        return new Address( value );
      } else {
        final String[] parts = value.split( "\\<|\\>" );
        Address result;
        if ( parts.length < 2 ) {
          result = new Address( value );
        } else {
          result = new Address( parts[1] );
          final String phrase = Address.trim( parts[0], '\"' );
          if ( phrase.length() > 0 ) {
            result.setPhrase( phrase );
          }
          if ( parts.length > 2 ) {
            final String comments = parts[2].trim();
            final Matcher matcher = Address.commentsPattern.matcher( comments );
            result.setComments( Address.getComment( matcher ), Address.getComment( matcher ),
                    Address.getComment( matcher ) );
          }
        }
        return result;
      }
    }

  }
}