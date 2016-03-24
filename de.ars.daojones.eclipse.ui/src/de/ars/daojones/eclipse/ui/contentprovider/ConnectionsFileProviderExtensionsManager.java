package de.ars.daojones.eclipse.ui.contentprovider;

import de.ars.daojones.eclipse.ui.Activator;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

/**
 * An extension manager that reads the {@link IExtensionRegistry}
 * and finds {@link FilteredContentProvider} extending the
 * {@link ConnectionsFileProvider}.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class ConnectionsFileProviderExtensionsManager {

	private static final String EXTENSION_POINT   = Activator.PLUGIN_ID + ".connectionsfileprovider";

	private static ConnectionsFileProviderExtensionsManager theInstance;
	private final Collection<FilteredContentProvider> providers = new HashSet<FilteredContentProvider>();
	
	private ConnectionsFileProviderExtensionsManager() {
		super();
		// read extension registry
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		final IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement ce : elements) {
			if("provider".equals(ce.getName())) {
				try {
					providers.add((FilteredContentProvider)ce.createExecutableExtension("class"));
				} catch (Throwable t) {
					Activator.log(IStatus.WARNING, "Unable to initialize extension for ConnectionsFileProvider!", t);
				}
			}
		}
	}
	
	/**
	 * Returns the singleton instance.
	 * @return the singleton instance
	 */
	public static synchronized ConnectionsFileProviderExtensionsManager getInstance() {
		if(null == theInstance) {
			theInstance = new ConnectionsFileProviderExtensionsManager();
		}
		return theInstance;
	}

	/**
	 * Returns a collection of {@link FilteredContentProvider}s that
	 * extend the {@link ConnectionsFileProvider}.
	 * This collection can be modified without any results
	 * to the original.
	 * @return the collection of {@link FilteredContentProvider}s
	 */
	public Collection<FilteredContentProvider> getProviders() {
		return new HashSet<FilteredContentProvider>(providers);
	}
	
}
