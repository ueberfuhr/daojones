package de.ars.daojones.runtime.beans.fields;

import java.io.File;
import java.io.IOException;

/**
 * A resource that reads the content from a file.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class FileResource extends URLResource {

  private final File file;

  /**
   * Constructor.
   * 
   * @param file
   *          the file
   * @param contentType
   *          the content type
   * @throws IOException
   */
  public FileResource( final File file, final String contentType ) throws IOException {
    this( file, file.getName(), contentType );
  }

  /**
   * Constructor.
   * 
   * @param file
   *          the file
   * @param name
   *          the name
   * @param contentType
   *          the content type
   * @throws IOException
   */
  public FileResource( final File file, final String name, final String contentType ) throws IOException {
    super( file.toURI().toURL(), name, contentType, file.length() );
    this.file = file;
  }

  /**
   * Returns the file.
   * 
   * @return the file
   */
  public File getFile() {
    return file;
  }

}
