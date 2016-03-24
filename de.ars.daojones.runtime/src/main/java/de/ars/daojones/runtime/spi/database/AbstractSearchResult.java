package de.ars.daojones.runtime.spi.database;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.DataAccessException;

/**
 * A common super class for search result implementations.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @param <T>
 *          the result type
 */
public abstract class AbstractSearchResult<T> implements SearchResult<T> {

  /**
   * An empty search result.
   */
  @SuppressWarnings( "rawtypes" )
  public static final SearchResult EMPTY_SEARCH_RESULT = new SearchResult() {

    @Override
    public int size() {
      return 0;
    }

    @Override
    public List getAsList() throws DataAccessException {
      return Collections.EMPTY_LIST;
    }

    @Override
    public List getAsList( final int beginIndex, final int endIndex ) throws DataAccessException,
            IndexOutOfBoundsException {
      if ( beginIndex != 0 || endIndex != 0 ) {
        throw new IndexOutOfBoundsException();
      } else {
        return Collections.EMPTY_LIST;
      }
    }

    @Override
    public Iterator iterator() {
      return Collections.EMPTY_LIST.iterator();
    }

    @Override
    public void close() throws DataAccessException {
    }
  };

  @Override
  public List<T> getAsList() throws DataAccessException {
    return getAsList( 0, this.size() );
  }

  @Override
  public List<T> getAsList( final int beginIndex, final int endIndex ) throws DataAccessException,
          IndexOutOfBoundsException {
    final int counter = 0;
    final List<T> result = new LinkedList<T>();
    for ( final T t : this ) {
      if ( counter >= beginIndex && counter < endIndex ) {
        result.add( t );
      } else if ( counter >= endIndex ) {
        break;
      }
    }
    return result;
  }

}