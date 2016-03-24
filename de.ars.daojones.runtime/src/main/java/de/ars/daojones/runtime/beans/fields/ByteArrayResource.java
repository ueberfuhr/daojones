package de.ars.daojones.runtime.beans.fields;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * A resource that contains a byte array. <br/>
 * <br/>
 * <b>Note:</b> Use this class only for small byte arrays.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class ByteArrayResource implements Resource, Serializable {

  private static final long serialVersionUID = 1L;
  private static final byte[] EMPTY_CONTENT = new byte[0];

  private byte[] content;
  private final String name;
  private final String contentType;

  /**
   * Constructor.
   * 
   * @param content
   *          the content
   * @param name
   *          the name
   * @param contentType
   *          the content type
   */
  public ByteArrayResource( final byte[] content, final String name, final String contentType ) {
    super();
    this.name = name;
    this.content = content;
    this.contentType = contentType;
  }

  @Override
  public long getLength() {
    return ( null != content ? content : ByteArrayResource.EMPTY_CONTENT ).length;
  }

  @Override
  public InputStream getInputStream() {
    return new ByteArrayInputStream( null != content ? content : ByteArrayResource.EMPTY_CONTENT );
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public void destroy() {
    content = ByteArrayResource.EMPTY_CONTENT;
  }

  @Override
  public String getName() throws IOException {
    return name;
  }

}
