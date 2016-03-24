package de.ars.daojones.runtime.configuration.provider;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import de.ars.daojones.internal.runtime.utilities.Messages;
import de.ars.daojones.runtime.configuration.context.ApplicationModelManager;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.context.CacheFactoryModelManager;
import de.ars.daojones.runtime.configuration.context.CallbackHandlerFactoryModelManager;
import de.ars.daojones.runtime.configuration.context.ConfigurationModel;
import de.ars.daojones.runtime.configuration.context.ConfigurationModelManager;
import de.ars.daojones.runtime.configuration.context.ConnectionFactoryModelManager;
import de.ars.daojones.runtime.configuration.context.ConnectionModelManager;
import de.ars.daojones.runtime.configuration.context.ConverterModelManager;
import de.ars.daojones.runtime.configuration.context.DaoJonesContextConfiguration;
import de.ars.daojones.runtime.connections.events.ConnectionEventListener;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.context.DaoJonesContextFactory;

/**
 * A class that configures the DaoJones context. You can use the
 * {@link DaoJonesContextFactory} to pre-configure a context that is created
 * later, and you can use this class to post-configure the context after
 * creation.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public class DaoJonesContextConfigurator {

  private static final Messages nls = Messages.create( "configuration.provider.DaoJonesContextConfigurator" );
  private final Map<ConfigurationHandle, ConfigurationCommand> commandsByHandle = new HashMap<ConfigurationHandle, ConfigurationCommand>();
  private final DaoJonesContext context;

  /**
   * Constructor.
   * 
   * @param context
   *          the context that has to be configured
   */
  public DaoJonesContextConfigurator( final DaoJonesContext context ) {
    super();
    this.context = context;
  }

  /**
   * Returns the context that gets configured.
   * 
   * @return the context that gets configured
   */
  public DaoJonesContext getContext() {
    return context;
  }

  // Command Pattern
  private static interface ConfigurationCommand {
    void execute() throws ConfigurationException;

    void undo() throws ConfigurationException;
  }

  private class CompoundConfigurationCommand implements ConfigurationCommand {
    private final List<ConfigurationCommand> commands = new LinkedList<ConfigurationCommand>();

    private <Id extends Serializable, Model extends ConfigurationModel<Id>> void addCommandFor(
            final ConfigurationModelManager<Id, Model> modelManager, final ConfigurationProvider<Model> provider )
            throws ConfigurationException {
      commands.add( new ModelConfigurationCommand<Id, Model>( modelManager, provider ) );
    }

    private void addCommandFor( final ConfigurationProvider<ConnectionEventListener> provider )
            throws ConfigurationException {
      commands.add( new EventConfigurationCommand( provider ) );
    }

    @Override
    public void execute() throws ConfigurationException {
      for ( final ConfigurationCommand command : commands ) {
        command.execute();
      }
    }

    @Override
    public void undo() throws ConfigurationException {
      // we need to go backwards
      if ( !commands.isEmpty() ) {
        for ( final ListIterator<ConfigurationCommand> it = commands.listIterator( commands.size() ); it.hasPrevious(); ) {
          final ConfigurationCommand command = it.previous();
          command.undo();
        }
      }
    }

  }

  private class ModelConfigurationCommand<Id extends Serializable, Model extends ConfigurationModel<Id>> implements
          ConfigurationCommand {
    private final ConfigurationModelManager<Id, Model> modelManager;
    private final List<Model> models = new LinkedList<Model>();

    public ModelConfigurationCommand( final ConfigurationModelManager<Id, Model> modelManager,
            final ConfigurationProvider<Model> provider ) throws ConfigurationException {
      super();
      this.modelManager = modelManager;
      // read the models
      if ( null != provider ) {
        for ( final Model model : provider.readConfiguration() ) {
          models.add( model );
        }
      }
    }

    @Override
    public void execute() throws ConfigurationException {
      for ( final Model model : models ) {
        modelManager.register( model );
      }
    }

    @Override
    public void undo() {
      // we need to go backwards
      if ( !models.isEmpty() ) {
        for ( final ListIterator<Model> it = models.listIterator( models.size() ); it.hasPrevious(); ) {
          final Model model = it.previous();
          modelManager.deregister( model );
        }
      }
    }
  }

  private class EventConfigurationCommand implements ConfigurationCommand {
    private final List<ConnectionEventListener> listeners = new LinkedList<ConnectionEventListener>();

    public EventConfigurationCommand( final ConfigurationProvider<ConnectionEventListener> provider )
            throws ConfigurationException {
      super();
      // read the listeners
      if ( null != provider ) {
        for ( final ConnectionEventListener listener : provider.readConfiguration() ) {
          listeners.add( listener );
        }
      }

    }

    @Override
    public void execute() throws ConfigurationException {
      for ( final ConnectionEventListener listener : listeners ) {
        getContext().addConnectionEventListener( listener );
      }
    }

    @Override
    public void undo() {
      // we need to go backwards
      if ( !listeners.isEmpty() ) {
        for ( final ListIterator<ConnectionEventListener> it = listeners.listIterator( listeners.size() ); it
                .hasPrevious(); ) {
          final ConnectionEventListener listener = it.previous();
          getContext().removeConnectionEventListener( listener );
        }
      }
    }
  }

  /**
   * A handle to identify the configuration steps within the configuration. This
   * allows to deconfigure the context.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
   * @since 2.0
   */
  public static class ConfigurationHandle implements Serializable {
    // nothing in this class, just to have a named serializable object
    private static final long serialVersionUID = 1L;
  }

  /**
   * Unconfigures the context by using the given handle. The handle is no longer
   * valid after the deconfiguration is done, so invoking this method twice with
   * the same handle will result in a {@link ConfigurationException}.
   * 
   * @param handle
   *          the handle that was returned by a configuration method of this
   *          instance
   * @throws ConfigurationException
   *           if the handle is not valid for this configurator instance, or if
   *           the deconfiguration could not be done successfully
   */
  public void deconfigure( final ConfigurationHandle handle ) throws ConfigurationException {
    final ConfigurationCommand command = commandsByHandle.get( handle );
    if ( null != command ) {
      command.undo();
      commandsByHandle.remove( handle );
    } else {
      throw new ConfigurationException( DaoJonesContextConfigurator.nls.get( "error.nohandle" ) );
    }
  }

  /**
   * Configures the context by using the given configuration sources.
   * 
   * @param sources
   *          the configuration sources
   * @return a handle that can be used to deconfigure the context by removing
   *         the configured objects
   * @throws ConfigurationException
   * @see {@link #deconfigure(ConfigurationHandle)}
   */
  public ConfigurationHandle configure( final ConfigurationSource... sources ) throws ConfigurationException {
    return configure( Arrays.asList( sources ) );
  }

  /**
   * Configures the context by using the given configuration sources.
   * 
   * @param sources
   *          the configuration sources
   * @return a handle that can be used to deconfigure the context by removing
   *         the configured objects
   * @throws ConfigurationException
   * @see {@link #deconfigure(ConfigurationHandle)}
   */
  public ConfigurationHandle configure( final Iterable<ConfigurationSource> sources ) throws ConfigurationException {

    final ConfigurationHandle handle = new ConfigurationHandle();
    final CompoundConfigurationCommand command = new CompoundConfigurationCommand();
    final DaoJonesContextConfiguration configuration = getContext().getConfiguration();

    /* *******************************************************************************
     *   A P P L I C A T I O N   I N D E P E N D E N T   C O N F I G U R A T I O N   *
     ******************************************************************************* */

    {
      // Connection Factories
      final ConnectionFactoryModelManager modelManager = configuration.getConnectionFactoryModelManager();
      for ( final ConfigurationSource source : sources ) {
        command.addCommandFor( modelManager, source.getConnectionFactoryModelConfigurationProvider() );
      }
    }

    {
      // Cache Factories
      final CacheFactoryModelManager modelManager = configuration.getCacheFactoryModelManager();
      for ( final ConfigurationSource source : sources ) {
        command.addCommandFor( modelManager, source.getCacheFactoryModelConfigurationProvider() );
      }
    }

    {
      // CallbackHandler Factories
      final CallbackHandlerFactoryModelManager modelManager = configuration.getCallbackHandlerFactoryModelManager();
      for ( final ConfigurationSource source : sources ) {
        command.addCommandFor( modelManager, source.getCallbackHandlerFactoryModelConfigurationProvider() );
      }
    }

    {
      // Applications
      final ApplicationModelManager modelManager = configuration.getApplicationModelManager();
      for ( final ConfigurationSource source : sources ) {
        command.addCommandFor( modelManager, source.getApplicationModelConfigurationProvider() );
      }
    }

    /* ***************************************************************************
     *   A P P L I C A T I O N   D E P E N D E N T   C O N F I G U R A T I O N   *
     *************************************************************************** */
    {
      // Global Converters
      final ConverterModelManager modelManager = configuration.getBeanModelManager().getConverterModelManager();
      for ( final ConfigurationSource source : sources ) {
        command.addCommandFor( modelManager, source.getGlobalConverterModelConfigurationProvider() );
      }
    }

    {
      // Beans
      final BeanModelManager modelManager = configuration.getBeanModelManager();
      for ( final ConfigurationSource source : sources ) {
        command.addCommandFor( modelManager, source.getBeanModelConfigurationProvider() );
      }
    }

    {
      // Connections
      final ConnectionModelManager modelManager = configuration.getConnectionModelManager();
      for ( final ConfigurationSource source : sources ) {
        command.addCommandFor( modelManager, source.getConnectionModelConfigurationProvider() );
      }
    }

    /* *****************************************
     *   F U R T H E R   E X T E N S I O N S   *
     ***************************************** */

    {
      // Connection Event Listeners
      for ( final ConfigurationSource source : sources ) {
        command.addCommandFor( source.getConnectionEventListenerConfigurationProvider() );
      }
    }
    // execute configuration here!
    command.execute();
    commandsByHandle.put( handle, command );
    return handle;

  }
}
