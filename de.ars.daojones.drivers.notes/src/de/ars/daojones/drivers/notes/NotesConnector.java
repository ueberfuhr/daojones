package de.ars.daojones.drivers.notes;

import static de.ars.daojones.drivers.notes.LoggerConstants.ERROR;
import static de.ars.daojones.drivers.notes.LoggerConstants.WARNING;
import static de.ars.daojones.drivers.notes.LoggerConstants.getLogger;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.Session;
import de.ars.daojones.connections.ConnectionData;
import de.ars.daojones.connections.model.ICredential;
import de.ars.daojones.connections.model.UserPasswordCredential;

final class NotesConnector {

    private static final long serialVersionUID = 1099662567351655808L;
    private static final Level SESSION_EVENT_LEVEL = Level.FINER;

    private final ConnectionData data;
    private transient Reference<Session> session;
    private transient Reference<Database> database;
    private final Set<SessionListener> listeners = new HashSet<SessionListener>();

    public NotesConnector(final ConnectionData data) {
        super();
        this.data = data;
    }

    public ConnectionData getConnectionData() {
        return data;
    }

    /*
     * Use a static method because the connector itself is not serializable.
     */
    private static Session createSession(ConnectionData connectionData,
            Set<SessionListener> listeners) throws NotesException {
        getLogger().log(
                SESSION_EVENT_LEVEL,
                "Start creating session to host " + connectionData.getHost()
                        + ". (" + Thread.currentThread().getName() + ")");
        Session session;
        try {
            if (isLocal(connectionData)) {
                ThreadManager.getInstance().initThread();
                final boolean anonymous = getUsername(connectionData) == null
                        || "".equals(getUsername(connectionData).trim());
                session = NotesFactory.createSession((String) null,
                        (String) (anonymous ? null
                                : getUsername(connectionData)),
                        (String) (anonymous ? null
                                : getPassword(connectionData)));
            } else {
                session = NotesFactory.createSession(connectionData.getHost(),
                        getUsername(connectionData),
                        getPassword(connectionData));
            }
            session.setConvertMIME(false);
        } catch (NotesException e) {
            getLogger().log(
                    ERROR,
                    "An error occured when creating the session: "
                            + e.getMessage() + "\n - host: "
                            + connectionData.getHost() + "\n - user: "
                            + getUsername(connectionData) + "\n - pwd:  "
                            + "********" + "\n - db:   "
                            + connectionData.getDatabaseName());
            throw e;
        }
        getLogger().log(
                SESSION_EVENT_LEVEL,
                "Session created successfully:" + "\n - host: "
                        + connectionData.getHost() + "\n - user: "
                        + getUsername(connectionData) + "\n - pwd:  "
                        + "********" + "\n - db:   "
                        + connectionData.getDatabaseName());
        for (SessionListener listener : listeners) {
            listener.sessionCreated(session);
        }
        return session;
    }

    private Session getSessionObject() {
        synchronized (this) {
            if (null == session) {
                final ConnectionData connectionData = getConnectionData();
                final Set<SessionListener> connectorListeners = this.listeners;
                session = new ThreadReference<Session>(
                        new SerializableCallable<Session>() {
                            // THIS COULD BE AWESOME???
                            private final transient Set<SessionListener> listeners = new HashSet<SessionListener>(
                                    connectorListeners);
                            private static final long serialVersionUID = 1L;

                            public Session call() throws Exception {
                                return NotesConnector.createSession(
                                        connectionData, listeners);
                            }
                        });
            }
        }
        return session.get();
    }

    Database getDatabase() {
        if (null == database) {
            database = new ThreadReference<Database>(
                    new SerializableCallable<Database>() {
                        private static final long serialVersionUID = 1L;

                        public Database call() throws Exception {
                            final Session session = NotesConnector.this
                                    .getSession();
                            if (null != session) {
                                getLogger().log(
                                        SESSION_EVENT_LEVEL,
                                        "Database "
                                                + getConnectionData()
                                                        .getDatabaseName()
                                                + " on "
                                                + (null != getConnectionData()
                                                        .getHost() ? "host "
                                                        + getConnectionData()
                                                                .getHost()
                                                        : "local host")
                                                + " gets opened now.");
                                if (isLocal()) {
                                    ThreadManager.getInstance().initThread();
                                    return getSession().getDatabase(null, /*
                                                                       																	 * getConnectionData(
                                                                       																	 * )
                                                                       																	 * .getHost
                                                                       																	 * ().
                                                                       																	 * substring
                                                                       																	 * (
                                                                       																	 * LOCALFILE_IDENTIFICATOR
                                                                       																	 * .
                                                                       																	 * length
                                                                       																	 * ()) +
                                                                       																	 * "/" +
                                                                       																	 */
                                    getConnectionData().getDatabaseName());
                                } else {
                                    return getSession().getDatabase(
                                            getConnectionData().getHost(),
                                            getConnectionData()
                                                    .getDatabaseName());
                                }
                            } else {
                                return null;
                            }
                        }
                    });
        }
        return database.get();
    }

