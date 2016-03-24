package sample;

import java.util.Collection;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import sample.Memo;
import sample.MemoController;

import de.ars.daojones.runtime.connections.Accessor.SearchResult;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.test.data.DataSource;
import de.ars.daojones.runtime.test.data.TestModelBuilder;
import de.ars.daojones.runtime.test.junit.DaoJones;
import de.ars.daojones.runtime.test.junit.Inject;
import de.ars.daojones.runtime.test.junit.TestModel;

@RunWith( DaoJones.class )
public class MemoControllerTest {

  @Inject
  private ConnectionProvider cp;

  //your test model
  @TestModel
  private DataSource ds = TestModelBuilder.newDataSource( "Memo" )
          .withEntries( TestModelBuilder.newEntry().withId( "id1" ).withProperty( "subject", "IMPORTANT: Come here!" ) )
          .build();

  @Test
  public void testGetImportantMemos() throws DataAccessException {
    final MemoController controller = new MemoController( cp );
    final SearchResult<Memo> importantMemos = controller.getImportantMemos();
    try {
      final Collection<Memo> memos = importantMemos.getAsList();
      Assert.assertThat( memos, Matchers.not( Matchers.empty() ) );
    } finally {
      importantMemos.close();
    }
  }
}
