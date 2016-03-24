package de.ars.daojones.internal.runtime.test.notes;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import de.ars.daojones.drivers.notes.NotesDatabasePath;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.spi.database.ConnectionContext;
import de.ars.daojones.runtime.test.TestConnectionFactory;
import de.ars.daojones.runtime.test.XmlResourceTestModelResolver;
import de.ars.daojones.runtime.test.data.Model;
import de.ars.daojones.runtime.test.spi.database.TestConnection;

/**
 * A custom test connection factory.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class NotesTestConnectionFactory extends TestConnectionFactory {

  public NotesTestConnectionFactory() {
    super();
    setResolver( new XmlResourceTestModelResolver() {

      @Override
      protected String getContentFile( final ConnectionModel model ) throws IOException {
        try {
          final String database = super.getContentFile( model );
          final NotesDatabasePath path = NotesDatabasePath.valueOf( database );
          final StringBuffer result = new StringBuffer();
          final String server = path.getServer();
          if ( null == server ) {
            if ( path.isUseLocalClient() ) {
              result.append( "local" );
            } else {
              result.append( path.getAuthority() );
            }
          } else {
            result.append( server );
          }
          result.append( '/' );
          result.append( path.getDatabase() );
          result.append( ".xml" );
          return result.toString();
        } catch ( final URISyntaxException e ) {
          throw new IOException( e );
        }
      }

      @Override
      protected URL resolveResource( final String file ) throws IOException {
        return getClass().getClassLoader().getResource( file );
      }
    } );
  }

  @Override
  protected <T> TestConnection<T> createTestConnection( final ConnectionContext<T> context, final Model model ) {
    return new NotesTestConnection<T>( context, model );
  }
}
