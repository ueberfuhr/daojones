package de.ars.daojones.internal.cache.ws;

import de.ars.daojones.runtime.configuration.context.CacheFactoryModelImpl;

/**
 * Cache factory model implementation for the Dynacache driver.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class DynacacheFactoryModel extends CacheFactoryModelImpl {

  private static final long serialVersionUID = 1L;

  private static final Messages bundle = Messages.create( "DynacacheFactoryModel" ); //$NON-NLS-1$

  /**
   * The id of this model.
   */
  public static final String ID = "com.ibm.ws.dynacache"; //$NON-NLS-1$

  public DynacacheFactoryModel() {
    super( DynacacheFactoryModel.ID, DynacacheFactory.class );
  }

  private final String name = DynacacheFactoryModel.bundle.get( "name" ); //$NON-NLS-1$
  private final String description = DynacacheFactoryModel.bundle.get( "description" ); //$NON-NLS-1$

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

}
