package de.ars.daojones.internal.integration.web;

import de.ars.daojones.integration.web.DaoJonesEnvironment;
import de.ars.daojones.runtime.configuration.context.DaoJonesContextConfiguration;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.context.Application;
import de.ars.daojones.runtime.context.DaoJonesContext;

/**
 * Default implementation.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
final class DaoJonesEnvironmentImpl implements DaoJonesEnvironment {

  private static final Package DAOJONES_PACKAGE = DaoJonesContext.class.getPackage();

  private final DaoJonesContext djContext;
  private final String applicationName;

  public DaoJonesEnvironmentImpl( final DaoJonesContext djContext, final String applicationName ) {
    super();
    this.djContext = djContext;
    this.applicationName = applicationName;
  }

  @Override
  public String getTitle() {
    return DaoJonesEnvironmentImpl.DAOJONES_PACKAGE.getImplementationTitle();
  }

  @Override
  public String getVersion() {
    return DaoJonesEnvironmentImpl.DAOJONES_PACKAGE.getImplementationVersion();
  }

  @Override
  public String getVendor() {
    return DaoJonesEnvironmentImpl.DAOJONES_PACKAGE.getImplementationVendor();
  }

  @Override
  public DaoJonesContext getContext() {
    return djContext;
  }

  @Override
  public DaoJonesContextConfiguration getConfig() {
    return getContext().getConfiguration();
  }

  @Override
  public Application getApplication() {
    return getContext().getApplication( applicationName );
  }

  @Override
  public ConnectionProvider getConnectionProvider() {
    return getApplication();
  }

}
