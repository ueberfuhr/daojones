package de.ars.daojones.runtime.context;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.ars.daojones.internal.runtime.configuration.context.DaoJonesContextImpl;
import de.ars.daojones.internal.runtime.configuration.provider.ServiceLoaderConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.configuration.provider.ConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.DaoJonesContextConfigurator;

/**
 * A factory to create the instance of the DaoJones context. Each environment
 * uses this factory to configure the DaoJones Context.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2012
 * @since 2.0
 */
public class DaoJonesContextFactory {

  private final List<ConfigurationSource> configurationSources = new LinkedList<ConfigurationSource>();

  public DaoJonesContextFactory() {
    super();
    // initial invocation
    setConfigurationSources();
  }

  /**
   * This method sets the configuration sources. A service loader source is
   * added always.
   * 
   * @param configurationSources
   *          the configuration sources
   */
  public void setConfigurationSources( final ConfigurationSource... configurationSources ) {
    this.configurationSources.clear();
    this.configurationSources.add( new ServiceLoaderConfigurationSource() );
    this.configurationSources.addAll( Arrays.asList( configurationSources ) );
  }

  /**
   * Creates the DaoJones context.
   * 
   * @return the DaoJones context
   * @throws ConfigurationException
   */
  public DaoJonesContext createContext() throws ConfigurationException {
    final DaoJonesContext result = new DaoJonesContextImpl();
    // configure context
    final DaoJonesContextConfigurator configurator = new DaoJonesContextConfigurator( result );
    configurator.configure( configurationSources );
    // return context
    return result;
  }

}
