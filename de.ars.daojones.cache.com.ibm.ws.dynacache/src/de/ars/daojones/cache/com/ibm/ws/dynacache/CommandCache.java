/**
 * 
 */
package de.ars.daojones.cache.com.ibm.ws.dynacache;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.ibm.websphere.cache.DynamicCacheAccessor;
import com.ibm.websphere.command.CommandException;
import com.ibm.ws.cache.util.SerializationUtility;
import de.ars.daojones.cache.Cache;
import de.ars.daojones.cache.CacheException;
import de.ars.daojones.cache.CacheKey;
import de.ars.daojones.cache.CacheValue;
import de.ars.daojones.cache.RepeatableCommand;

/**
 * An implementation of {@link Cache} using Websphere Command Cache.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 */
@SuppressWarnings("restriction")
public class CommandCache<K, V> implements Cache<K, V> {

	private static final Logger logger = Logger.getLogger(CommandCache.class
			.getName());

	/**
	 * @see de.ars.daojones.cache.Cache#put(CacheKey, RepeatableCommand,
	 *      boolean)
	 */
	public CacheValue<V> put(CacheKey<K> key,
			RepeatableCommand<CacheValue<V>> callable, boolean overwriteExisting)
			throws CacheException {
		// check the DynaCache to be enabled
		if (!DynamicCacheAccessor.isCachingEnabled()) {
			logger
					.log(Level.WARNING,
							"Cannot use WebSphere Dynamic Cache because it is disabled!");
			try {
				return callable.call();
			} catch (Exception e) {
				throw new CacheException(e);
			}
		}
		checkSerialization(key, "The cache key cannot be serialized.");
		checkSerialization(callable, "The callable cannot be serialized.");
		final QueryCommand<K, V> cmd = new QueryCommand<K, V>();
		try {
			boolean valueInvalid = overwriteExisting;
			CacheValue<V> value = null;
			if (!valueInvalid) {
				cmd.setKey(key);
				cmd.setCallable(callable);
				cmd.execute();
				value = cmd.getValue();
				valueInvalid = null != value && !value.isValid();
			}
			if (valueInvalid) {
				DynamicCacheAccessor.getDistributedMap().invalidate(
						cmd.getClass().getName() + ":getCacheId="
								+ cmd.getCacheId(), true);
				final QueryCommand<K, V> cmd2 = new QueryCommand<K, V>();
				cmd2.setKey(key);
				cmd2.setCallable(callable);
				cmd2.execute();
				value = cmd2.getValue();
			}
			return value;
		} catch (CommandException e) {
			checkSerialization(cmd.getValue(),
					"The cache value cannot be serialized.");
			throw new CacheException("Error using Websphere DynaCache.",
					null != e.getException() ? e.getException() : e);
		}
	}

	/**
	 * @see de.ars.daojones.cache.Cache#put(de.ars.daojones.cache.CacheKey,
	 *      de.ars.daojones.cache.RepeatableCommand)
	 */
	public CacheValue<V> put(CacheKey<K> key,
			RepeatableCommand<CacheValue<V>> callable) throws CacheException {
		return put(key, callable, false);
	}

	/**
	 * @see de.ars.daojones.cache.Cache#clear()
	 */
	public void clear() throws CacheException {
		throw new CacheException(
				"Please use the CacheMonitor application to clear the cache!");
	}

	private static void checkSerialization(Serializable s, String message)
			throws CacheException {
		try {
			Serializable s2 = SerializationUtility
					.deserialize(SerializationUtility.serialize(s));
			if (logger.isLoggable(Level.FINER))
				logger.log(Level.FINER, "Serialization test successful: " + s
						+ " -> " + s2);
		} catch (NotSerializableException e) {
			throw new CacheException(message + " (" + s.getClass().getName()
					+ ")", e);
		} catch (IOException e) {
			throw new CacheException(message + " (" + s.getClass().getName()
					+ ")", e);
		} catch (ClassNotFoundException e) {
			throw new CacheException(message + " (" + s.getClass().getName()
					+ ")", e);
		}
	}

}
