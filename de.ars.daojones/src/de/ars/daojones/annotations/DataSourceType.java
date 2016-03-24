package de.ars.daojones.annotations;

import de.ars.daojones.connections.ConnectionMetaData;

/**
 * Specifies the kind of datasource. If you choose a view as datasource,
 * please note that you won't be able to create objects. Dependent from
 * the driver, you also won't be able to update or delete objects.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public enum DataSourceType {

	/**
	 * A single table where you can read, write or delete entries.
	 */
	TABLE,
	/**
	 * A couple of columns that can be read from multiple tables.
	 * If the driver supports joined tables, you won't be able to delete
	 * or create objects.
	 * @see ConnectionMetaData
	 */
	VIEW;
	
}
