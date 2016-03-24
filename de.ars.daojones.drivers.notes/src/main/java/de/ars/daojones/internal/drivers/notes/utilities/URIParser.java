package de.ars.daojones.internal.drivers.notes.utilities;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * A parser that parses URIs.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public abstract class URIParser {

  private URIParser() {
    super();
  }

  /**
   * Parses the parameters of an {@link URI} and returns them as a
   * {@link Properties} object.
   * 
   * @param uri
   *          the {@link URI}
   * @param encoding
   *          the encoding
   * @return the {@link Properties} object
   */
  public static Properties parseParameters( final URI uri, final String encoding ) {
    final Properties result = new Properties();
    final String query = uri.getQuery();
    if ( null != query && query.length() > 0 ) {
      final List<String> parameters = new LinkedList<String>();
      int idxOfAmpersand;
      int fromIndex = 0;
      while ( fromIndex < query.length() && ( idxOfAmpersand = query.indexOf( "&", fromIndex ) ) >= 0 ) {
        parameters.add( query.substring( fromIndex, idxOfAmpersand ) );
        fromIndex = idxOfAmpersand + 1;
      }
      parameters.add( query.substring( fromIndex ) );
      while ( parameters.remove( "" ) ) {
        ;
      }
      for ( final String param : parameters ) {
        final int idxOfSeparator = param.indexOf( "=" );
        final String name = idxOfSeparator >= 0 ? param.substring( 0, idxOfSeparator ) : param;
        final String value = idxOfSeparator >= 0 ? param.substring( idxOfSeparator + 1 ) : "";
        try {
          result.setProperty( URLDecoder.decode( name, encoding ), URLDecoder.decode( value, encoding ) );
        } catch ( final UnsupportedEncodingException e ) {
          result.setProperty( name, value );
        }
      }
    }
    return result;
  }

}
