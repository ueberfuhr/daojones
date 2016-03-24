package de.ars.daojones.internal.drivers.notes.search;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping.DataSourceType;
import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.spi.beans.fields.DatabaseAccessor;

public class ViewSearch extends Search {

  private final String viewName;

  public ViewSearch( final SearchContext context, final String viewName ) {
    super( context );
    this.viewName = viewName;
  }

  public String getViewName() {
    return viewName;
  }

  @Override
  protected DataSourceType getType() {
    return DataSourceType.VIEW;
  }

  @Override
  protected SearchResult<DatabaseAccessor> execute() throws NotesException, DataAccessException {
    final Database db = getContext().getDatabase();
    final View view = db.getView( getViewName() );
    if ( null == view ) {
      throw new ViewNotFoundException( viewName );
    }
    db.isFTIndexed();
    return null;
  }

}
