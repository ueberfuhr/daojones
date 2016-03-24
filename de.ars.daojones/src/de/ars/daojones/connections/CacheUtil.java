package de.ars.daojones.connections;

import de.ars.daojones.cache.Cache;
import de.ars.daojones.cache.CacheValue;
import de.ars.daojones.cache.DefaultCacheValue;
import de.ars.daojones.cache.ExpiratingCacheValue;

/**
 * A helper class providing methods for using the {@link Cache} API.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
abstract class CacheUtil {

	public static <X> X unwrap(CacheValue<X> value) {
		return null == value ? null : value.getData();
	}

	public static <X> CacheValue<X> wrap(X value, final long cacheInterval) {
		if (null == value)
			return null;
		CacheValue<X> result = new DefaultCacheValue<X>(value);
		if (cacheInterval > 0)
			result = new ExpiratingCacheValue<X>(result, System
					.currentTimeMillis()
					+ cacheInterval);
		return result;
	}

}
