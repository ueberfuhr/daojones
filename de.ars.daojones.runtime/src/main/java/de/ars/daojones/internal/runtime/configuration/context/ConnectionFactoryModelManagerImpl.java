package de.ars.daojones.internal.runtime.configuration.context;

import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModel;
import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModelManager;

public class ConnectionFactoryModelManagerImpl extends ConfigurationModelManagerImpl<String, ConnectionFactoryModel>
        implements ConnectionFactoryModelManager {

  public ConnectionFactoryModelManagerImpl( final Class<ConnectionFactoryModel> modelClass ) {
    super( modelClass );
  }

}
