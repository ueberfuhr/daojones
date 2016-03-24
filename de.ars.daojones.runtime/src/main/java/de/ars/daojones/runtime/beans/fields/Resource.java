package de.ars.daojones.runtime.beans.fields;

import java.io.IOException;
import java.io.InputStream;

/**
 * A couple of bytes that represent a special information that cannot be stored
 * into text or any other prmitive form. This can be used to store
 * attachments/BLOBs/CLOBs.<br/>
 * <br/>
 * <b>Please note:</b> Beans are not serializable if they contain fields of type
 * Resource.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public interface Resource {

  /**
   * Returns the name.
   * 
   * @return the name
   * @throws IOException
   */
  String getName() throws IOException;

  /**
   * Returns the size of the object.
   * 
   * @return the size
   * @throws IOException
   */
  long getLength() throws IOException;

  /**
   * Returns the {@link InputStream} to read the bytes. Multiple invocations of
   * this method will result in multiple, independent streams. <br/>
   * <br/>
   * <b>Note: </b>Close the stream, if you do not need it anymore.
   * 
   * @return the {@link InputStream}
   * @throws IOException
   */
  InputStream getInputStream() throws IOException;

  /**
   * Returns the content-type of the embedded object or null, if the
   * content-type is unknown.
   * 
   * @return the content-type
   * @throws IOException
   */
  String getContentType() throws IOException;

  /**
   * Call this method when you do not need the {@link Resource} anymore. This
   * releases resources.
   * 
   * @throws IOException
   */
  void destroy() throws IOException;

}
