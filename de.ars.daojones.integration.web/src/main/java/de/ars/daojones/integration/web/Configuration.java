package de.ars.daojones.integration.web;

import javax.servlet.ServletContext;

import de.ars.daojones.runtime.configuration.provider.AnnotationBeanConfigurationSource;

/**
 * This class provides the whole configuration of the web application.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 */
public final class Configuration {

  /**
   * The name of the initialization parameter to set a custom comma-separated
   * list of connection configuration files. Defaults to
   * <tt>{@value #PARAM_CONNECTION_CONFIG_FILES_DEFAULT}</tt>.
   */
  public static final String PARAM_CONNECTION_CONFIG_FILES = "daojones.CONNECTION_CONFIG_FILES";
  /**
   * The the default comma-separated list of connection configuration files.
   */
  public static final String PARAM_CONNECTION_CONFIG_FILES_DEFAULT = "WEB-INF/daojones-connections.xml,META-INF/daojones-connections.xml";

  /**
   * The name of the initialization parameter to set a custom comma-separated
   * list of connection configuration files. Defaults to
   * <tt>{@value #PARAM_BEAN_CONFIG_FILES_DEFAULT}</tt>.
   */
  public static final String PARAM_BEAN_CONFIG_FILES = "daojones.BEAN_CONFIG_FILES";
  /**
   * The default comma-separated list of connection configuration files.
   */
  public static final String PARAM_BEAN_CONFIG_FILES_DEFAULT = "/WEB-INF/daojones-beans.xml,"
          .concat( AnnotationBeanConfigurationSource.DEFAULT_CONFIG_FILE );

  /**
   * The name of the initialization parameter for the scan-annotations flag. If
   * <tt>true</tt>, the classpath of the web module is scanned for annotations.
   * Defaults to <tt>{@value #PARAM_SCAN_ANNOTATIONS_DEFAULT}</tt>.
   */
  public static final String PARAM_SCAN_ANNOTATIONS = "daojones.SCAN_ANNOTATIONS";
  /**
   * The default value for the scan-annotations flag.
   */
  public static final String PARAM_SCAN_ANNOTATIONS_DEFAULT = "true";

  /**
   * The name of the initialization parameter to set the application id.
   * Defaults to <tt>{@value #PARAM_APPLICATION_DEFAULT}</tt>.
   * 
   * @see #PARAM_APPLICATION_SCOPE
   */
  public static final String PARAM_APPLICATION = "daojones.APPLICATION";
  /**
   * The default application id.
   * 
   * @see #PARAM_APPLICATION_SCOPE_DEFAULT
   */
  public static final String PARAM_APPLICATION_DEFAULT = "web-app";

  /**
   * The scope of the DaoJones application.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
   * @since 2.0
   */
  public static enum ApplicationScope {
    /**
     * Only scoped for a single module. The application name and the module name
     * are used as prefix of the DaoJones application name.
     */
    MODULE,
    /**
     * Scoped for all modules within the same enterprise application that share
     * the same DaoJones library. The application name is used as prefix of the
     * DaoJones application name.
     */
    APPLICATION,
    /**
     * Scoped for all modules in all enterprise applications that share the same
     * DaoJones library. The DaoJones application name does not contain any
     * prefix.
     */
    PUBLIC;
  }

  /**
   * The name of the initialization parameter to set the application scope.
   * Defaults to <tt>{@value #PARAM_APPLICATION_SCOPE_DEFAULT}</tt>.
   */
  public static final String PARAM_APPLICATION_SCOPE = "daojones.APPLICATION_SCOPE";
  /**
   * The default application scope.
   */
  public static final String PARAM_APPLICATION_SCOPE_DEFAULT = ApplicationScope.MODULE.name().toLowerCase();

