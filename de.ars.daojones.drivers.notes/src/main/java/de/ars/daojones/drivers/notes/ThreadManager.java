package de.ars.daojones.drivers.notes;

import de.ars.daojones.internal.drivers.notes.NotesSessionManager;
import de.ars.daojones.runtime.connections.DataAccessException;

/**
 * A manager class holding all references to Notes objects. This is used to
 * destroy all thread-local objects.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @since 2.0
 */
public interface ThreadManager {

  /**
   * Initializes the current thread for the usage of Notes objects. This is
   * automatically called by {@link NotesSessionManager}.
   */
  void initThread();

  /**
   * Destroys all Notes objects that were created at the current thread. You
   * should call this method to avoid memory leaks.
   */
  void termThread();

  /**
   * Releases the memory that Notes objects block.
   * 
   * @param dao
   *          the {@link Dao}
   * @throws DataAccessException
   */
  // TODO remove this object
  // TODO write web-fragment or web container initializer
  void recycle( final Object dao ) throws DataAccessException;

}