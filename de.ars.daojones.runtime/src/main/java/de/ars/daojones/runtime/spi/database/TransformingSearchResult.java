package de.ars.daojones.runtime.spi.database;

import java.util.Iterator;

import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.DataAccessException;

public abstract class TransformingSearchResult<T, TT> extends AbstractSearchResult<TT> {

  private final SearchResult<T> delegate;

  public TransformingSearchResult( final SearchResult<T> delegate ) {
    super();
    this.delegate = delegate;
  }

  @Override
  public int size() {
    return delegate.size();
  }

  protected abstract TT convert( T t );

  @Override
  public Iterator<TT> iterator() {
    final Iterator<T> delegateIt = delegate.iterator();
    return new Iterator<TT>() {

      @Override
      public boolean hasNext() {
        return delegateIt.hasNext();
      }

      @Override
      public TT next() {
        return convert( delegateIt.next() );
      }

      @Override
      public void remove() {
        delegateIt.remove();
      }

    };
  }

  @Override
  public void close() throws DataAccessException {
    delegate.close();
  }

}
