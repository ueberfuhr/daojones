package de.ars.daojones.connections.model;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.ars.daojones.connections.model.io.IReadableConnectionConfigurationSource;

/**
 * A connection configuration that resolves all references and imports.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ResolvedConnectionConfiguration implements IConnectionConfiguration {

	private static final long serialVersionUID = 6432412731570373631L;

	private final Collection<IConnection> connections = new LinkedList<IConnection>();
	private final Map<String, ICredential> credentials = new HashMap<String, ICredential>();
	private final IConnectionConfiguration original;
	private final Collection<String> callingStack = new LinkedList<String>();
	
	/**
	 * Creates an instance based on another configuration.
	 * @param conf the configuration.
	 * @throws IOException
	 */
	public ResolvedConnectionConfiguration(IConnectionConfiguration conf) throws IOException {
		this(conf, null);
	}
	
	private ResolvedConnectionConfiguration(IConnectionConfiguration conf, Collection<String> callingStack) throws IOException {
		super();
		this.original = conf;
		if(null != callingStack) this.callingStack.addAll(callingStack);
		resolve();
		
	}
	
	/*
	 * Strategy:
	 * =========
	 * Import configurations first (detect cycles!!!)
	 * Detect globally defined credentials by id (first import first), own definitions at the end
	 * Detect connections (first import first) and resolve their credential references
	 */
	private void resolve() throws IOException {
		// load imported configurations
		final IReadableConnectionConfigurationSource src = original.getSource();
		final List<IConnectionConfiguration> importedConfigurations = new LinkedList<IConnectionConfiguration>();
		for(IImportDeclaration importDeclaration : original.getImportDeclarations()) {
			if(callingStack.contains(importDeclaration.getFile())) throw new IOException("Cycle detected for configuration file import (\"" + importDeclaration.getFile() + "\")!");
			if(null == src) throw new IOException("Cannot resolve linked elements because of missing connection configuration source.");
			final IConnectionConfiguration conf = src.read(importDeclaration.getFile());
			final Collection<String> stack = new LinkedList<String>();
			stack.addAll(callingStack);
			stack.add(importDeclaration.getFile());
			final ResolvedConnectionConfiguration resolvedConf = new ResolvedConnectionConfiguration(conf, stack);
			importedConfigurations.add(resolvedConf);
		}
		// put credentials in a map
		for(IConnectionConfiguration conf : importedConfigurations) {
			for(ICredential cred : conf.getCredentials()) {
				credentials.put(cred.getId(), cred);
			}
		}
		for(ICredential cred : original.getCredentials()) {
			credentials.put(cred.getId(), cred);
		}
		credentials.remove(null);
		// resolve credentials
		final Collection<IConnection> resolvedConnections = new LinkedList<IConnection>();
		for(final IConnection con : original.getConnections()) {
			ICredential cred = con.getCredential();
			if(null == cred || !(cred instanceof ICredentialReference)) {
				resolvedConnections.add(con);
			} else {
				final Collection<String> referenceIDs = new HashSet<String>();
				while(null != cred && cred instanceof ICredentialReference) {
					final String id = ((ICredentialReference)con.getCredential()).getReferenceId();
					if(!referenceIDs.add(id)) throw new IOException("Cycle detected for reference id of credential (\"" + id + "\")!");
					cred = credentials.get(id);
				}
				final ICredential finalCred = cred;
				resolvedConnections.add(new ConnectionWrapper(con) {
					private static final long serialVersionUID = 4076311669340803134L;
					@Override
					public ICredential getCredential() {
						return finalCred;
					}
				});
			}
		}
		getConnections().clear();
		getConnections().addAll(resolvedConnections);
		for(IConnectionConfiguration conf : importedConfigurations) {
			getConnections().addAll(conf.getConnections());
		}
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnectionConfiguration#getConnections()
	 */
	// TODO Java6-Migration
//	@Override
	public Collection<IConnection> getConnections() {
		return connections;
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnectionConfiguration#getCredentials()
	 */
	// TODO Java6-Migration
//	@Override
	public Collection<ICredential> getCredentials() {
		return new LinkedList<ICredential>(credentials.values());
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnectionConfiguration#getImportDeclarations()
	 */
	// TODO Java6-Migration
//	@Override
	public Collection<IImportDeclaration> getImportDeclarations() {
		return new LinkedList<IImportDeclaration>();
	}

	/**
	 * @see IConnectionConfiguration#getSource()
	 */
	// TODO Java6-Migration
//	@Override
	public IReadableConnectionConfigurationSource getSource() {
		return original.getSource();
	}
	
	/**
	 * @see de.ars.daojones.connections.model.IConnectionConfiguration#setSource(de.ars.daojones.connections.model.io.IReadableConnectionConfigurationSource)
	 */
	// TODO Java6-Migration
//	@Override
	public void setSource(IReadableConnectionConfigurationSource source) {
		original.setSource(source);
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnectionConfigurationElement#getConnectionConfiguration()
	 */
	// TODO Java6-Migration
//	@Override
	public IConnectionConfiguration getConnectionConfiguration() {
		return original;
	}
	
}
