package de.ars.daojones.runtime.docs.test_support;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.connections.DataAccessException;
import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.test.data.DataSource;
import de.ars.daojones.runtime.test.data.Entry;
import de.ars.daojones.runtime.test.data.TestModelBuilder;
import de.ars.daojones.runtime.test.junit.DaoJones;
import de.ars.daojones.runtime.test.junit.DaoJonesMatchers;
import de.ars.daojones.runtime.test.junit.TestModel;

@RunWith(DaoJones.class)
public class TestModelMatchersTest {

  private Entry entry1 = TestModelBuilder.newEntry().withId("id1")
          .withProperty("sender", "doe@acme.com").build();

  @TestModel
  private DataSource ds = TestModelBuilder.newDataSource("Memo").withEntries(entry1)
          .build();

  @Test
  public void testRead(final ConnectionProvider cp) throws DataAccessException {
    final Connection<Memo> con = cp.getConnection(Memo.class);
    try {
      final Memo memo = con.find();
      // is the memo bean read from entry1?
      Assert.assertThat(memo.getId(), DaoJonesMatchers.isMappedTo(entry1));
    } finally {
      con.close();
    }
  }

  @Test
  public void testDelete(final ConnectionProvider cp) throws DataAccessException {
    final Connection<Memo> con = cp.getConnection(Memo.class);
    try {
      // Delete all!
      con.delete(Query.create());
      // is the entry1 removed?
      Assert.assertThat(ds, Matchers.not(DaoJonesMatchers.hasEntry("id1")));
    } finally {
      con.close();
    }
  }

}
