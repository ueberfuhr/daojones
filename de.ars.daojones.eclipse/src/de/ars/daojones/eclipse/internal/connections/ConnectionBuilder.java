package de.ars.daojones.eclipse.internal.connections;

import static de.ars.daojones.eclipse.internal.Activator.PLUGIN_ID;
import static de.ars.daojones.eclipse.internal.Activator.log;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import de.ars.daojones.connections.ApplicationContext;
import de.ars.daojones.connections.ApplicationContextFactory;
import de.ars.daojones.connections.ConnectionBuildException;
import de.ars.daojones.connections.ConnectionFactory;
import de.ars.daojones.connections.model.Connection;
import de.ars.daojones.connections.model.ICredential;
import de.ars.daojones.connections.model.Interval;
import de.ars.daojones.connections.model.UserPasswordCredential;
import de.ars.daojones.eclipse.connections.ConnectionFactoryManager;
import de.ars.daojones.eclipse.connections.ConnectionFactoryMetaData;
import de.ars.daojones.eclipse.internal.Activator;
import de.ars.daojones.runtime.DataAccessException;

/**
 * This class reads the {@link IExtensionRegistry} for
 * connection elements and registers them in to the {@link ConnectionFactoryManager}.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class ConnectionBuilder {
	
	private static final String EXTENSION_CREDENTIALS = PLUGIN_ID + ".connectioncredentials";
	private static final String EXTENSION_FACTORIES   = PLUGIN_ID + ".connectionfactories";
	private static final String EXTENSION_CONNECTIONS = PLUGIN_ID + ".connections";
	
	private static ApplicationContext getContext(String id) {
		return ApplicationContextFactory.getInstance().getApplicationContext(null != id ? id : ApplicationContext.DEFAULT_ID); 
	}
	
	private static final Collection<String> applicationContextIds = new HashSet<String>();
	
	/**
	 * Reads the {@link IExtensionRegistry} for
	 * connection elements and registers them in to the {@link ConnectionFactoryManager}.
	 */
	public static void buildConnectionsFromExtensions() {
		// local variables
		final Map<String, ICredential> credentials = new HashMap<String, ICredential>();
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		
		// read factories
		final IConfigurationElement[] factoryElements = registry.getConfigurationElementsFor(EXTENSION_FACTORIES);
		for(IConfigurationElement factoryElement : factoryElements) {
			final ConnectionFactoryMetaData md = new ConnectionFactoryMetaData();
			try {
				md.setId(factoryElement.getAttribute("id"));
				md.setName(factoryElement.getAttribute("name"));
				md.setDescription(factoryElement.getAttribute("description"));
				md.setInstance((ConnectionFactory) factoryElement.createExecutableExtension("class"));
				ConnectionFactoryManager.getInstance().register(md);
			} catch (InvalidRegistryObjectException e) {
				log(IStatus.ERROR, "Connection Factory \"" + md.getName() + "\" could not be instantiated!", e);
			} catch (CoreException e) {
				log(IStatus.ERROR, "Connection Factory \"" + md.getName() + "\" could not be instantiated!", e);
			}
		}

		// read credentials
		final IConfigurationElement[] credentialElements = registry.getConfigurationElementsFor(EXTENSION_CREDENTIALS);
		for(IConfigurationElement credentialElement : credentialElements) {
			if("UserPassword".equals(credentialElement.getAttribute("type"))) {
				final UserPasswordCredential c = new UserPasswordCredential();
				c.setUsername(credentialElement.getAttribute("username"));
				c.setPassword(credentialElement.getAttribute("password"));
				credentials.put(credentialElement.getAttribute("id"), c);
				log(IStatus.INFO, "Successfully initialized credential for user \"" + c.getUsername() + "\".");
			}
		}
		
		// read connections
		final IConfigurationElement[] connectionElements = registry.getConfigurationElementsFor(EXTENSION_CONNECTIONS);
		for(IConfigurationElement connectionElement : connectionElements) {
			if("connection".equals(connectionElement.getName())) {
				final ConnectionFactoryMetaData md = ConnectionFactoryManager.getInstance().getFactory(connectionElement.getAttribute("factory"));
				final Connection con = new Connection() {
					private static final long serialVersionUID = -2698207419267147167L;
					@Override
					public ConnectionFactory createConnectionFactory()
							throws ConnectionBuildException {
						return null != md && null != md.getInstance() ? md.getInstance() :  super.createConnectionFactory();
					}
				};
				con.setName(connectionElement.getAttribute("name"));
				con.setDescription(connectionElement.getAttribute("description"));
				con.setDefault("true".equals(connectionElement.getAttribute("default")));
				con.setHost(connectionElement.getAttribute("host"));
				con.setDatabase(connectionElement.getAttribute("database"));
				if(null != md && null != md.getInstance()) con.setFactory(md.getInstance().getClass().getName());
				con.setCredential(credentials.get(connectionElement.getAttribute("credential")));
				for(IConfigurationElement child : connectionElement.getChildren()) {
					if("connectionpool".equals(child.getName())) {
						final Interval pool = new Interval();
						pool.setMinimum(new Integer(child.getAttribute("minCount")));
						pool.setMaximum(new Integer(child.getAttribute("maxCount")));
						con.setConnectionPool(pool);
					} else if("cache".equals(child.getName())) {
						con.setCached(true);
						String intervalStr = child.getAttribute("interval");
						if(null != intervalStr) {
							intervalStr = intervalStr.trim();
							if(intervalStr.endsWith("w")) {
								con.setCacheExpiration(new Long(intervalStr.substring(0, intervalStr.length()-1)) *7*24*60*60*1000);
							} else if(intervalStr.endsWith("d")) {
								con.setCacheExpiration(new Long(intervalStr.substring(0, intervalStr.length()-1)) *24*60*60*1000);
							} else if(intervalStr.endsWith("h")) {
								con.setCacheExpiration(new Long(intervalStr.substring(0, intervalStr.length()-1)) *60*60*1000);
							} else if(intervalStr.endsWith("m")) {
								con.setCacheExpiration(new Long(intervalStr.substring(0, intervalStr.length()-1)) *60*1000);
							} else if(intervalStr.endsWith("ms")) {
								con.setCacheExpiration(new Long(intervalStr.substring(0, intervalStr.length()-2)));
							} else if(intervalStr.endsWith("s")) {
								con.setCacheExpiration(new Long(intervalStr.substring(0, intervalStr.length()-1)) *1000);
							} else {
								con.setCacheExpiration(new Long(intervalStr) *1000);
							}
						}
					} else if("limitation".equals(child.getName())) {
						con.setMaxResults(new Integer(child.getAttribute("maxResults")));
					} else if("forClass".equals(child.getName())) {
						con.getForClasses().add(child.getAttribute("class"));
					}
				}
				getContext(connectionElement.getAttribute("applicationContext")).createConnection(con);
				applicationContextIds.add(connectionElement.getAttribute("applicationContext"));
			} else if("connectionfile".equals(connectionElement.getName())) {
				try {
					final Bundle declaringBundle = Platform.getBundle(connectionElement.getContributor().getName());
					getContext(connectionElement.getAttribute("applicationContext"))
						.createConnections(FileLocator.openStream(declaringBundle, new Path(connectionElement.getAttribute("filename")), true));
					applicationContextIds.add(connectionElement.getAttribute("applicationContext"));
					log(IStatus.INFO, "Connections successfully read from file \"" + connectionElement.getAttribute("filename") + "\"!");
				} catch (ConnectionBuildException e) {
					log(IStatus.ERROR, "Error reading connections from file \"" + connectionElement.getAttribute("filename") + "\"!", e);
				} catch (IOException e) {
					log(IStatus.ERROR, "Error reading connections from file \"" + connectionElement.getAttribute("filename") + "\"!", e);
				}
			}
		};

	}
	
	/**
	 * Destroys the connections.
	 */
	public static void destroyConnections() {
		try {
			for(String applicationContextId : applicationContextIds) {
				ApplicationContextFactory.getInstance().getApplicationContext(applicationContextId).destroy();
			};
			applicationContextIds.clear();
		} catch (DataAccessException e) {
			Activator.log(IStatus.ERROR, "Error during destroying connections!", e);
		}
	}
	
}
