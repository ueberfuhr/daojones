package de.ars.daojones.internal.runtime.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Unmarshaller.Listener;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

/**
 * A reader for XML files that uses JAXB.
 * 
 * @author ueberfuhr, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 * @param <T>
 *          the element type that is read
 */
public class JAXBReader<T> {

  public interface Callback {

    void beforeUnmarshal( Object target, Object parent );

    void afterUnmarshal( Object target, Object parent );

  }

  private final Class<? extends T> root;
  private final URL schema;

  public JAXBReader( final Class<? extends T> root, final URL schema ) {
    super();
    this.root = root;
    this.schema = schema;
  }

  public T read( final InputStream in ) throws IOException {
    return read( in, null );
  }

  @SuppressWarnings( "unchecked" )
  public T read( final InputStream in, final Callback callback ) throws IOException {
    try {
      // get context and unmarshaller
      final JAXBContext context = JAXBContext.newInstance( root.getPackage().getName(), root.getClassLoader() );
      final Unmarshaller unmarshaller = context.createUnmarshaller();
      // get schema
      final SchemaFactory factory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
      if ( null != schema ) {
        final InputStream schemaStream = schema.openStream();
        try {
          final Schema schemaObject = factory.newSchema( new StreamSource( schemaStream ) );
          unmarshaller.setSchema( schemaObject );
        } finally {
          schemaStream.close();
        }
      }
      if ( null != callback ) {
        unmarshaller.setListener( new Listener() {

          @Override
          public void afterUnmarshal( final Object target, final Object parent ) {
            callback.afterUnmarshal( target, parent );
          }

          @Override
          public void beforeUnmarshal( final Object target, final Object parent ) {
            callback.beforeUnmarshal( target, parent );
          }
        } );
      }
      // Unmarshal
      final Object o = unmarshaller.unmarshal( in );
      if ( null == o ) {
        return ( T ) o;
      }
      return JAXBElement.class.isAssignableFrom( o.getClass() ) ? ( ( JAXBElement<T> ) o ).getValue() : ( T ) o;
    } catch ( final JAXBException e ) {
      throw new IOException( e );
    } catch ( final SAXException e ) {
      throw new IOException( e );
    }

  }
}
