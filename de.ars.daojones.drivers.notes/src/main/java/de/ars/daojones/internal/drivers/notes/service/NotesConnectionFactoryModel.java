package de.ars.daojones.internal.drivers.notes.service;

import de.ars.daojones.drivers.notes.NotesDriverConfiguration;
import de.ars.daojones.drivers.notes.NotesConnectionFactory;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModel;
import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModelImpl;

/**
 * The {@link ConnectionFactoryModel} for the {@link NotesConnectionFactory}.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public class NotesConnectionFactoryModel extends ConnectionFactoryModelImpl {

  private static final long serialVersionUID = 1L;

  private static final Messages nls = Messages.create( "service.NotesConnectionFactoryModel" ); //$NON-NLS-1$

  /**
   * Constructor.
   */
  public NotesConnectionFactoryModel() {
    super( NotesDriverConfiguration.DRIVER_ID, NotesConnectionFactory.class );
  }

  private final String name = NotesConnectionFactoryModel.nls.get( "driver.name" ); //$NON-NLS-1$
  private final String description = NotesConnectionFactoryModel.nls.get( "driver.description" ); //$NON-NLS-1$

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

}