    boolean isLocal() {
        return isLocal(getConnectionData());
    }

    private static boolean isLocal(ConnectionData connectionData) {
        return null == connectionData.getHost()
                || connectionData.getHost().trim().length() < 1;
    }

    private static String getUsername(ConnectionData connectionData) {
        final ICredential credential = connectionData.getCredential();
        return null != credential
                && credential instanceof UserPasswordCredential ? ((UserPasswordCredential) credential)
                .getUsername()
                : null;
    }

    private static String getPassword(ConnectionData connectionData) {
        final ICredential credential = connectionData.getCredential();
        return null != credential
                && credential instanceof UserPasswordCredential ? ((UserPasswordCredential) credential)
                .getPassword()
                : null;
    }

    /**
     * Destroys a session.
     * 
     * @param fireEvents
     *            true, if all listeners should be notified.
     * @throws NotesException
     *             if an error occured when recycling the session
     */
    private Session destroySession(boolean fireEvents) throws NotesException {
        getLogger().log(
                SESSION_EVENT_LEVEL,
                "Start destroying session. ("
                        + Thread.currentThread().getName() + ")");
        final Session oldSession = getSessionObject();
        try {
            if (null != database)
                database.recycle();
        } finally {
            database = null;
            try {
                if (null != session)
                    session.recycle();
            } finally {
                session = null;
                if (fireEvents) {
                    for (SessionListener listener : listeners) {
                        listener.sessionDestroyed(oldSession);
                    }
                }
            }
        }
        getLogger().log(
                SESSION_EVENT_LEVEL,
                "Session destroyed successfully. ("
                        + Thread.currentThread().getName() + ")");
        return oldSession;
    }

    /**
     * Reconnects to the database.
     * 
     * @throws NotesException
     *             if an error occured when destroying the old or creating the
     *             new session.
     */
    private void refreshSession() throws NotesException {
        getLogger().log(
                SESSION_EVENT_LEVEL,
                "Start refreshing session. ("
                        + Thread.currentThread().getName() + ")");
        final Session oldSession = destroySession(false);
        final Session newSession = getSessionObject();
        getLogger().log(
                SESSION_EVENT_LEVEL,
                "Session refreshed successfully. "
                        + Thread.currentThread().getName() + "");
        if (oldSession != newSession) {
            for (SessionListener listener : listeners) {
                listener.sessionRefreshed(oldSession, getSessionObject());
            }
        }
    }

    /*
     * *************************************** I N T E R F A C E M E T H O D S *
     * **************************************
     */

    /**
     * Returns a session to the database.
     * 
     * @return a session to the database
     */
    Session getSession() throws NotesException {
        return getSessionObject();
    }

    /**
     * Destroys the connection to the database. After calling this method, you
     * can call getSession() and getDatabase() again to rebuild the connection.
     */
    public void close() {
        try {
            destroySession(true);
        } catch (NotesException e) {
            getLogger().log(WARNING, "Error destroying session!", e);
        }
    }

    /**
     * Refreshs the connection to the database.
     * 
     * @throws NotesException
     *             if an error occured when refreshing the connection.
     */
    void refresh() throws NotesException {
        refreshSession();
    }

    /**
     * Adds a session listener that is notified when creating, refreshing or
     * destroying the session.
     * 
     * @param l
     *            the session listener
     */
    public void addSessionListener(SessionListener l) {
        listeners.add(l);
    }

    /**
     * Removes a session listener.
     * 
     * @param l
     *            the session listener
     */
    public void removeSessionListener(SessionListener l) {
        listeners.remove(l);
    }

    /**
     * Returns an array containing all registered session listeners.
     * 
     * @return an array containing all registered session listeners
     */
    public SessionListener[] getSessionListeners() {
        return (SessionListener[]) listeners.toArray(new SessionListener[] {});
    }

    /**
     * Automatically destroys the session.
     * 
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getConnectionData().hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return getConnectionData().equals(
                ((NotesConnector) obj).getConnectionData());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Notes Connector to session with connectiondata "
                + this.getConnectionData();
    }
    
}
