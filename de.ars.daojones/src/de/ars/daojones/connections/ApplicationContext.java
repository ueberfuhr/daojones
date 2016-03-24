package de.ars.daojones.connections;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.ars.daojones.cache.memory.MemoryCacheFactory;
import de.ars.daojones.connections.model.ConnectionConfiguration;
import de.ars.daojones.connections.model.IConnection;
import de.ars.daojones.connections.model.IConnectionConfiguration;
import de.ars.daojones.connections.model.IInterval;
import de.ars.daojones.connections.model.ResolvedConnectionConfiguration;
import de.ars.daojones.connections.model.io.IReadableConnectionConfigurationSource;
import de.ars.daojones.connections.model.io.XmlInputStreamConnectionConfigurationSource;
import de.ars.daojones.runtime.Dao;
import de.ars.daojones.runtime.DataAccessException;
import de.ars.daojones.runtime.search.SearchEngine;
import de.ars.daojones.runtime.search.SearchException;
import de.ars.daojones.runtime.search.SearchResultCollection;

/**
 * A class handling a collection of connections for a special context. If you
 * only have one context for connections, use {@link #getDefault()} as the
 * context.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class ApplicationContext extends ConnectionManager {

    /**
     * The id of the default {@link ApplicationContext}.
     */
    public static final String DEFAULT_ID = "default";

    private static ApplicationContext defaultContext;
    private CacheConfiguration cacheConfiguration;
    private final String id;
    private boolean valid = true;
    private final List<SearchEngine<?>> searchEngines = new LinkedList<SearchEngine<?>>();
    private final Map<Object, Object> scope = new HashMap<Object, Object>();

    /**
     * Creates a new instance with a special id.
     * 
     * @param id
     *            the id
     */
    protected ApplicationContext(final String id) {
        super();
        this.id = id;
        this.cacheConfiguration = new CacheConfiguration(
                new MemoryCacheFactory());
        ApplicationContextFactory.getInstance().register(this);
    }

    /**
     * Returns the {@link CacheConfiguration}.
     * 
     * @return the {@link CacheConfiguration}
     */
    public CacheConfiguration getCacheConfiguration() {
        return cacheConfiguration;
    }

    /**
     * Sets the {@link CacheConfiguration}.
     * 
     * @param cacheConfiguration
     *            the {@link CacheConfiguration}
     */
    public void setCacheConfiguration(CacheConfiguration cacheConfiguration) {
        this.cacheConfiguration = cacheConfiguration;
    }

    /**
     * Returns the id.
     * 
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the default {@link ApplicationContext}.
     * 
     * @return the default {@link ApplicationContext}
     */
    public static synchronized ApplicationContext getDefault() {
        if (null == defaultContext) {
            defaultContext = ApplicationContextFactory.getInstance()
                    .getApplicationContext(DEFAULT_ID);
        }
        ;
        return defaultContext;
    }

    /**
     * Destroys the {@link ApplicationContext} and all included connections.
     * 
     * @throws DataAccessException
     */
    public void destroy() throws DataAccessException {
        if (DEFAULT_ID.equals(getId()))
            defaultContext = null;
        super.destroyConnections();
        this.valid = false;
    }

    /**
     * Returns true before the {@link ApplicationContext} gets destroyed.
     * 
     * @return true or false
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * A facade method using the {@link ConnectionConfiguration} class.
     * 
     * @param in
     *            the InputStream
     * @throws IOException
     * @throws ConnectionBuildException
     */
    public void createConnections(final InputStream in) throws IOException,
            ConnectionBuildException {
        final IReadableConnectionConfigurationSource source = new XmlInputStreamConnectionConfigurationSource() {
            private static final long serialVersionUID = 1L;

            @Override
            protected InputStream getInputStream(String resource)
                    throws IOException {
                return in;
            }
        };
        createConnections(new ResolvedConnectionConfiguration(source.read(null)));
    }

    /**
     * Creates a set of connections.
     * 
     * @param conf
     *            the connection configuration
     * @throws ConnectionBuildException
     *             if creating the connections failed
     */
    public void createConnections(IConnectionConfiguration conf)
            throws ConnectionBuildException {
        try {
            final IConnectionConfiguration resolvedConfiguration = new ResolvedConnectionConfiguration(
                    conf);
            for (IConnection con : resolvedConfiguration.getConnections()) {
                createConnection(con);
            }
        } catch (IOException e) {
            throw new ConnectionBuildException(e);
        }
    }

    /**
     * @see de.ars.daojones.connections.ConnectionManager#createConnectionObject(IConnection,
     *      java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Connection<Dao> createConnectionObject(IConnection connection,
            Class<Dao> c) throws ConnectionBuildException {
        try {
            final ConnectionFactory factory = connection
                    .createConnectionFactory();
            final ConnectionData data = new ConnectionData(connection
                    .getDatabase(), connection.getHost(), connection
                    .getCredential(), connection.getMaxResults());
            final IInterval connectionPool = connection.getConnectionPool();
            int minCount = null != connectionPool ? connectionPool.getMinimum()
                    : 1;
            int maxCount = null != connectionPool ? connectionPool.getMaximum()
                    : 1;
            if (minCount < 1)
                throw new ConnectionBuildException(
                        "Minimum count of connections must be greater than zero!");
            if (maxCount < 1)
                throw new ConnectionBuildException(
                        "Maximum count of connections must be greater than zero!");
            if (minCount > maxCount)
                throw new ConnectionBuildException(
                        "Minimum count must not be greater than maximum count!");
            // pool
            Connection con = (maxCount > 1 ? new PooledConnection(this, c,
                    factory, data, minCount, maxCount) : factory
                    .createConnection(this, c, data));
            // cache
            if (connection.isCached()) {
                con = new CachedConnection(this, con, c, connection
                        .getCacheExpiration(), this.cacheConfiguration);
            }
            return con;
        } catch (NumberFormatException e) {
            throw new ConnectionBuildException(e);
        }
    }

    /**
     * Adds a {@link SearchEngine}.
     * 
     * @param engine
     *            the {@link SearchEngine}
     * @return true, if adding the {@link SearchEngine} was successful
     */
    public boolean addSearchEngine(SearchEngine<?> engine) {
        if (!this.searchEngines.contains(engine)
                && this.searchEngines.add(engine)) {
            engine.setContext(this);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes a {@link SearchEngine}.
     * 
     * @param engine
     *            the {@link SearchEngine}
     * @return true, if removing the {@link SearchEngine} was successful
     */
    public boolean removeSearchEngine(SearchEngine<?> engine) {
        if (this.searchEngines.remove(engine)) {
            engine.setContext(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns a copy of the internal list of {@link SearchEngine}s.
     * 
     * @return the list of {@link SearchEngine}s
     */
    public List<SearchEngine<?>> getSearchEngines() {
        return new LinkedList<SearchEngine<?>>(this.searchEngines);
    }

    /**
     * Sets all {@link SearchEngine}s.
     * 
     * @param searchEngines
     *            the {@link SearchEngine}s
     */
    public void setSearchEngines(List<SearchEngine<?>> searchEngines) {
        synchronized (this.searchEngines) {
            for (SearchEngine<?> engine : this.searchEngines) {
                engine.setContext(null);
            }
            this.searchEngines.clear();
            this.searchEngines.addAll(searchEngines);
            for (SearchEngine<?> engine : searchEngines) {
                engine.setContext(this);
            }
        }
    }

    /**
     * Searches using all registered search engines that can search for a given
     * bean type.
     * 
     * @param <T>
     *            the bean type
     * @param c
     *            the bean class
     * @param includeSubclasses
     *            a flag indicating whether to include subclasses of the bean
     *            type or not
     * @param options
     *            the search options
     * @param text
     *            the search text
     * @return the list of {@link SearchResultCollection}s
     * @throws SearchException
     */
    @SuppressWarnings("unchecked")
    public <T extends Dao> List<SearchResultCollection<T>> search(Class<T> c,
            boolean includeSubclasses, Map<String, Object> options,
            String... text) throws SearchException {
        final List<SearchResultCollection<T>> result = new LinkedList<SearchResultCollection<T>>();
        for (SearchEngine<?> engine : getSearchEngines()) {
            if (null == c
                    || c.getName().equals(engine.getSearchType().getName())
                    || includeSubclasses
                    && c.isAssignableFrom(engine.getSearchType())) {
                result.add((SearchResultCollection) engine
                        .search(options, text));
            }
        }
        return result;
    }

    /**
     * Searches using all registered search engines.
     * 
     * @param options
     *            the search options
     * @param text
     *            the search text
     * @return the {@link SearchResultCollection}
     * @throws SearchException
     */
    @SuppressWarnings("unchecked")
    public List<SearchResultCollection<?>> search(Map<String, Object> options,
            String... text) throws SearchException {
        return (List) search(null, true, options, text);
    }

    /**
     * Returns a map providing application scoped objects. This map is used by
     * the drivers to store non-serializable objects.
     * 
     * @noreference This method is not intended to be referenced by clients.
     * @return the application scope
     */
    public Map<Object, Object> getScope() {
        return this.scope;
    }

}
