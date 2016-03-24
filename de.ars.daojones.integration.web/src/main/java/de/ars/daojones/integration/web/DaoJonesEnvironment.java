package de.ars.daojones.integration.web;

import de.ars.daojones.runtime.configuration.context.DaoJonesContextConfiguration;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.context.Application;
import de.ars.daojones.runtime.context.DaoJonesContext;

/**
 * An interface to access the DaoJones environment within a web application. It
 * is also accessible with Java Server Pages or Facelets to display environment
 * details and to set parameters for EL function invocations.
 * 
 * To get the instance programmatically, you can use one of the methods provided
 * by the {@link WebApplication} class or use the {@link javax.inject.Inject}
 * annotation to get the instance injected. Within JSPs, there is an implicit
 * object for this instance. The name of this object is per default &quot;
 * <tt>dj</tt> &quot;, but can be configured by using the initialization
 * parameter {@value Configuration#PARAM_ENV_NAME}.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 * @see WebApplication
 */
public interface DaoJonesEnvironment {

  /**
   * Returns the title of the DaoJones distribution.
   * 
   * @return the title of the DaoJones distribution
   */
  String getTitle();

  /**
   * Returns the version of the DaoJones distribution.
   * 
   * @return the version of the DaoJones distribution
   */
  String getVersion();

  /**
   * Returns the vendor of the DaoJones distribution.
   * 
   * @return the vendor of the DaoJones distribution
   */
  String getVendor();

  /**
   * Returns the DaoJones context.
   * 
   * @return the DaoJones context
   */
  DaoJonesContext getContext();

  /**
   * Returns the DaoJones configuration. This method delegates to
   * 
   * <pre>
   * getContext().getConfiguration()
   * </pre>
   * 
   * @return the DaoJones configuration
   */
  DaoJonesContextConfiguration getConfig();

  /**
   * Returns the DaoJones application that is configured for this web
   * application.
   * 
   * @return the DaoJones application that is configured for this web
   *         application
   */
  Application getApplication();

  /**
   * Returns the DaoJones connection provider that is configured for this web
   * application.
   * 
   * @return the DaoJones connection provider that is configured for this web
   *         application
   */
  ConnectionProvider getConnectionProvider();

}
