package de.ars.daojones.runtime.spi.security;

import javax.security.auth.callback.CallbackHandler;

import de.ars.daojones.runtime.spi.cache.CallbackHandlerException;

/**
 * A factory that creates instances of {@link CallbackHandler}. This is used for
 * connections that need authentication using JAAS.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public interface CallbackHandlerFactory {

  /**
   * Creates the callback handler instance.
   * 
   * @param context
   *          the context
   * @return the callback handler
   * @throws CallbackHandlerException
   *           if creating the callback handler failed
   */
  CallbackHandler createCallbackHandler( CallbackHandlerContext context ) throws CallbackHandlerException;

}
