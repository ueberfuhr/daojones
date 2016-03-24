package de.ars.daojones.internal.runtime.configuration.context;

import de.ars.daojones.runtime.configuration.context.CacheFactoryModel;
import de.ars.daojones.runtime.configuration.context.CacheFactoryModelManager;

public class CacheFactoryModelManagerImpl extends ConfigurationModelManagerImpl<String, CacheFactoryModel> implements
        CacheFactoryModelManager {

  public CacheFactoryModelManagerImpl( final Class<CacheFactoryModel> modelClass ) {
    super( modelClass );
  }

}
