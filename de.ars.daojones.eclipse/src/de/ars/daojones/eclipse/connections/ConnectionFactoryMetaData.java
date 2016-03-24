package de.ars.daojones.eclipse.connections;

import de.ars.daojones.connections.ConnectionFactory;

/**
 * A transfer object containing the meta data of a Connection Factory.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ConnectionFactoryMetaData {

	private String id;
	private String name;
	private String description;
	private ConnectionFactory instance;
	
	/**
	 * Returns the id of the factory.
	 * @return the id of the factory
	 */
	public String getId() {
		return id;
	}
	/**
	 * Sets the id of the factory.
	 * @param id the id of the factory
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * Returns the human readable name of the factory.
	 * @return the human readable name of the factory
	 */
	public String getName() {
		return name;
	}
	/**
	 * Sets the human readable name of the factory.
	 * @param name the human readable name of the factory
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Returns the human readable description of the factory.
	 * @return the human readable description of the factory
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * Sets the human readable description of the factory.
	 * @param description the human readable description of the factory
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * Returns the factory object.
	 * @return the factory object
	 */
	public ConnectionFactory getInstance() {
		return instance;
	}
	/**
	 * Sets the factory object.
	 * @param instance the factory object
	 */
	public void setInstance(ConnectionFactory instance) {
		this.instance = instance;
	}
	
}
