package de.ars.daojones.drivers.notes;


import lotus.domino.Session;

/**
 * A listener for session events.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface SessionListener {
	
	/**
	 * This method is invoked after a session to a domino server was created.
	 * @param session the new session instance
	 */
	public void sessionCreated(Session session);
	/**
	 * This method is invoked after a session to a domino server was created again.
	 * This happens after a session timeout.
	 * @param oldSession the destroyed session instance
	 * @param newSession the created session instance
	 */
	public void sessionRefreshed(Session oldSession, Session newSession);
	/**
	 * This method is invoked when refreshing a session failed because of an error.
	 * @param session the destroyed session instance
	 */
	public void sessionDestroyed(Session session);

}
