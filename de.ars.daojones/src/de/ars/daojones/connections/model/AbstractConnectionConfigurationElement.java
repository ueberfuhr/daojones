package de.ars.daojones.connections.model;

/**
 * An abstract class holding a connection configuration.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class AbstractConnectionConfigurationElement implements IConnectionConfigurationElement {

	private static final long serialVersionUID = 477465407422860356L;

	private IConnectionConfiguration connectionConfiguration;
	
	/**
	 * Creates a new instance.
	 * This is equal to call {@link AbstractConnectionConfigurationElement#AbstractConnectionConfigurationElement(IConnectionConfiguration)}
	 * with the parameter null.
	 */
	protected AbstractConnectionConfigurationElement() {
		this(null);
	}
	/**
	 * Creates a new instance.
	 * @param connectionConfiguration the connection configuration
	 */
	protected AbstractConnectionConfigurationElement(IConnectionConfiguration connectionConfiguration) {
		super();
		setConnectionConfiguration(connectionConfiguration);
	}
	
	/**
	 * @see de.ars.daojones.connections.model.IConnectionConfigurationElement#getConnectionConfiguration()
	 */
	// TODO Java6-Migration
//	@Override
	public IConnectionConfiguration getConnectionConfiguration() {
		return connectionConfiguration;
	}

	/**
	 * Sets the connection configuration.
	 * @param connectionConfiguration the connection configuration
	 * @see de.ars.daojones.connections.model.IConnectionConfigurationElement#getConnectionConfiguration()
	 */
	protected void setConnectionConfiguration(IConnectionConfiguration connectionConfiguration) {
		this.connectionConfiguration = connectionConfiguration;
	}

}
