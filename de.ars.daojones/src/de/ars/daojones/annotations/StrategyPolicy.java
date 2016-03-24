package de.ars.daojones.annotations;

import de.ars.daojones.connections.Connection;
import de.ars.daojones.runtime.DataObject;

/**
 * Defines the kind of accessing the database to read or write a property.
 * The defined literals have different semantics concering getters and setters.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public enum StrategyPolicy {

	/**
	 * The property is buffered but read when initializing the bean and
	 * stored when calling the setter. 
	 */
	IMMEDIATELY,
	/**
	 * The property is buffered and only read during the first getter call.
	 * The value is only stored to the database when explicitly calling the 
	 * {@link Connection#update(de.ars.daojones.runtime.Dao)} method.
	 */
	LAZY,
	/**
	 * The property is not buffered. The value is read on each getter call.
	 * On calling the setter, the value is only set to the internal {@link DataObject}
	 * and only stored when explicitly calling the 
	 * {@link Connection#update(de.ars.daojones.runtime.Dao)} method.
	 */
	ALWAYS;

}
