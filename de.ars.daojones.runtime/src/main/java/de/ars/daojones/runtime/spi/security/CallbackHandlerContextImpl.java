package de.ars.daojones.runtime.spi.security;

import java.util.Properties;

/**
 * Default implementation of {@link CallbackHandlerContext}.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 2.0.0
 */
public class CallbackHandlerContextImpl implements CallbackHandlerContext {

  private final String application;
  private final Properties properties;

  /**
   * Creates an instance.
   * 
   * @param application
   *          the application id
   * @param properties
   *          the properties
   */
  public CallbackHandlerContextImpl( final String application, final Properties properties ) {
    super();
    this.application = application;
    this.properties = properties;
  }

  @Override
  public String getApplication() {
    return application;
  }

  @Override
  public Properties getProperties() {
    return null != properties ? new Properties( properties ) : new Properties();
  }

}