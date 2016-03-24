package de.ars.daojones.runtime.spi.database;

import java.util.Iterator;
import java.util.NoSuchElementException;

import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.DataAccessException;

public class CompositeSearchResult<T> extends AbstractSearchResult<T> {

  private final SearchResult<T>[] results;
  private final int maxCountOfResults;
  private final int size;

  public CompositeSearchResult( final int maxCountOfResults, final SearchResult<T>... results ) {
    super();
    this.maxCountOfResults = maxCountOfResults;
    this.results = results;
    int sizeOfResults = 0;
    for ( final SearchResult<T> result : results ) {
      sizeOfResults += result.size();
    }
    this.size = Math.min( sizeOfResults, maxCountOfResults );
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public void close() throws DataAccessException {
    for ( final SearchResult<T> result : results ) {
      result.close();
    }
  }

  @Override
  public Iterator<T> iterator() {
    return new Iterator<T>() {

      private int idx = 0;
      private int count = 0;
      private Iterator<T> currentIterator;
      {
        assignIterator();
      }

      private void assignIterator() {
        currentIterator = idx < results.length ? results[idx].iterator() : null;
      }

      private void assignNextIterator() {
        do {
          idx++;
          assignIterator();
        } while ( currentIterator != null && !currentIterator.hasNext() );
      }

      @Override
      public boolean hasNext() {
        if ( count >= maxCountOfResults ) {
          return false;
        } else {
          if ( currentIterator != null && !currentIterator.hasNext() ) {
            assignNextIterator();
          }
          return currentIterator != null && currentIterator.hasNext();
        }
      }

      @Override
      public T next() {
        if ( hasNext() ) {
          count++;
          return currentIterator.next();
        } else {
          throw new NoSuchElementException();
        }
      }

      @Override
      public void remove() {
        if ( null != currentIterator ) {
          currentIterator.remove();
        } else {
          throw new UnsupportedOperationException();
        }
      }

    };
  }

}