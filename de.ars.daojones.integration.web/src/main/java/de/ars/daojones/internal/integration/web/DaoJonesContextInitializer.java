package de.ars.daojones.internal.integration.web;

import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import de.ars.daojones.integration.web.Configuration;
import de.ars.daojones.integration.web.Configuration.ApplicationScope;
import de.ars.daojones.integration.web.DaoJonesEnvironment;
import de.ars.daojones.runtime.configuration.context.ApplicationModelManager;
import de.ars.daojones.runtime.configuration.context.BeanModel;
import de.ars.daojones.runtime.configuration.context.BeanModelManager;
import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.configuration.context.ConnectionModelManager;
import de.ars.daojones.runtime.configuration.provider.AnnotationBeanConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.ConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.DaoJonesContextConfigurator;
import de.ars.daojones.runtime.configuration.provider.XmlBeanConfigurationSource;
import de.ars.daojones.runtime.configuration.provider.XmlConnectionConfigurationSource;
import de.ars.daojones.runtime.context.DaoJonesContext;

/**
 * Initializes the DaoJones environment during application startup.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
@WebListener
public class DaoJonesContextInitializer implements ServletContextListener {

  private static final Messages logger = Messages.create( "DaoJonesContextInitializer" );

  private static final char SCOPE_SEPARATOR = '#';

  @Inject
  private DaoJonesContext djContext;
  @Inject
  private DaoJonesEnvironmentProducer dep;

  private String getApplicationName( final ServletContext sc ) throws NamingException {
    final ApplicationScope applicationScope = Configuration.getApplicationScope( sc );
    final StringBuilder sb = new StringBuilder();
    final InitialContext ic = new InitialContext();
    if ( applicationScope == ApplicationScope.MODULE ) {
      sb.insert( 0, DaoJonesContextInitializer.SCOPE_SEPARATOR );
      sb.insert( 0, ic.lookup( "java:module/ModuleName" ) );
    }
    if ( applicationScope == ApplicationScope.MODULE || applicationScope == ApplicationScope.APPLICATION ) {
      sb.insert( 0, DaoJonesContextInitializer.SCOPE_SEPARATOR );
      sb.insert( 0, ic.lookup( "java:app/AppName" ) );
    }
    sb.append( Configuration.getApplicationName( sc ) );
    //    FINDBUGS DOES NOT ALLOW THIS:
    //    switch ( applicationScope ) {
    //    case MODULE:
    //      sb.insert( 0, DaoJonesContextInitializer.SCOPE_SEPARATOR );
    //      sb.insert( 0, ic.lookup( "java:module/ModuleName" ) );
    //      // Fall-through
    //    case APPLICATION:
    //      sb.insert( 0, DaoJonesContextInitializer.SCOPE_SEPARATOR );
    //      sb.insert( 0, ic.lookup( "java:app/AppName" ) );
    //      // Fall-through
    //    case PUBLIC:
    //      sb.append( Configuration.getApplicationName( sc ) );
    //    }
    return sb.toString();
  }

  @Override
  public void contextInitialized( final ServletContextEvent sce ) {
    try {
      final ServletContext sc = sce.getServletContext();
      final Collection<ConfigurationSource> configurations = new LinkedList<ConfigurationSource>();
      final String app = getApplicationName( sc );
      if ( !Configuration.isSkipConfiguration( sc ) ) {
        // Read connection configuration
        final String[] ccFiles = Configuration.getConnectionConfigurationFiles( sc );
        for ( final String ccFile : ccFiles ) {
          final String ccFileNormalized = ccFile.startsWith( "/" ) ? ccFile.substring( 1 ) : ccFile;
          final URL[] urls = ClasspathUrlFinder.findResourceBases( ccFileNormalized );
          for ( final URL fragment : urls ) {
            final URL configFile = new URL( fragment, ccFileNormalized );
            configurations.add( new XmlConnectionConfigurationSource( app, configFile ) );
          }
        }
        // Read annotations for bean configuration
        final String[] bcFiles = Configuration.getBeanConfigurationFiles( sc );
        if ( Configuration.isScanAnnotations( sc ) ) {
          configurations.add( new AnnotationBeanConfigurationSource( app, bcFiles ) );
        }
        // Read bean configuration from xml files
        for ( final String bcFile : bcFiles ) {
          final String bcFileNormalized = bcFile.startsWith( "/" ) ? bcFile.substring( 1 ) : bcFile;
          final URL[] urls = ClasspathUrlFinder.findResourceBases( bcFileNormalized );
          for ( final URL fragment : urls ) {
            final URL configFile = new URL( fragment, bcFileNormalized );
            configurations.add( new XmlBeanConfigurationSource( app, configFile ) );
          }
        }
      }
      // Configure
      final DaoJonesContextConfigurator configurator = new DaoJonesContextConfigurator( djContext );
      configurator.configure( configurations );
      // Put into application scope
      final DaoJonesEnvironment dj = new DaoJonesEnvironmentImpl( djContext, app );
      final String environmentName = Configuration.getEnvironmentName( sc );
      sc.setAttribute( environmentName, dj );
      // Put into CDI context
      dep.setEnvironment( dj );
    } catch ( final RuntimeException e ) {
      DaoJonesContextInitializer.logger.log( Level.WARNING, e, "start.error" );
      // Can be catched by the server to avoid starting the JEE application.
      throw e;
    } catch ( final Exception e ) {
      DaoJonesContextInitializer.logger.log( Level.WARNING, e, "start.error" );
      // Can be catched by the server to avoid starting the JEE application.
      throw new RuntimeException( e );
    }
  }

  @Override
  public void contextDestroyed( final ServletContextEvent sce ) {
    try {
      final ServletContext sc = sce.getServletContext();
      if ( !Configuration.isSkipConfiguration( sc ) ) {
        // do not destroy the whole DaoJones context
        final String app = getApplicationName( sc );
        // remove bean models
        final BeanModelManager bmm = djContext.getConfiguration().getBeanModelManager();
        for ( final BeanModel model : bmm.getModels() ) {
          if ( app.equals( model.getId().getApplicationId() ) ) {
            bmm.deregister( model );
          }
        }
        // remove connection models
        final ConnectionModelManager cmm = djContext.getConfiguration().getConnectionModelManager();
        for ( final ConnectionModel model : cmm.getModels() ) {
          if ( app.equals( model.getId().getApplicationId() ) ) {
            cmm.deregister( model );
          }
        }
        // remove application model
        final ApplicationModelManager amm = djContext.getConfiguration().getApplicationModelManager();
        amm.deregister( app );
      }
    } catch ( final RuntimeException e ) {
      DaoJonesContextInitializer.logger.log( Level.WARNING, e, "stop.error" );
      // Can be catched by the server to avoid starting the JEE application.
      throw e;
    } catch ( final Exception e ) {
      DaoJonesContextInitializer.logger.log( Level.WARNING, e, "stop.error" );
      // Can be catched by the server to avoid starting the JEE application.
      throw new RuntimeException( e );
    }
  }

}
