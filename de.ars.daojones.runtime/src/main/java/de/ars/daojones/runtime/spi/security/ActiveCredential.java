package de.ars.daojones.runtime.spi.security;

import javax.security.auth.login.LoginException;

/**
 * A common interface for all credentials that can be used to execute the login
 * by themselves.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 * @param <P>
 *          the parameter type that is necessary for login
 * @param <R>
 *          the result type which should be a reference to the login result,
 *          e.g. a session or a token
 */
public interface ActiveCredential<P, R> extends Credential {

  /**
   * Executes the login.
   * 
   * @param parameters
   *          the parameters
   * @return the login result (e.g. a session or a token)
   * @throws LoginException
   *           if the login failed
   */
  R login( P parameters ) throws LoginException;

}
