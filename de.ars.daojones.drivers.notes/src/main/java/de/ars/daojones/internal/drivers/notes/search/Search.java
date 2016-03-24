package de.ars.daojones.internal.drivers.notes.search;

import lotus.domino.NotesException;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping.DataSourceType;
import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.DatabaseAccessor;

/**
 * A common super type for a kind of search that is run against the database.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public abstract class Search {

  private final SearchContext context;

  public Search( final SearchContext context ) {
    super();
    this.context = context;
  }

  protected SearchContext getContext() {
    return context;
  }

  protected abstract DataSourceType getType();

  /**
   * Executes a single search.
   * 
   * @return the search result
   * @throws NotesException
   *           if the database search fails because of a notes exception
   * @throws DataAccessException
   *           if a custom exception occurs
   */
  protected abstract SearchResult<DatabaseAccessor> execute() throws NotesException, DataAccessException;

}
