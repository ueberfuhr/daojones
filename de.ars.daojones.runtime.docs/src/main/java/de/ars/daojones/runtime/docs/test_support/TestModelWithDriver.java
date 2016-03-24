package de.ars.daojones.runtime.docs.test_support;

import de.ars.daojones.drivers.notes.NotesDriverConfiguration;
import de.ars.daojones.runtime.test.junit.ConfigDriver;
import de.ars.daojones.runtime.test.junit.DaoJones;

import org.junit.runner.RunWith;

@RunWith(DaoJones.class)
@ConfigDriver(NotesDriverConfiguration.DRIVER_ID)
public class TestModelWithDriver {

  // ...

}
