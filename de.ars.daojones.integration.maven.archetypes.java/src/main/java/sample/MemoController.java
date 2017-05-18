package sample;

import static de.ars.daojones.runtime.query.SearchCriterionBuilder.field;

import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.query.Query;

public class MemoController {

  private final ConnectionProvider connectionProvider;

  public MemoController( final ConnectionProvider connectionProvider ) {
    super();
    this.connectionProvider = connectionProvider;
  };

  public SearchResult<Memo> getImportantMemos() throws DataAccessException {
    try ( final Connection<Memo> con = connectionProvider.getConnection( Memo.class ) ) {
      // find all memos whose subject starts with "IMPORTANT"
      return con.findAll( Query.create().only( field( Memo.SUBJECT ).asString().startsWith( "IMPORTANT" ) ) );
    }
  }

}
