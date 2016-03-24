package de.ars.daojones.internal.runtime.configuration.context;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;

import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.runtime.configuration.context.ConfigurationModel;
import de.ars.daojones.runtime.configuration.context.ConfigurationModelManager;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;

/**
 * A class managing factories.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 * @param <Id>
 *          the model id type
 * @param <Model>
 *          the model type
 */
abstract class ConfigurationModelManagerImpl<Id extends Serializable, Model extends ConfigurationModel<Id>> implements
        ConfigurationModelManager<Id, Model> {

  private static final Messages logger = Messages.create( "configuration.context.ConfigurationModelManager" );
  private static final Level level = Level.FINER;

  private final Class<Model> modelClass;

  private Object[] toLogParams( final Model model ) {
    return new Object[] { modelClass.getName(), null != model ? model.getId() : null,
        null != model ? model.getName() : null };
  }

  /* *********************************************************
   *    I N S T A N C E   -   I M P L E M E N T A T I O N    *
   ********************************************************* */

  private Map<Id, Model> models = new HashMap<Id, Model>();

  ConfigurationModelManagerImpl( final Class<Model> modelClass ) {
    super();
    this.modelClass = modelClass;
  }

  @Override
  @SuppressWarnings( "unchecked" )
  public void register( final Model model ) throws ConfigurationException {
    models.put( model.getId(), model );
    registered( Arrays.asList( model ) );
    ConfigurationModelManagerImpl.logger.log( ConfigurationModelManagerImpl.level, "register", toLogParams( model ) );
  }

  @Override
  public void deregister( final Model model ) {
    deregister( model.getId() );
  }

  protected void registered( final Collection<Model> models ) {
  }

  protected void deregistered( final Collection<Model> models ) {
  }

  @Override
  @SuppressWarnings( "unchecked" )
  public void deregister( final Id id ) {
    final Model model = models.remove( id );
    if ( null != model ) {
      deregistered( Arrays.asList( model ) );
      ConfigurationModelManagerImpl.logger
              .log( ConfigurationModelManagerImpl.level, "deregister", toLogParams( model ) );
    }
  }

  @Override
  public Collection<Model> getModels() {
    return new HashSet<Model>( models.values() );
  }

  @Override
  public Model getModel( final Id id ) {
    return models.get( id );
  }

  /**
   * Clears all models.
   */
  public void clear() {
    final int size = models.size();
    if ( size > 0 ) {
      final Collection<Model> oldModels = new HashSet<Model>( models.values() );
      models.clear();
      deregistered( oldModels );
      ConfigurationModelManagerImpl.logger.log( ConfigurationModelManagerImpl.level, "clear", modelClass.getName(),
              size );
    }
  }

}