  /**
   * The name of the initialization parameter to set a custom name of the
   * DaoJones environment instance of type {@link DaoJonesEnvironment} within
   * the application scope. Defaults to
   * <tt>{@value #PARAM_ENV_NAME_DEFAULT}</tt>.
   * 
   * @see DaoJonesEnvironment
   */
  public static final String PARAM_ENV_NAME = "daojones.ENV_NAME";
  /**
   * The default name of the DaoJones environment instance of type
   * {@link DaoJonesEnvironment} within the application scope.
   * 
   * @see DaoJonesEnvironment
   */
  public static final String PARAM_ENV_NAME_DEFAULT = "dj";
  /**
   * Suppresses the automatic initialization of the DaoJones environment, if
   * <tt>true</tt>. Defaults to <tt>{@value #PARAM_SKIP_CONFIG_DEFAULT}</tt>.
   */
  public static final String PARAM_SKIP_CONFIG = "daojones.SKIP_CONFIG";
  /**
   * The default value of the skip-configuration flag.
   */
  public static final String PARAM_SKIP_CONFIG_DEFAULT = "false";

  private Configuration() {
    super();
  }

  private static String getContextParameter( final ServletContext ctx, final String name, final String defaultValue ) {
    final String value = ctx.getInitParameter( name );
    return null != value ? value : defaultValue;
  }

  /**
   * Returns the list of connection configuration files.
   * 
   * @param ctx
   *          the servlet context
   * @return the list of connection configuration files
   */
  public static String[] getConnectionConfigurationFiles( final ServletContext ctx ) {
    return Configuration.getContextParameter( ctx, Configuration.PARAM_CONNECTION_CONFIG_FILES,
            Configuration.PARAM_CONNECTION_CONFIG_FILES_DEFAULT ).split( "," );
  }

  /**
   * Returns the list of bean configuration files.
   * 
   * @param ctx
   *          the servlet context
   * @return the list of bean configuration files
   */
  public static String[] getBeanConfigurationFiles( final ServletContext ctx ) {
    return Configuration.getContextParameter( ctx, Configuration.PARAM_BEAN_CONFIG_FILES,
            Configuration.PARAM_BEAN_CONFIG_FILES_DEFAULT ).split( "," );
  }

  /**
   * Returns the scan-annotations flag. If <tt>true</tt>, the classpath of the
   * web module is scanned for annotations.
   * 
   * @param ctx
   *          the servlet context
   * @return the scan-annotations flag
   */
  public static boolean isScanAnnotations( final ServletContext ctx ) {
    return Boolean.valueOf( Configuration.getContextParameter( ctx, Configuration.PARAM_SCAN_ANNOTATIONS,
            Configuration.PARAM_SCAN_ANNOTATIONS_DEFAULT ) );
  }

  /**
   * Returns the name of the application.
   * 
   * @param ctx
   *          the servlet context
   * @return the name of the application
   */
  public static String getApplicationName( final ServletContext ctx ) {
    return Configuration.getContextParameter( ctx, Configuration.PARAM_APPLICATION,
            Configuration.PARAM_APPLICATION_DEFAULT );
  }

  /**
   * Returns the application scope.
   * 
   * @param ctx
   *          the servlet context
   * @return the application scope
   */
  public static ApplicationScope getApplicationScope( final ServletContext ctx ) {
    return ApplicationScope.valueOf( Configuration.getContextParameter( ctx, Configuration.PARAM_APPLICATION_SCOPE,
            Configuration.PARAM_APPLICATION_SCOPE_DEFAULT ).toUpperCase() );
  }

  /**
   * Returns the name of the DaoJones environment instance of type
   * {@link DaoJonesEnvironment} within the application scope.
   * 
   * @param ctx
   *          the servlet context
   * @return the name of the DaoJones environment instance of type
   *         {@link DaoJonesEnvironment} within the application scope
   */
  public static String getEnvironmentName( final ServletContext ctx ) {
    return Configuration.getContextParameter( ctx, Configuration.PARAM_ENV_NAME, Configuration.PARAM_ENV_NAME_DEFAULT );
  }

  /**
   * Returns the skip-configuration flag. If <tt>true</tt>, the initialization
   * of the DaoJones environment is suppressed. The {@link DaoJonesEnvironment}
   * object will get initialized anyway to avoid {@link NullPointerException}s.
   * 
   * @param ctx
   *          the servlet context
   * @return the skip-configuration flag
   */
  public static boolean isSkipConfiguration( final ServletContext ctx ) {
    return Boolean.valueOf( Configuration.getContextParameter( ctx, Configuration.PARAM_SKIP_CONFIG,
            Configuration.PARAM_SKIP_CONFIG_DEFAULT ) );
  }

}
