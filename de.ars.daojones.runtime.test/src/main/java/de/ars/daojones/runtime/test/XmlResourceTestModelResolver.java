package de.ars.daojones.runtime.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;

import de.ars.daojones.internal.runtime.test.data.ModelFactory;
import de.ars.daojones.internal.runtime.test.utilities.Messages;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.test.data.Model;

/**
 * A test model resolver that reads the test data from an XML file url.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public abstract class XmlResourceTestModelResolver implements TestModelResolver {

  private static final Messages bundle = Messages.create( "XmlResourceTestModelResolver" );

  /**
   * Resolves the url to the given resource file.
   * 
   * @param file
   *          the resource file
   * @return the url
   * @throws IOException
   */
  protected abstract URL resolveResource( String file ) throws IOException;

  /**
   * Returns the path to the model file. This path must be in the class path.
   * You can overwrite this method to provide a custom file architecture.
   * 
   * @param model
   *          the connection model
   * @return the path to the model file
   * @throws IOException
   */
  protected String getContentFile( final ConnectionModel model ) throws IOException {
    return model.getConnection().getDatabase();
  }

  /**
   * Handles the case that the resource was not found. This, by default, throws
   * an exception. You can overwrite this method the change this behaviour.
   * 
   * @param model
   *          the connection model
   * @param file
   *          the file
   * @return the model
   * @throws IOException
   */
  protected Model handleNullResource( final ConnectionModel model, final String file ) throws IOException {
    throw new FileNotFoundException( file );
  }

  @Override
  public Model resolveModel( final ConnectionModel model ) throws IOException {
    final String file = getContentFile( model );
    final URL resource = resolveResource( file );
    if ( null == resource ) {
      return handleNullResource( model, file );
    } else {
      XmlResourceTestModelResolver.bundle.log( Level.INFO, "file.open", resource );
      final InputStream in = resource.openStream();
      try {
        final Model content = ModelFactory.readModel( in );
        return content;
      } finally {
        in.close();
      }
    }
  }

}
