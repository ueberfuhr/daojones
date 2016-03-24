package de.ars.daojones.connections.model;

import java.io.IOException;
import java.io.InputStream;

import de.ars.daojones.connections.model.io.XmlInputStreamConnectionConfigurationSource;

/**
 * An {@link IConnectionConfigurationBundle} implementation based on the current
 * machine where the VM is running.
 * 
 * If you specify <code>folder/bundle</code> as the bundlename, this bundle
 * searches for the following files in the following order: <br/>
 * <ul>
 * <li><code>folder/bundle_&lt;host name&gt;.xml</code></li>
 * <li><code>folder/bundle_&lt;IP address&gt;.xml</code></li>
 * <li><code>folder/bundle.xml</code></li>
 * </ul>
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @since 0.9.0
 */
public abstract class XMLConfigurationBundle implements IConnectionConfigurationBundle {

//  private final InputStream in;
//
//  /**
//   * Creates an instance
//   * 
//   * @param in
//   *          the {@link InputStream}
//   */
//  public XMLConfigurationBundle( InputStream in ) {
//    super();
//    if ( null == in )
//      throw new NullPointerException( "InputStream is null!" );
//    this.in = in;
//  }

  /**
   * Returns the {@link InputStream} to read the XML.
   * 
   * @return the {@link InputStream}
   * @throws IOException
   */
  protected abstract InputStream getInputStream() throws IOException;

  /**
   * @see de.ars.daojones.connections.model.IConnectionConfigurationBundle#getConfiguration()
   */
  public IConnectionConfiguration getConfiguration() throws IOException {
    final XmlInputStreamConnectionConfigurationSource source = new XmlInputStreamConnectionConfigurationSource() {
      private static final long serialVersionUID = 1L;

      @Override
      protected InputStream getInputStream( String resource )
          throws IOException {
        return XMLConfigurationBundle.this.getInputStream();
      }
    };
    return source.read( "" );
  }

}
