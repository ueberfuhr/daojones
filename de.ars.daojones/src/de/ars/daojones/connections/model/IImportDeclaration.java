package de.ars.daojones.connections.model;

import java.io.Serializable;

/**
 * An {@link IImportDeclaration} is used to import
 * credentials and connections into a connection configuration.
 * You can use credentials from the imported file.
 * You can split a connection configuration into multiple files.
 * PLEASE BE CAREFUL NOT TO CREATE CYCLES!!!
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface IImportDeclaration extends IConnectionConfigurationElement, Serializable {

	/**
	 * Returns the name of the file relative to the current configuration file.
	 * @return  the name of the file
	 */
	public String getFile();
	
}
