package de.ars.daojones.connections;

import java.util.Properties;

import de.ars.daojones.cache.CacheException;
import de.ars.daojones.cache.CacheFactory;

/**
 * A transfer object for a cache configuration.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public final class CacheConfiguration {

	/**
	 * The name of the property containing the class name of the cache factory.
	 */
	public static final String PROPERTY_CACHEFACTORY = "cache.factory";

	private final CacheFactory cacheFactory;

	/**
	 * Creates an instance by reading {@link Properties}.
	 * @param p the {@link Properties}
	 * @throws CacheException
	 */
	public CacheConfiguration(Properties p) throws CacheException {
		this(resolveCacheFactory(p.getProperty(PROPERTY_CACHEFACTORY)));
	}
	/**
	 * Creates an instance.
	 * @param cachefactory the {@link CacheFactory}
	 */
	public CacheConfiguration(CacheFactory cachefactory) {
		super();
		this.cacheFactory = cachefactory;
	}

	private static CacheFactory resolveCacheFactory(String cacheFactoryClassName)
			throws CacheException {
		if (null == cacheFactoryClassName)
			throw new CacheException("You do not have any property named \""
					+ PROPERTY_CACHEFACTORY
					+ "\" in your properties for the cache configuration.");
		try {
			return (CacheFactory) Class.forName(cacheFactoryClassName.trim())
					.newInstance();
		} catch (Throwable t) {
			throw new CacheException(
					"An error occured during initialization of the cache driver.",
					t);
		}
	}

	/**
	 * Returns the {@link CacheFactory}.
	 * 
	 * @return the cacheFactory the {@link CacheFactory}
	 */
	public CacheFactory getCacheFactory() {
		return cacheFactory;
	}

}
