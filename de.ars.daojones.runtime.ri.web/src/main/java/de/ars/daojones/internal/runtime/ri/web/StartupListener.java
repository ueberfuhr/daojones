package de.ars.daojones.internal.runtime.ri.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import de.ars.daojones.integration.cdi.DaoJonesApplication;
import de.ars.daojones.runtime.configuration.context.DaoJonesContextConfiguration;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.context.Application;
import de.ars.daojones.runtime.context.DaoJonesContext;

@WebListener
public class StartupListener implements ServletContextListener {

  private static final Logger logger = Logger.getLogger( StartupListener.class.getName() );

  @Inject
  private DaoJonesContext context;
  @Inject
  private DaoJonesContextConfiguration config;
  @Inject
  @DaoJonesApplication( "sample" )
  private Application djSampleApp; // not configured, but should be injected
  @Inject
  @DaoJonesApplication( "sample" )
  private ConnectionProvider djSampleConnectionProvider; // not configured, but should be injected

  @Override
  public void contextInitialized( final ServletContextEvent sce ) {
    // DaoJones must already be initialized here
    StartupListener.logger.log( Level.INFO, "Initializing application..." );
    StartupListener.logger.log( Level.INFO, "DaoJones title:\t\t"
            + DaoJonesContext.class.getPackage().getImplementationTitle() );
    StartupListener.logger.log( Level.INFO, "DaoJones version:\t\t"
            + DaoJonesContext.class.getPackage().getImplementationVersion() );
    StartupListener.logger.log( Level.INFO, "DaoJones vendor:\t\t"
            + DaoJonesContext.class.getPackage().getImplementationVendor() );
    StartupListener.logger.log( Level.INFO, "DaoJones context:\t\t" + context );
    StartupListener.logger.log( Level.INFO, "DaoJones config:\t\t" + config );
    StartupListener.logger.log( Level.INFO, "Sample application:\t\t" + djSampleApp );
    StartupListener.logger.log( Level.INFO, "Sample connection provider:\t" + djSampleConnectionProvider );
  }

  @Override
  public void contextDestroyed( final ServletContextEvent sce ) {

  }

}
