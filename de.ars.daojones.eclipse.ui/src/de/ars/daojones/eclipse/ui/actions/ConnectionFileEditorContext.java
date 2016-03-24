package de.ars.daojones.eclipse.ui.actions;

import java.beans.PropertyChangeSupport;

import org.eclipse.core.resources.IFile;

import de.ars.daojones.connections.model.IConnectionConfiguration;

/**
 * The context to execute the operation to create an import.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ConnectionFileEditorContext extends PropertyChangeSupport {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 6250629341387567350L;
	/**
	 * The name of the property for the import declarations.
	 */
	public static final String PROPERTY_IMPORTS = "imports";
	/**
	 * The name of the property for the credentials.
	 */
	public static final String PROPERTY_CREDENTIALS = "credentials";
	/**
	 * The name of the property for the connections.
	 */
	public static final String PROPERTY_CONNECTIONS = "connections";
	
	
	// the connection configuration where to insert the import declaration
	private final IConnectionConfiguration configuration;
	// the IResource that contains the configuration
	private final IFile configurationFile;
	
	/**
	 * Creates an instance.
	 * @param configuration the {@link IConnectionConfiguration}
	 * @param configurationFile the {@link IFile} containing the configuration
	 */
	public ConnectionFileEditorContext(IConnectionConfiguration configuration, IFile configurationFile) {
		super(configuration);
		this.configuration = configuration;
		this.configurationFile = configurationFile;
	}
	
	/**
	 * Returns the {@link IConnectionConfiguration}.
	 * @return the {@link IConnectionConfiguration}
	 */
	public IConnectionConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Returns the {@link IFile} containing the configuration.
	 * @return the {@link IFile} containing the configuration
	 */
	public IFile getConfigurationFile() {
		return configurationFile;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((configuration == null) ? 0 : configuration.hashCode());
		result = prime
				* result
				+ ((configurationFile == null) ? 0 : configurationFile
						.hashCode());
		return result;
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
		ConnectionFileEditorContext other = (ConnectionFileEditorContext) obj;
		if (configuration == null) {
			if (other.configuration != null)
				return false;
		} else if (!configuration.equals(other.configuration))
			return false;
		if (configurationFile == null) {
			if (other.configurationFile != null)
				return false;
		} else if (!configurationFile.equals(other.configurationFile))
			return false;
		return true;
	}

	
	
}
