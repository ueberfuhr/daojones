package de.ars.daojones.internal.drivers.notes.search;

import java.util.Arrays;

import de.ars.daojones.drivers.notes.NotesQueryParameters;
import de.ars.daojones.drivers.notes.search.SearchType;
import de.ars.daojones.runtime.configuration.beans.DatabaseTypeMapping.DataSourceType;
import de.ars.daojones.runtime.query.Query;

public final class SearchTypeHelper {

  private SearchTypeHelper() {
    super();
  }

  public static SearchType getSearchType( final Query query, final DataSourceType dataSourceType ) {
    final Query.Parameter[] queryParameters = query.getParameters();
    final boolean ft = DataSourceType.VIEW == dataSourceType
            || Arrays.asList( queryParameters ).contains( NotesQueryParameters.FT_SEARCH );
    return ft ? SearchType.FT_SEARCH : SearchType.FORMULA;
  }

}
