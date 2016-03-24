package de.ars.daojones.internal.drivers.notes.service;

import de.ars.daojones.drivers.notes.ThreadManager;
import de.ars.daojones.internal.drivers.notes.ThreadManagerImpl;
import de.ars.daojones.runtime.connections.DataAccessException;

/**
 * A bridge to register the singleton thread manager as OSGi service.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 1.2.0
 */
public class ThreadManagerBridge implements ThreadManager {

  @Override
  public void initThread() {
    ThreadManagerImpl.getInstance().initThread();
  }

  @Override
  public void termThread() {
    ThreadManagerImpl.getInstance().termThread();
  }

  @Override
  public void recycle( final Object dao ) throws DataAccessException {
    ThreadManagerImpl.getInstance().recycle( dao );
  }

}
