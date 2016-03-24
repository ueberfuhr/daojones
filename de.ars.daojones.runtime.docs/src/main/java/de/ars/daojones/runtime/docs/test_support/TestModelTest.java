package de.ars.daojones.runtime.docs.test_support;

import org.junit.runner.RunWith;

import de.ars.daojones.runtime.test.data.DataSource;
import de.ars.daojones.runtime.test.data.TestModelBuilder;
import de.ars.daojones.runtime.test.junit.DaoJones;
import de.ars.daojones.runtime.test.junit.TestModel;

@RunWith(DaoJones.class)
public class TestModelTest {

  // your test model
  @TestModel
  private DataSource ds = TestModelBuilder
  // <datasource name="Memo">
          .newDataSource("Memo").withEntries(
          // <entry id="id1">
                  TestModelBuilder.newEntry().withId("id1")
                  // <property name="sender" value="doe@acme.com"/>
                          .withProperty("sender", "doe@acme.com")
          // </entry>
          // </datasource>
          ).build();

}
