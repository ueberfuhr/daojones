package de.ars.daojones.drivers.notes.security;

import javax.security.auth.callback.CallbackHandler;

import de.ars.daojones.internal.drivers.notes.security.LocalClientCallbackHandler;

/**
 * A utility class that provides methods to create common callback handlers.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2016
 * @since 2.0
 */
public final class CallbackHandlers {

  private CallbackHandlers() {
  }

  /**
   * Creates a callback handler to access the local notes client.
   *
   * @return the callback handler
   */
  public static Class<? extends CallbackHandler> localClient() {
    return LocalClientCallbackHandler.class;
  }

}
