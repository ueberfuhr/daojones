package de.ars.daojones.runtime.beans.fields;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * A resource that provides a string.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class StringResource extends ByteArrayResource {

  private static final long serialVersionUID = 1L;

  /**
   * The content type that is used when there isn't any content type specified.
   * The default content type is <tt>{@value}</tt>.
   */
  public static final String DEFAULT_CONTENT_TYPE = "text/plain";

  /**
   * Constructor. The string is encoded into a sequence of bytes using the
   * platform's default charset
   *
   * @param content
   *          the content
   * @param name
   *          the name
   * @param contentType
   *          the content type
   * @param charsetName
   *          the name of a supported charset
   * @throws UnsupportedEncodingException
   *           if the named charset is not supported
   */
  public StringResource( final String content, final String name, final String contentType, final String charsetName )
          throws UnsupportedEncodingException {
    super( null != content ? content.getBytes( charsetName ) : null, name, contentType );
  }

  /**
   * Constructor.
   *
   * @param content
   *          the content
   * @param name
   *          the name
   * @param contentType
   *          the content type
   * @param charset
   *          The {@link Charset} to be used to encode the String
   */
  public StringResource( final String content, final String name, final String contentType, final Charset charset ) {
    super( null != content ? content.getBytes( charset ) : null, name, contentType );
  }

  /**
   * Constructor. The string is encoded into a sequence of bytes using the
   * platform's default charset.
   *
   * @param content
   *          the content
   * @param name
   *          the name
   * @param contentType
   *          the content type
   */
  public StringResource( final String content, final String name, final String contentType ) {
    this( content, name, contentType, Charset.defaultCharset() );
  }

  /**
   * Constructor for a string resource with the default content type. The string
   * is encoded into a sequence of bytes using the platform's default charset.
   *
   * @param content
   *          the content
   * @param name
   *          the name
   * @see #DEFAULT_CONTENT_TYPE
   */
  public StringResource( final String content, final String name ) {
    this( content, name, StringResource.DEFAULT_CONTENT_TYPE );
  }

  /**
   * Constructor for a string resource with the default content type. The string
   * is encoded into a sequence of bytes using the platform's default charset.
   *
   * @param content
   *          the content
   * @param name
   *          the name
   * @param charset
   *          The {@link Charset} to be used to encode the String
   * @see #DEFAULT_CONTENT_TYPE
   */
  public StringResource( final String content, final String name, final Charset charset ) {
    this( content, name, StringResource.DEFAULT_CONTENT_TYPE, charset );
  }

}
