package de.ars.daojones.runtime.beans.fields;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import de.ars.daojones.internal.runtime.utilities.Messages;

/**
 * A resource that buffers the content of a resource within an internal byte
 * array. This type of resource is serializable.<br/>
 * <br/>
 * <b>Please note:</b> Use this type carefully, because buffering the content of
 * a resource increases memory usage and slows down performance.<br/>
 * <b>Please note:</b> The maximum size of a buffered resource is
 * {@value Integer#MAX_VALUE}.<br/>
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class BufferedResource implements Resource, Serializable {

  private static final long serialVersionUID = 1L;
  private static final Messages bundle = Messages.create( "beans.fields.BufferedResource" );

  private final ByteArrayResource delegate;

  private static byte[] readContents( final Resource resource ) throws IOException {
    final long length = resource.getLength();
    if ( Integer.MAX_VALUE < length ) {
      throw new IOException( BufferedResource.bundle.get( "error.toolarge", length, Integer.MAX_VALUE ) );
    }
    final InputStream in = resource.getInputStream();
    try {
      final ByteArrayOutputStream bos = new ByteArrayOutputStream( ( int ) length );
      try {
        int len;
        final byte[] buf = new byte[4096];
        while ( ( len = in.read( buf ) ) > 0 ) {
          bos.write( buf, 0, len );
        }
        return bos.toByteArray();
      } finally {
        bos.close();
      }
    } finally {
      in.close();
    }
  }

  public BufferedResource( final Resource resource ) throws IOException {
    super();
    delegate = new ByteArrayResource( BufferedResource.readContents( resource ), resource.getName(),
            resource.getContentType() );
  }

  @Override
  public String getName() throws IOException {
    return delegate.getName();
  }

  @Override
  public long getLength() throws IOException {
    return delegate.getLength();
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return delegate.getInputStream();
  }

  @Override
  public String getContentType() throws IOException {
    return delegate.getContentType();
  }

  @Override
  public void destroy() throws IOException {
    delegate.destroy();
  }

}
