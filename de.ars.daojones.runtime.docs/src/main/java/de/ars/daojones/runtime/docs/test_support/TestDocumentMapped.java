package de.ars.daojones.runtime.docs.test_support;

import de.ars.daojones.drivers.notes.NotesDriverConfiguration;
import de.ars.daojones.runtime.test.data.Entry;
import de.ars.daojones.runtime.test.data.TestModelBuilder;
import de.ars.daojones.runtime.test.junit.ConfigDriver;
import de.ars.daojones.runtime.test.junit.DaoJones;
import de.ars.daojones.runtime.test.notes.NotesTestModel;

import org.junit.runner.RunWith;

@SuppressWarnings("unused")
@RunWith(DaoJones.class)
@ConfigDriver(NotesDriverConfiguration.DRIVER_ID)
public class TestDocumentMapped {

  // ...

  // View Entry with reference to document entry
  private final Entry viewEntry1 = TestModelBuilder.newEntry() //
          .withId("viewentry1") //
          .withProperty("?1", "Entry 1") //
          .withProperty(NotesTestModel.DOCUMENT_MAPPING_PROPERTY, "entry1") //
          .build();

}
