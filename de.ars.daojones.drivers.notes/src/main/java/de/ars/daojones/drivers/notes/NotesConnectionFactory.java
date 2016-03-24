package de.ars.daojones.drivers.notes;

import java.io.Serializable;
import java.net.URISyntaxException;

import de.ars.daojones.internal.drivers.notes.NotesConnection;
import de.ars.daojones.runtime.spi.database.Connection;
import de.ars.daojones.runtime.spi.database.ConnectionBuildException;
import de.ars.daojones.runtime.spi.database.ConnectionContext;
import de.ars.daojones.runtime.spi.database.ConnectionFactory;

/**
 * The driver implementation for the {@link ConnectionFactory} interface.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public class NotesConnectionFactory implements ConnectionFactory, Serializable {

  private static final long serialVersionUID = 1L;

  @Override
  public <T> Connection<T> createConnection( final ConnectionContext<T> context ) throws ConnectionBuildException {
    try {
      return new NotesConnection<T>( context );
    } catch ( final URISyntaxException e ) {
      throw new ConnectionBuildException( e );
    }
  }

}
