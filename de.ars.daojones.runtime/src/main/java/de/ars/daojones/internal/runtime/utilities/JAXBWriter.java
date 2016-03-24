package de.ars.daojones.internal.runtime.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Marshaller.Listener;
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
public class JAXBWriter<T> {

  public interface Callback {

    void beforeMarshal( Object model );

    void afterMarshal( Object model );

  }

  private final Class<? extends T> root;
  private final URL schema;

  public JAXBWriter( final Class<? extends T> root, final URL schema ) {
    super();
    this.root = root;
    this.schema = schema;
  }

  public void write( final T t, final OutputStream out ) throws IOException {
    write( t, out, null );
  }

  public void write( final T t, final OutputStream out, final Callback callback ) throws IOException {
    try {
      // get context and unmarshaller
      final JAXBContext context = JAXBContext.newInstance( root.getPackage().getName(), root.getClassLoader() );
      final Marshaller marshaller = context.createMarshaller();
      // get schema
      final SchemaFactory factory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
      if ( null != schema ) {
        final InputStream schemaStream = schema.openStream();
        try {
          final Schema schemaObject = factory.newSchema( new StreamSource( schemaStream ) );
          marshaller.setSchema( schemaObject );
        } finally {
          schemaStream.close();
        }
      }
      if ( null != callback ) {
        marshaller.setListener( new Listener() {

          @Override
          public void afterMarshal( final Object source ) {
            callback.afterMarshal( source );
          }

          @Override
          public void beforeMarshal( final Object source ) {
            callback.beforeMarshal( source );
          }
        } );
      }
      // Marshal
      marshaller.marshal( t, out );
    } catch ( final JAXBException e ) {
      throw new IOException( e );
    } catch ( final SAXException e ) {
      throw new IOException( e );
    }

  }

}
