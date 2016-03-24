package de.ars.daojones.internal.drivers.notes;

import lotus.domino.Base;
import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;
import de.ars.daojones.drivers.notes.DataHandler.DataHandlerContext;
import de.ars.daojones.drivers.notes.DataHandlerProvider;

public class DataHandlerContextWrapper<T extends Base> implements DataHandlerContext<T> {
  private final DataHandlerContext<?> delegate;

  public DataHandlerContextWrapper( final DataHandlerContext<?> delegate ) {
    super();
    this.delegate = delegate;
  }

  protected DataHandlerContext<?> getDelegate() {
    return delegate;
  }

  @Override
  public String getApplication() {
    return delegate.getApplication();
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public T getSource() throws NotesException {
    return ( T ) delegate.getSource();
  }

  @Override
  public Session getSession() throws NotesException {
    return delegate.getSession();
  }

  @Override
  public Database getDatabase() throws NotesException {
    return delegate.getDatabase();
  }

  @Override
  public DataHandlerProvider getDataHandlerProvider() {
    return delegate.getDataHandlerProvider();
  }

}
