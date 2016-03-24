package de.ars.daojones.runtime.beans.fields;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * A resource that reads the content from a url.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class URLResource implements Resource {

  private final URL url;
  private final String name;
  private final String contentType;
  private final long length;

  /**
   * Constructor.
   * 
   * @param url
   *          the url
   * @param name
   *          the name
   * @param contentType
   *          the content type
   * @param length
   *          the content length
   */
  public URLResource( final URL url, final String name, final String contentType, final long length ) {
    super();
    this.url = url;
    this.name = name;
    this.contentType = contentType;
    this.length = length;
  }

  /**
   * Returns the url.
   * 
   * @return the url
   */
  public URL getUrl() {
    return url;
  }

  @Override
  public String getName() throws IOException {
    return name;
  }

  @Override
  public long getLength() {
    return length;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return url.openStream();
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public void destroy() {
    // nothing to destroy
  }

}
