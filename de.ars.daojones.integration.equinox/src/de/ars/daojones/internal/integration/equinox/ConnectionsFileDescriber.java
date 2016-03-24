package de.ars.daojones.internal.integration.equinox;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.ars.daojones.integration.equinox.Constants;

/**
 * The {@link IContentDescriber} for connection XML file.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ConnectionsFileDescriber implements IContentDescriber {

  /**
   * The id of the content type this describer is testing.
   */
  public static final String CONTENT_TYPE = "de.ars.daojones.contenttypes.connectionsfile";

  /**
   * @see org.eclipse.core.runtime.content.IContentDescriber#describe(java.io.InputStream,
   *      org.eclipse.core.runtime.content.IContentDescription)
   */
  // @Override
  @Override
  public int describe( final InputStream contents, final IContentDescription description ) throws IOException {
    // short test
    if ( null != description
            && Constants.FILE_IDENTIFIER_VALUE.equals( description.getProperty( Constants.FILE_IDENTIFIER_KEY ) ) ) {
      return IContentDescriber.VALID;
    }
    // test for content
    try {
      final SAXParserFactory factory = SAXParserFactory.newInstance();
      final SAXParser parser = factory.newSAXParser();
      final ThreadLocal<Boolean> result = new ThreadLocal<Boolean>();
      result.set( false );
      try {
        parser.parse( contents, new DefaultHandler() {
          @Override
          public void startElement( final String uri, final String localName, final String name,
                  final Attributes attributes ) throws SAXException {
            if ( "configuration".equals( name ) ) {
              for ( int i = 0; i < attributes.getLength() && !result.get(); i++ ) {
                // xmlns="http://www.ars.de/daojones/connections/"
                if ( attributes.getQName( i ).startsWith( "xmlns" )
                        && "http://www.ars.de/daojones/connections/".equals( attributes.getValue( 0 ) ) ) {
                  result.set( true );
                }
              }
            }
            // break
            throw new SAXException();
          }
        } );
        return IContentDescriber.INDETERMINATE;
      } catch ( SAXException e ) {
        return result.get() ? IContentDescriber.VALID : IContentDescriber.INVALID;
      }
    } catch ( ParserConfigurationException e ) {
      return IContentDescriber.INDETERMINATE;
    } catch ( SAXException e ) {
      return IContentDescriber.INDETERMINATE;
    }
  }

  /**
   * @see org.eclipse.core.runtime.content.IContentDescriber#getSupportedOptions()
   */
  @Override
  public QualifiedName[] getSupportedOptions() {
    return new QualifiedName[] {};
  }

}
