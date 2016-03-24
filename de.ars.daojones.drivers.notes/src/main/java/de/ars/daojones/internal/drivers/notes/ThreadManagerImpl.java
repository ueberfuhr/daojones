package de.ars.daojones.internal.drivers.notes;

import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;

import lotus.domino.Base;
import lotus.domino.NotesThread;
import de.ars.daojones.drivers.notes.ThreadManager;
import de.ars.daojones.internal.drivers.notes.utilities.Messages;
import de.ars.daojones.runtime.connections.DataAccessException;

/**
 * A manager class holding all references to Notes objects. This is used to
 * destroy all thread-local objects.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ThreadManagerImpl implements ThreadManager {

  private static final Messages bundle = Messages.create( "ThreadManagerImpl" );

  private static ThreadManagerImpl theInstance;
  private final ThreadLocal<Boolean> initialized = new ThreadLocal<Boolean>() {
    @Override
    protected Boolean initialValue() {
      return false;
    }
  };
  private final ThreadLocal<Collection<Reference<? extends Base>>> references = new ThreadLocal<Collection<Reference<? extends Base>>>();

  private ThreadManagerImpl() {
    super();
  }

  /**
   * Returns the instance.
   * 
   * @return the instance
   */
  public static synchronized ThreadManagerImpl getInstance() {
    if ( null == ThreadManagerImpl.theInstance ) {
      ThreadManagerImpl.theInstance = new ThreadManagerImpl();
    }
    return ThreadManagerImpl.theInstance;
  }

  @Override
  public void initThread() {
    if ( !initialized.get() ) {
      ThreadManagerImpl.bundle.log( Level.FINER, "thread.init", Thread.currentThread().getId(), Thread.currentThread()
              .getName() );
      try {
        NotesThread.sinitThread();
        initialized.set( true );
      } catch ( final UnsatisfiedLinkError e ) {
        ThreadManagerImpl.bundle.log( Level.WARNING, e, "thread.error.linkage" );
        // Do not catch errors
        throw e;
      } catch ( final Exception t ) {
        ThreadManagerImpl.bundle.log( Level.WARNING, t, "thread.init.error", Thread.currentThread().getId(), Thread
                .currentThread().getName() );
      }
    }
  }

  @Override
  public void termThread() {
    ThreadManagerImpl.bundle.log( Level.FINER, "thread.terminate", Thread.currentThread().getId(), Thread
            .currentThread().getName() );
    final Collection<Reference<? extends Base>> referencesCol = references.get();
    if ( null != referencesCol ) {
      for ( final Reference<? extends Base> reference : new HashSet<Reference<? extends Base>>( referencesCol ) ) {
        try {
          if ( null != reference ) {
            reference.get().recycle();
          }
        } catch ( final Exception t ) {
          ThreadManagerImpl.bundle.log( Level.FINER, t, "reference.recycle.error" );
        }
      }
      references.remove();
    }
    if ( initialized.get() ) {
      try {
        NotesThread.stermThread();
        initialized.set( false );
      } catch ( final UnsatisfiedLinkError e ) {
        ThreadManagerImpl.bundle.log( Level.WARNING, e, "thread.error.linkage" );
        // Do not catch errors
        throw e;
      } catch ( final Exception t ) {
        ThreadManagerImpl.bundle.log( Level.WARNING, t, "thread.terminate.error", Thread.currentThread().getId(),
                Thread.currentThread().getName() );
      }
    }
  }

  /**
   * Registers a reference to a Notes object.
   * 
   * @param reference
   *          the reference
   */
  public void register( final Reference<? extends Base> reference ) {
    Collection<Reference<? extends Base>> referenceCol = references.get();
    if ( null == referenceCol ) {
      referenceCol = new HashSet<Reference<? extends Base>>();
      references.set( referenceCol );
    }
    ThreadManagerImpl.bundle.log( Level.FINER, "reference.register", Thread.currentThread().getId(), Thread
            .currentThread().getName(), reference.toString() );
    referenceCol.add( reference );
  }

  /**
   * Removes a reference from the registry.
   * 
   * @param reference
   *          the reference
   */
  void unregister( final Reference<? extends Base> reference ) {
    ThreadManagerImpl.bundle.log( Level.FINER, "reference.unregister", Thread.currentThread().getId(), Thread
            .currentThread().getName(), reference.toString() );
    final Collection<Reference<? extends Base>> referenceCol = references.get();
    if ( null != referenceCol ) {
      referenceCol.remove( reference );
    }
  }

  @Override
  public void recycle( final Object o ) throws DataAccessException {
    //    final DataObject dataObject = dao.getDataObject();
    //    if ( dataObject instanceof NotesDataObject ) {
    //      try {
    //        ( ( NotesDataObject ) dataObject ).recycle();
    //      } catch ( final NotesException e ) {
    //        throw new DataAccessException( e );
    //      }
    //    }
  }

  /**
   * @see java.lang.Object#finalize()
   */
  @Override
  protected void finalize() throws Throwable {
    termThread();
    super.finalize();
  }

}
