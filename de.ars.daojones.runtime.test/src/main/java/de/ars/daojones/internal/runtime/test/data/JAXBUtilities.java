package de.ars.daojones.internal.runtime.test.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import de.ars.daojones.runtime.test.data.ObjectFactory;

abstract class JAXBUtilities {

  private static final String SCHEMA = "/META-INF/schemas/daojones-2.0-testmodel.xsd";

  /**
   * Returns the {@link JAXBContext}.
   * 
   * @return the {@link JAXBContext}
   * @throws JAXBException
   */
  public static JAXBContext getContext() throws JAXBException {
    final JAXBContext result = JAXBContext.newInstance( ObjectFactory.class.getPackage().getName() );
    return result;
  }

  /**
   * Returns the schema.
   * 
   * @return the schema
   * @throws SAXException
   */
  public static Schema getSchema() throws SAXException {
    final SchemaFactory factory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
    final Schema schema = factory.newSchema( new StreamSource( JAXBUtilities.class
            .getResourceAsStream( JAXBUtilities.SCHEMA ) ) );
    return schema;
  }

  private static IOException toIOException( final Throwable cause ) {
    final IOException result = new IOException( cause.getMessage() );
    result.initCause( cause );
    return result;
  }

  @SuppressWarnings( "unchecked" )
  public static <T> T read( final InputStream in, final Class<T> c ) throws IOException {
    try {
      final JAXBContext context = JAXBUtilities.getContext();
      final Unmarshaller unmarshaller = context.createUnmarshaller();
      final Schema schema = JAXBUtilities.getSchema();
      unmarshaller.setSchema( schema );
      final Object o = unmarshaller.unmarshal( in );
      if ( null == o ) {
        return ( T ) o;
      }
      return JAXBElement.class.isAssignableFrom( o.getClass() ) ? ( ( JAXBElement<T> ) o ).getValue() : ( T ) o;
    } catch ( final UnmarshalException e ) {
      try {
        throw e.getCause();
        //      } catch ( SAXParseException e1 ) {
        //        // TODO Java6-Migration
        //        throw new InvalidFormatException( e1.getMessage() );
      } catch ( final Throwable t ) {
        // TODO Java6-Migration
        throw JAXBUtilities.toIOException( e );
      }
    } catch ( final JAXBException e ) {
      // TODO Java6-Migration
      throw JAXBUtilities.toIOException( e );
    } catch ( final SAXException e ) {
      // TODO Java6-Migration
      throw JAXBUtilities.toIOException( e );
    }
  }

  public static void write( final Object o, final OutputStream out ) throws IOException {
    try {
      if ( null != o ) {
        final JAXBContext context = JAXBUtilities.getContext();
        final Marshaller marshaller = context.createMarshaller();
        final Schema schema = JAXBUtilities.getSchema();
        marshaller.setSchema( schema );
        marshaller.marshal( o, out );
      }
    } catch ( final JAXBException e ) {
      // TODO Java6-Migration
      throw JAXBUtilities.toIOException( e );
    } catch ( final SAXException e ) {
      // TODO Java6-Migration
      throw JAXBUtilities.toIOException( e );
    }
  }

}
