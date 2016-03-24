package de.ars.daojones.internal.runtime.configuration.connections;

import de.ars.daojones.internal.runtime.cache.NoCacheFactoryModel;
import de.ars.daojones.runtime.configuration.connections.Cache;
import de.ars.daojones.runtime.configuration.connections.Connection;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModelManager;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

public class CacheNormalizer implements ConnectionModelNormalizer {

  @Override
  public void normalize( final ConnectionModel model, final ConnectionModelManager modelManager )
          throws ConfigurationException {
    final Connection connection = model.getConnection();
    if ( null == connection.getCache() ) {
      final Cache cache = new Cache();
      cache.setType( NoCacheFactoryModel.ID );
      connection.setCache( cache );
    }
  }
}
