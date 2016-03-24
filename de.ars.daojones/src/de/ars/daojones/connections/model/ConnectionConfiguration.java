package de.ars.daojones.connections.model;

import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.CONFIGURATION_TAG;
import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.CONNECTION_TAG;
import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.CREDENTIAL_TAG;
import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.IMPORT_TAG;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.ars.daojones.connections.model.io.IReadableConnectionConfigurationSource;

/**
 * Default implementation of {@link IConnectionConfiguration}.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name=CONFIGURATION_TAG)
public class ConnectionConfiguration implements IConnectionConfiguration {

	private static final long serialVersionUID = 8200431581239595254L;
	
	@XmlElement(name=IMPORT_TAG, required=false, type=ImportDeclaration.class)
	private Collection<IImportDeclaration> importDeclarations;
	@XmlElement(name=CREDENTIAL_TAG, required=false, type=UserPasswordCredential.class)
	private Collection<ICredential> credentials;
	@XmlElement(name=CONNECTION_TAG, required=false, type=Connection.class)
	private Collection<IConnection> connections;
	// @XmlTransient
	private transient IReadableConnectionConfigurationSource source;

	private static void checkForParent(Collection<?> col, ConnectionConfiguration c) {
		for(Object o : col) checkForParent(o, c);
	}
	private static void checkForParent(Collection<?> col, ConnectionConfiguration c, ConnectionConfiguration cIf) {
		for(Object o : col) checkForParent(o, c, cIf);
	}
	private static void checkForParent(Object o, ConnectionConfiguration c) {
		if(o instanceof AbstractConnectionConfigurationElement)
			((AbstractConnectionConfigurationElement)o).setConnectionConfiguration(c);
	}
	private static void checkForParent(Object o, ConnectionConfiguration c, ConnectionConfiguration cIf) {
		if(o instanceof AbstractConnectionConfigurationElement && ((AbstractConnectionConfigurationElement)o).getConnectionConfiguration() == cIf)
			((AbstractConnectionConfigurationElement)o).setConnectionConfiguration(c);
	}
	
	private <T>Collection<T> createCollection() {
		return new LinkedList<T>() {
			private static final long serialVersionUID = -3004579369551404958L;
			@Override
			public boolean add(T t) {
				checkForParent(t, ConnectionConfiguration.this);
				return super.add(t);
			}
			@Override
			public boolean addAll(Collection<? extends T> col) {
				checkForParent(col, ConnectionConfiguration.this);
				return super.addAll(col);
			}
			@Override
			public void clear() {
				checkForParent(this, null);
				super.clear();
			}
			@Override
			public Iterator<T> iterator() {
				final Iterator<T> result = super.iterator();
				return new Iterator<T>() {
					private T lastRead;
					// TODO Java6-Migration
//					@Override
					public boolean hasNext() {
						return result.hasNext();
					}
					// TODO Java6-Migration
//					@Override
					public T next() {
						lastRead = result.next();
						return lastRead;
					}
					// TODO Java6-Migration
//					@Override
					public void remove() {
						checkForParent(lastRead, null);
						result.remove();
					}
				};
			}
			@Override
			public boolean remove(Object o) {
				checkForParent(o, null, ConnectionConfiguration.this);
				return super.remove(o);
			}
			@Override
			public boolean removeAll(Collection<?> c) {
				checkForParent(c, null, ConnectionConfiguration.this);
				return super.removeAll(c);
			}
		};
	}
	
	/**
	 * @see de.ars.daojones.connections.model.IConnectionConfiguration#getImportDeclarations()
	 */
	// TODO Java6-Migration
//	@Override
	public Collection<IImportDeclaration> getImportDeclarations() {
		synchronized (this) {
			if(null == importDeclarations) {
				importDeclarations = createCollection();
			}
		}
		return importDeclarations;
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnectionConfiguration#getCredentials()
	 */
	// TODO Java6-Migration
//	@Override
	public Collection<ICredential> getCredentials() {
		synchronized (this) {
			if(null == credentials) {
				credentials = createCollection();
			}
		}
		return credentials;
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnectionConfiguration#getConnections()
	 */
	// TODO Java6-Migration
//	@Override
	public Collection<IConnection> getConnections() {
		synchronized (this) {
			if(null == connections) {
				connections = createCollection();
			}
		}
		return connections;
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnectionConfiguration#getSource()
	 */
	// TODO Java6-Migration
//	@Override
	public IReadableConnectionConfigurationSource getSource() {
		return source;
	}
	
	/**
	 * Sets the source containing the {@link ConnectionConfiguration}.
	 * @param source the source
	 * @see de.ars.daojones.connections.model.IConnectionConfiguration#getSource()
	 */
	public void setSource(IReadableConnectionConfigurationSource source) {
		this.source = source;
	}

	/**
	 * @see de.ars.daojones.connections.model.IConnectionConfigurationElement#getConnectionConfiguration()
	 */
	// TODO Java6-Migration
//	@Override
	public IConnectionConfiguration getConnectionConfiguration() {
		return this;
	}
	
	/**
	 * Iterates through the childs and sets the parent.
	 */
	public void resolveParents() {
		// IMPORTS
		for(IImportDeclaration i : getImportDeclarations())	checkForParent(i, ConnectionConfiguration.this);
		// CREDENTIALS
		for(ICredential c : getCredentials())				checkForParent(c, ConnectionConfiguration.this);
		// CONNECTIONS
		for(IConnection c : getConnections())				checkForParent(c, ConnectionConfiguration.this);
	}

}
