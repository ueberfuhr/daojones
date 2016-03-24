package de.ars.daojones;

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
import org.xml.sax.SAXParseException;

abstract class JAXBUtilities {

	/**
	 * Returns the {@link JAXBContext}.
	 * @return the {@link JAXBContext}
	 * @throws JAXBException
	 */
	public static JAXBContext getContext() throws JAXBException {
		final JAXBContext result = JAXBContext.newInstance(
			Inheritations.class.getPackage().getName()
		);
		return result;
	}
	
	/**
	 * Returns the schema.
	 * @return the schema
	 * @throws SAXException
	 */
	public static Schema getSchema() throws SAXException {
		final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
       	final Schema schema = factory.newSchema(new StreamSource(JAXBUtilities.class.getResourceAsStream("inheritations.xsd")));
       	return schema;
	}
	
	@SuppressWarnings("unchecked")
	public static <T>T read(InputStream in, Class<T> c) throws InvalidFormatException, IOException {
		try {
			final JAXBContext context = getContext();
			final Unmarshaller unmarshaller = context.createUnmarshaller();
	       	final Schema schema = getSchema();
			unmarshaller.setSchema(schema);
			final Object o = unmarshaller.unmarshal(in);
			if(null == o) return (T)o;
			return JAXBElement.class.isAssignableFrom(o.getClass()) ? ((JAXBElement<T>)o).getValue() : (T)o;
		} catch(UnmarshalException e) {
			try {
				throw e.getCause();
			} catch(SAXParseException e1) {
				// TODO Java6-Migration
				throw new InvalidFormatException(e1.getMessage());
			} catch(Throwable t) {
				// TODO Java6-Migration
				throw new IOException(e.getMessage());
			}
		} catch (JAXBException e) {
			// TODO Java6-Migration
			throw new IOException(e.getMessage());
		} catch (SAXException e) {
			// TODO Java6-Migration
			throw new IOException(e.getMessage());
		}
	}

	public static void write(Object o, OutputStream out) throws IOException {
		try {
			if(null != o) {
				final JAXBContext context = getContext();
				final Marshaller marshaller = context.createMarshaller();
		       	final Schema schema = getSchema();
		       	marshaller.setSchema(schema);
				marshaller.marshal(o, out);
			}
		} catch (JAXBException e) {
			// TODO Java6-Migration
			throw new IOException(e.getMessage());
		} catch (SAXException e) {
			// TODO Java6-Migration
			throw new IOException(e.getMessage());
		}

	}
	
}
