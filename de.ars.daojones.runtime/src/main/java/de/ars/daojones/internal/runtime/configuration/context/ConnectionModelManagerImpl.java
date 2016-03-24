package de.ars.daojones.internal.runtime.configuration.context;

import java.util.Collection;

import de.ars.daojones.internal.runtime.configuration.ModelNormalizer;
import de.ars.daojones.internal.runtime.configuration.ServiceLoaderCombinatedNormalizer;
import de.ars.daojones.internal.runtime.configuration.connections.ConnectionModelNormalizer;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModelManager;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.connections.events.ConnectionEvent;
import de.ars.daojones.runtime.connections.events.ConnectionEventType;

public class ConnectionModelManagerImpl extends ConfigurationModelManagerImpl<ConnectionModel.Id, ConnectionModel>
        implements ConnectionModelManager {

  private final EventManager eventManager;

  public ConnectionModelManagerImpl( final Class<ConnectionModel> modelClass, final EventManager eventManager ) {
    super( modelClass );
    this.eventManager = eventManager;
  }

  @Override
  protected void registered( final Collection<ConnectionModel> models ) {
    eventManager.fireEvent( new ConnectionEvent( ConnectionEventType.CONNECTION_MODEL_REGISTERED, this, models
            .toArray( new ConnectionModel[models.size()] ) ) );
    super.registered( models );
  }

  @Override
  protected void deregistered( final Collection<ConnectionModel> models ) {
    eventManager.fireEvent( new ConnectionEvent( ConnectionEventType.CONNECTION_MODEL_DEREGISTERED, this, models
            .toArray( new ConnectionModel[models.size()] ) ) );
    super.deregistered( models );
  }

  private static final ModelNormalizer<ConnectionModel.Id, ConnectionModel, ConnectionModelManager> connectionModelNormalizer = new ServiceLoaderCombinatedNormalizer<ConnectionModel.Id, ConnectionModel, ConnectionModelManager, ConnectionModelNormalizer>(
          ConnectionModelManagerImpl.class.getClassLoader(), ConnectionModelNormalizer.class );

  @Override
  public void register( final ConnectionModel model ) throws ConfigurationException {
    // normalize
    ConnectionModelManagerImpl.connectionModelNormalizer.normalize( model, this );
    // TODO Validate
    super.register( model );
  }

}
