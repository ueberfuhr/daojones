package de.ars.daojones.drivers.notes;

import static de.ars.daojones.drivers.notes.LoggerConstants.DEBUG;
import static de.ars.daojones.drivers.notes.LoggerConstants.WARNING;
import static de.ars.daojones.drivers.notes.LoggerConstants.getLogger;

import java.util.Collection;
import java.util.HashSet;

import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.DataObject;

import lotus.domino.Base;
import lotus.domino.NotesException;
import lotus.domino.NotesThread;

/**
 * A manager class holding all references to Notes objects. This is used to
 * destroy all thread-local objects.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ThreadManager {

	private static ThreadManager theInstance;
	private final ThreadLocal<Boolean> initialized = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return false;
		}
	};
	private final ThreadLocal<Collection<Reference<? extends Base>>> references = new ThreadLocal<Collection<Reference<? extends Base>>>();

	private ThreadManager() {
		super();
	}

	/**
	 * Returns the instance.
	 * 
	 * @return the instance
	 */
	public static synchronized ThreadManager getInstance() {
		if (null == theInstance) {
			theInstance = new ThreadManager();
		}
		return theInstance;
	}

	/**
	 * Initializes the current thread for the usage of Notes objects. This is
	 * automatically called by {@link NotesConnector}.
	 */
	void initThread() {
		if (!initialized.get()) {
			getLogger().log(
					DEBUG,
					"Initializing Notes Thread "
							+ Thread.currentThread().getId() + "("
							+ Thread.currentThread().getName() + ")");
			try {
				NotesThread.sinitThread();
			} catch (Throwable t) {
				getLogger().log(
						WARNING,
						"Could not initialize Notes Thread "
								+ Thread.currentThread().getId() + "("
								+ Thread.currentThread().getName() + ")", t);
			}
			initialized.set(true);
		}
	}

	/**
	 * Destroys all Notes objects that were created at the current thread. You
	 * should call this method to avoid memory leaks.
	 */
	public void termThread() {
		getLogger().log(
				DEBUG,
				"Terminating Notes Thread " + Thread.currentThread().getId()
						+ "(" + Thread.currentThread().getName() + ")");
		final Collection<Reference<? extends Base>> referencesCol = references
				.get();
		if (null != referencesCol) {
			for (Reference<? extends Base> reference : new HashSet<Reference<? extends Base>>(
					referencesCol)) {
				try {
					if (null != reference)
						reference.recycle();
				} catch (Throwable t) {
					getLogger().log(DEBUG, "Reference could not be recycled.",
							t);
				}
			}
			references.remove();
		}
	}

	// public void termApplication() {
	// if(initialized.get()) {
	// getLogger().log(DEBUG, "Terminating Notes Thread " +
	// Thread.currentThread().getId() + "(" + Thread.currentThread().getName() +
	// ")");
	// try {
	// NotesThread.stermThread();
	// } catch (Throwable t) {
	// // Suppress because it may not be initialized
	// //getLogger().log(WARNING, "Session could not be recycled.", t);
	// }
	// initialized.remove();
	// }
	// }

	/**
	 * Registers a reference to a Notes object
	 * 
	 * @param reference
	 *            the reference
	 */
	void register(Reference<? extends Base> reference) {
		Collection<Reference<? extends Base>> referenceCol = references.get();
		if (null == referenceCol) {
			referenceCol = new HashSet<Reference<? extends Base>>();
			references.set(referenceCol);
		}
		;
		getLogger().log(
				DEBUG,
				"Registering reference " + reference + " for thread "
						+ Thread.currentThread().getId() + "("
						+ Thread.currentThread().getName());
		referenceCol.add(reference);
	}

	/**
	 * Removes a reference from the registry.
	 * 
	 * @param reference
	 *            the reference
	 */
	void unregister(Reference<? extends Base> reference) {
		getLogger().log(
				DEBUG,
				"Unregistering reference " + reference + " for thread "
						+ Thread.currentThread().getId() + "("
						+ Thread.currentThread().getName());
		final Collection<Reference<? extends Base>> referenceCol = references
				.get();
		if (null != referenceCol)
			referenceCol.remove(reference);
	}

	/**
	 * Releases the memory that Notes objects block.
	 * 
	 * @param dao
	 *            the {@link Dao}
	 * @throws DataAccessException
	 */
	public void recycle(Dao dao) throws DataAccessException {
		final DataObject dataObject = dao.getDataObject();
		if (dataObject instanceof NotesDataObject)
			try {
				((NotesDataObject) dataObject).recycle();
			} catch (NotesException e) {
				throw new DataAccessException(e);
			}
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
