package de.ars.daojones.events;

/**
 * An enumeration containing kinds of database access. These kinds are used for
 * event handling purposes. The kind of database access is provided by the
 * {@link DataAccessEvent} class.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public enum DataAccessType {

	/**
	 * A create operation.
	 */
	CREATE,
	/**
	 * A query operation.
	 */
	READ,
	/**
	 * An insert or update operation.
	 */
	WRITE,
	/**
	 * A delete operation.
	 */
	DELETE,
	/**
	 * The connection close operation.
	 */
	DISCONNECT;

}
