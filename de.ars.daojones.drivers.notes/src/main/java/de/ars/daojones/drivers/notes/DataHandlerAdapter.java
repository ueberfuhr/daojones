package de.ars.daojones.drivers.notes;

import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.ViewEntry;
import de.ars.daojones.runtime.spi.beans.fields.FieldContext;

/**
 * An adapter class. Custom data handler implementations should derive at least
 * from this class instead directly implementing the DataHandler interface to
 * avoid compatibility issues (e.g. in case of additional methods of the data
 * handler interface) in the future.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the data type
 * @see AbstractDataHandler
 */
public abstract class DataHandlerAdapter<T> implements DataHandler<T> {

  @Override
  public T readDocument( final de.ars.daojones.drivers.notes.DataHandler.DataHandlerContext<Document> context,
          final FieldContext<T> fieldContext ) throws NotesException, DataHandlerException {
    throw new UnsupportedOperationException();
  }

  @Override
  public T readView( final de.ars.daojones.drivers.notes.DataHandler.DataHandlerContext<ViewEntry> context,
          final FieldContext<T> fieldContext ) throws NotesException, DataHandlerException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeDocument( final de.ars.daojones.drivers.notes.DataHandler.DataHandlerContext<Document> context,
          final FieldContext<T> fieldContext, final T value ) throws NotesException, DataHandlerException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeView( final de.ars.daojones.drivers.notes.DataHandler.DataHandlerContext<ViewEntry> context,
          final FieldContext<T> fieldContext, final T value ) throws NotesException, DataHandlerException {
    throw new UnsupportedOperationException();
  }

}
