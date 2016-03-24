package de.ars.daojones.internal.runtime.test.notes;

import de.ars.daojones.drivers.notes.NotesDriverConfiguration;
import de.ars.daojones.runtime.test.TestConnectionFactoryModel;
import de.ars.daojones.runtime.test.spi.database.AbstractTestConnectionFactoryModel;

public class NotesTestConnectionFactoryModel extends AbstractTestConnectionFactoryModel implements
        TestConnectionFactoryModel {

  private static final long serialVersionUID = 1L;

  private static final Messages bundle = Messages.create( "NotesTestConnectionFactoryModel" ); //$NON-NLS-1$

  public NotesTestConnectionFactoryModel() {
    super( NotesDriverConfiguration.DRIVER_ID + ".test", NotesTestConnectionFactory.class ); //$NON-NLS-1$
  }

  @Override
  public String getOriginalId() {
    return NotesDriverConfiguration.DRIVER_ID;
  }

  private final String name = NotesTestConnectionFactoryModel.bundle.get( "driver.name" ); //$NON-NLS-1$
  private final String description = NotesTestConnectionFactoryModel.bundle.get( "driver.description" ); //$NON-NLS-1$

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

}
