package de.ars.daojones.internal.runtime.cache;

import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.runtime.configuration.context.CacheFactoryModelImpl;

public class MemoryCacheFactoryModel extends CacheFactoryModelImpl {

  public static final String ID = "memory";

  private static final long serialVersionUID = 1L;
  private static final Messages bundle = Messages.create( "cache.MemoryCache" ); //$NON-NLS-1$

  public MemoryCacheFactoryModel() {
    super( MemoryCacheFactoryModel.ID, MemoryCacheFactory.class );
  }

  private final String name = MemoryCacheFactoryModel.bundle.get( "name" ); //$NON-NLS-1$
  private final String description = MemoryCacheFactoryModel.bundle.get( "description" ); //$NON-NLS-1$

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

}
