package de.ars.daojones.internal.runtime.connections;

import de.ars.daojones.runtime.query.Query;
import de.ars.daojones.runtime.spi.cache.CacheKey;
import de.ars.daojones.runtime.spi.cache.CompositeCacheKey;

/**
 * A DaoJones database query that is used as {@link CacheKey}.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class CachedQuery extends CompositeCacheKey {

  private static final long serialVersionUID = -2260526846723969766L;

  /**
   * Creates an instance.
   * 
   * @param query
   *          the query
   */
  public CachedQuery( Query query ) {
    super( query );
  }

}
