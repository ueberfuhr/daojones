package de.ars.daojones.internal.runtime.cache;

import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.runtime.configuration.context.CacheFactoryModelImpl;

public class NoCacheFactoryModel extends CacheFactoryModelImpl {

  public static final String ID = "none"; //$NON-NLS-1$

  private static final long serialVersionUID = 1L;
  private static final Messages bundle = Messages.create( "cache.NoCache" ); //$NON-NLS-1$

  public NoCacheFactoryModel() {
    super( NoCacheFactoryModel.ID, NoCacheFactory.class );
  }

  private final String name = NoCacheFactoryModel.bundle.get( "name" ); //$NON-NLS-1$
  private final String description = NoCacheFactoryModel.bundle.get( "description" ); //$NON-NLS-1$

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

}
