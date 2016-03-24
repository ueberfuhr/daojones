package de.ars.daojones.connections.model;

import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.CONNECTION_CACHED;
import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.CONNECTION_CACHEEXPIRATION;
import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.CONNECTION_CONNECTIONPOOL;
import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.CONNECTION_CREDENTIAL;
import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.CONNECTION_CREDENTIALREF;
import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.CONNECTION_DATABASE;
import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.CONNECTION_DEFAULT;
import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.CONNECTION_DESCRIPTION;
import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.CONNECTION_FACTORY;
import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.CONNECTION_FORCLASS;
import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.CONNECTION_HOST;
import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.CONNECTION_MAXRESULTS;
import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.CONNECTION_NAME;
import static de.ars.daojones.connections.model.IConnectionConfigurationConstants.CONNECTION_TAG;

import java.util.Collection;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.ars.daojones.connections.ConnectionBuildException;
import de.ars.daojones.connections.ConnectionFactory;

/**
 * Default implementation for {@link IConnection} with JAXB annotations.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * </p>
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;import&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *                 &lt;attribute name=&quot;file&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}anyURI&quot; /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name=&quot;credential&quot; type=&quot;{http://www.ars.de/daojones/connections/}Credential&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;connection&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name=&quot;description&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *                   &lt;element name=&quot;host&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *                   &lt;element name=&quot;database&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *                   &lt;choice minOccurs=&quot;0&quot;&gt;
 *                     &lt;element name=&quot;credential&quot; type=&quot;{http://www.ars.de/daojones/connections/}Credential&quot;/&gt;
 *                     &lt;element name=&quot;credentialref&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *                   &lt;/choice&gt;
 *                   &lt;element name=&quot;forClass&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *                   &lt;element name=&quot;connectionpool&quot; type=&quot;{http://www.ars.de/daojones/connections/}Interval&quot; minOccurs=&quot;0&quot;/&gt;
 *                 &lt;/sequence&gt;
 *                 &lt;attribute name=&quot;name&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *                 &lt;attribute name=&quot;factory&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}QName&quot; /&gt;
 *                 &lt;attribute name=&quot;default&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}boolean&quot; default=&quot;false&quot; /&gt;
 *                 &lt;attribute name=&quot;cached&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}boolean&quot; default=&quot;false&quot; /&gt;
 *                 &lt;attribute name=&quot;cacheExpiration&quot; type=&quot;{http://www.ars.de/daojones/connections/}Natural&quot; /&gt;
 *                 &lt;attribute name=&quot;maxResults&quot; type=&quot;{http://www.ars.de/daojones/connections/}Natural&quot; default=&quot;2147483647&quot; /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = CONNECTION_TAG)
public class Connection extends AbstractConnectionConfigurationElement
        implements IChangeableConnection {

    private static final long serialVersionUID = 493339272420132167L;
    private static final Logger logger = Logger.getLogger(Connection.class
            .getName());

    @XmlAttribute(name = CONNECTION_NAME)
    @XmlJavaTypeAdapter(StringTrimAdapter.class)
    private String name = null;
    @XmlAttribute(name = CONNECTION_FACTORY)
    @XmlJavaTypeAdapter(StringTrimAdapter.class)
    private String factory = null;
    @XmlAttribute(name = CONNECTION_DEFAULT)
    private boolean _default = false;
    @XmlAttribute(name = CONNECTION_CACHED)
    private boolean cached = false;
    @XmlAttribute(name = CONNECTION_CACHEEXPIRATION)
    private long cacheExpiration;
    @XmlAttribute(name = CONNECTION_MAXRESULTS)
    private int maxResults = Integer.MAX_VALUE;

    @XmlElement(name = CONNECTION_DESCRIPTION)
    @XmlJavaTypeAdapter(StringTrimAdapter.class)
    private String description = null;
    @XmlElement(name = CONNECTION_HOST)
    @XmlJavaTypeAdapter(StringTrimAdapter.class)
    private String host = null;
    @XmlElement(name = CONNECTION_DATABASE)
    @XmlJavaTypeAdapter(StringTrimAdapter.class)
    private String database = null;
    @XmlElement(name = CONNECTION_CREDENTIAL, type = UserPasswordCredential.class)
    // TODO: Umwandlung: null, wenn CredentialReference
    private ICredential credential = null;
    @XmlElement(name = CONNECTION_CREDENTIALREF)
    private String credentialRef = null;
    @XmlTransient
    private ICredentialReference credentialRefInstance = null;
    @XmlElement(name = CONNECTION_FORCLASS, type = String.class)
    @XmlJavaTypeAdapter(StringTrimAdapter.class)
    private Collection<String> forClasses;
    @XmlElement(name = CONNECTION_CONNECTIONPOOL, type = Interval.class)
    private IInterval connectionPool;

    /**
     * Creates a new instance.
     */
    public Connection() {
        super();
    }

    /**
     * @see de.ars.daojones.connections.model.IConnection#getName()
     */
    // TODO Java6-Migration
    // @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            the name
     * @see de.ars.daojones.connections.model.IConnection#getName()
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see de.ars.daojones.connections.model.IConnection#getFactory()
     */
    // TODO Java6-Migration
    // @Override
    public String getFactory() {
        return factory;
    }

    /**
     * Sets the connection factory class name.
     * 
     * @param factory
     *            the connection factory class name
     * @see de.ars.daojones.connections.model.IConnection#getFactory()
     */
    public void setFactory(String factory) {
        this.factory = factory;
    }

    /**
     * @see de.ars.daojones.connections.model.IConnection#isDefault()
     */
    // TODO Java6-Migration
    // @Override
    public boolean isDefault() {
        return _default;
    }

    /**
     * Sets the default status.
     * 
     * @param _default
     *            the default status
     * @see de.ars.daojones.connections.model.IConnection#isDefault()
     */
    public void setDefault(boolean _default) {
        this._default = _default;
    }

    /**
     * @see de.ars.daojones.connections.model.IConnection#isCached()
     */
    // TODO Java6-Migration
    // @Override
    public boolean isCached() {
        return cached;
    }

    /**
     * Sets the cached status.
     * 
     * @param cached
     *            the cached status
     * @see de.ars.daojones.connections.model.IConnection#isCached()
     */
    public void setCached(boolean cached) {
        this.cached = cached;
    }

    /**
     * @see de.ars.daojones.connections.model.IConnection#getCacheExpiration()
     */
    // TODO Java6-Migration
    // @Override
    public long getCacheExpiration() {
        return cacheExpiration;
    }

    /**
     * Sets the cache expiration period.
     * 
     * @param cacheExpiration
     *            the cache expiration period
     * @see de.ars.daojones.connections.model.IConnection#getCacheExpiration()
     */
    public void setCacheExpiration(long cacheExpiration) {
        this.cacheExpiration = cacheExpiration;
    }

    /**
     * Returns the maximum number of results.
     * 
     * @return the maximum number of results
     * @see de.ars.daojones.connections.model.IConnection#getMaxResults()
     */
    // TODO Java6-Migration
    // @Override
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * Sets the maximum count of results.
     * 
     * @param maxResults
     *            the maximum count of results
     * @see de.ars.daojones.connections.model.IConnection#getMaxResults()
     */
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        if (this.maxResults < 1)
            this.maxResults = Integer.MAX_VALUE;
    }

    /**
     * @see de.ars.daojones.connections.model.IConnection#getDescription()
     */
    // TODO Java6-Migration
    // @Override
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     * 
     * @param description
     *            the description
     * @see de.ars.daojones.connections.model.IConnection#getDescription()
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @see de.ars.daojones.connections.model.IConnection#getHost()
     */
    // TODO Java6-Migration
    // @Override
    public String getHost() {
        return host;
    }

    /**
     * Sets the host.
     * 
     * @param host
     *            the host
     * @see de.ars.daojones.connections.model.IConnection#getHost()
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @see de.ars.daojones.connections.model.IConnection#getDatabase()
     */
    // TODO Java6-Migration
    // @Override
    public String getDatabase() {
        return database;
    }

    /**
     * Sets the database.
     * 
     * @param database
     *            the database
     * @see de.ars.daojones.connections.model.IConnection#getDatabase()
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * @see de.ars.daojones.connections.model.IConnection#getCredential()
     */
    // TODO Java6-Migration
    // @Override
    public ICredential getCredential() {
        if (null != this.credentialRef) {
            synchronized (this.credentialRef) {
                if (null == this.credentialRefInstance) {
                    this.credentialRefInstance = new CredentialReference(
                            this.credentialRef);
                }
                return this.credentialRefInstance;
            }
        } else {
            return this.credential;
        }
    }

    /**
     * Sets the credential.
     * 
     * @param credential
     *            the credential
     * @see de.ars.daojones.connections.model.IConnection#getCredential()
     */
    public void setCredential(ICredential credential) {
        if (null != credential && credential instanceof ICredentialReference) {
            this.credential = null;
            this.credentialRefInstance = (ICredentialReference) credential;
            this.credentialRef = this.credentialRefInstance.getReferenceId();
        } else {
            this.credential = credential;
            this.credentialRefInstance = null;
            this.credentialRef = null;
        }
    }

    /**
     * @see de.ars.daojones.connections.model.IConnection#getForClasses()
     */
    // TODO Java6-Migration
    // @Override
    public Collection<String> getForClasses() {
        synchronized (this) {
            if (null == forClasses) {
                forClasses = new TreeSet<String>();
            }
        }
        return forClasses;
    }

    /**
     * @see de.ars.daojones.connections.model.IChangeableConnection#setForClasses(Collection)
     */
    // TODO Java6-Migration
    // @Override
    public void setForClasses(Collection<String> value) {
        if (null == value) {
            this.forClasses = null;
        } else {
            this.forClasses = new TreeSet<String>();
            this.forClasses.addAll(value);
        }
    }

    /**
     * @see de.ars.daojones.connections.model.IConnection#getConnectionPool()
     */
    // TODO Java6-Migration
    // @Override
    public IInterval getConnectionPool() {
        return connectionPool;
    }

    /**
     * Sets the connection pool.
     * 
     * @param connectionPool
     *            the connection pool
     * @see de.ars.daojones.connections.model.IConnection#getConnectionPool()
     */
    public void setConnectionPool(IInterval connectionPool) {
        this.connectionPool = connectionPool;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (_default ? 1231 : 1237);
        result = prime * result
                + (int) (cacheExpiration ^ (cacheExpiration >>> 32));
        result = prime * result + (cached ? 1231 : 1237);
        result = prime * result
                + ((connectionPool == null) ? 0 : connectionPool.hashCode());
        result = prime * result
                + ((credential == null) ? 0 : credential.hashCode());
        result = prime * result
                + ((credentialRef == null) ? 0 : credentialRef.hashCode());
        result = prime * result
                + ((database == null) ? 0 : database.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((factory == null) ? 0 : factory.hashCode());
        result = prime * result
                + ((forClasses == null) ? 0 : forClasses.hashCode());
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + maxResults;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Connection other = (Connection) obj;
        if (_default != other._default)
            return false;
        if (cacheExpiration != other.cacheExpiration)
            return false;
        if (cached != other.cached)
            return false;
        if (connectionPool == null) {
            if (other.connectionPool != null)
                return false;
        } else if (!connectionPool.equals(other.connectionPool))
            return false;
        if (credential == null) {
            if (other.credential != null)
                return false;
        } else if (!credential.equals(other.credential))
            return false;
        if (credentialRef == null) {
            if (other.credentialRef != null)
                return false;
        } else if (!credentialRef.equals(other.credentialRef))
            return false;
        if (database == null) {
            if (other.database != null)
                return false;
        } else if (!database.equals(other.database))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (factory == null) {
            if (other.factory != null)
                return false;
        } else if (!factory.equals(other.factory))
            return false;
        if (forClasses == null) {
            if (other.forClasses != null)
                return false;
        } else if (!forClasses.equals(other.forClasses))
            return false;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (maxResults != other.maxResults)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    /**
     * @see de.ars.daojones.connections.model.IConnection#createConnectionFactory()
     */
    public ConnectionFactory createConnectionFactory()
            throws ConnectionBuildException {
        final ClassLoader classLoader = getClass().getClassLoader();
        try {
            return (ConnectionFactory) classLoader.loadClass(getFactory())
                    .newInstance();
        } catch (IllegalAccessException e) {
            throw new ConnectionBuildException(e);
        } catch (InstantiationException e) {
            throw new ConnectionBuildException(e);
        } catch (ClassNotFoundException e) {
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, classLoader.toString());
            }
            throw new ConnectionBuildException(e);
        }
    }

}
