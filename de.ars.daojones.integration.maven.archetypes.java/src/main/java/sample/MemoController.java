package sample;

import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.query.SearchCriterionBuilder;

public class MemoController {

  private final ConnectionProvider connectionProvider;

  public MemoController( final ConnectionProvider connectionProvider ) {
    super();
    this.connectionProvider = connectionProvider;
  };

  public SearchResult<Memo> getImportantMemos() throws DataAccessException {
    final Connection<Memo> con = connectionProvider.getConnection( Memo.class );
    try {
      // find all memos whose subject starts with "IMPORTANT"
      return con.findAll( Query.create().only(
              SearchCriterionBuilder.field( Memo.SUBJECT ).asString().startsWith( "IMPORTANT" ) ) );
    } finally {
      con.close();
    }
  }

}
