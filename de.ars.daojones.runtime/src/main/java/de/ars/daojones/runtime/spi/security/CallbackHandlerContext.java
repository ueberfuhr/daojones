package de.ars.daojones.runtime.spi.security;

import java.util.Properties;

/**
 * A transfer object that encapsulates all information that is available for
 * creating a callback handler instance.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH
 * @since 2.0.0
 */
public interface CallbackHandlerContext {

  /**
   * Returns the application id.
   * 
   * @return the application id
   */
  String getApplication();

  /**
   * Returns the custom properties. This will return a copy, so changes could be
   * made and do not affect the original properties source.
   * 
   * @return the custom properties
   */
  Properties getProperties();

}