package de.ars.daojones.internal.runtime.test.notes;

import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.spi.database.ConnectionContext;
import de.ars.daojones.runtime.test.data.Entry;
import de.ars.daojones.runtime.test.data.Model;
import de.ars.daojones.runtime.test.spi.database.TestConnection;
import de.ars.daojones.runtime.test.spi.database.TestDatabaseEntry;

/**
 * The Notes-driver-specific test connection.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 * @param <T>
 *          the bean type
 */
public class NotesTestConnection<T> extends TestConnection<T> {

  public NotesTestConnection( final ConnectionContext<T> connectionContext, final Model content ) {
    super( connectionContext, content );
  }

  @Override
  public TestDatabaseEntry createDatabaseEntry( final Entry entry, final BeanModel model ) {
    return new NotesDatabaseEntry( entry, model, this );
  }

}
