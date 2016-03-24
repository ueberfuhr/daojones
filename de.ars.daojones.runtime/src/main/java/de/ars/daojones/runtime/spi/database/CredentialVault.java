package de.ars.daojones.runtime.spi.database;

import javax.security.auth.callback.CallbackHandler;

import de.ars.daojones.runtime.configuration.context.ConnectionModel;
import de.ars.daojones.runtime.spi.security.Credential;

/**
 * A value for the credentials available for a single connection. Each
 * connection has a credential (locally by using <tt>&lt;credential></tt> or
 * global by using <tt>&lt;credential-reference></tt>). If no credential is
 * specified, a default local credential exists.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public interface CredentialVault {

  /**
   * The scope of the credential. This is used to differ when to ask the
   * callback handler again for the credential, and when to use a shared
   * credential that was already requested.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
   * @since 2.0
   */
  public enum Scope {
    /**
     * The credential is requested any time it is needed. It is not cached.
     */
    TRANSIENT,
    /**
     * The credential is requested for any connection (i.e. the
     * {@link ConnectionModel}) that needs this credential. It is valid as long
     * as the connection exists.
     */
    CONNECTION,
    /**
     * The credential is requested for any credential model (i.e. the
     * {@link de.ars.daojones.runtime.configuration.connections.Credential}). If
     * the credential is a local one, it is requested for any connection (so
     * this scope is the same like {@link #CONNECTION}). If the credential is a
     * global one, it is shared among multiple connections with multiple
     * drivers, but within the same application.
     */
    CREDENTIAL,
    /**
     * The credential is requested once and shared for all connections and
     * credentials within the same application.
     */
    APPLICATION;
  }

  /**
   * A common interface that is used for requesting a credential. This is used
   * to create the callbacks that are necessary to find the credential.
   * 
   * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
   * @since 2.0
   * @param <T>
   *          the credential type
   */
  public interface CredentialRequest<T extends Credential> {

    /**
     * Executes the callbacks.
     * 
     * @param callbackHandler
     *          the callback handler.
     * @return the credential
     * @throws CredentialVaultException
     *           if an error occured during executing the callbacks and creating
     *           the credential.
     */
    T execute( CallbackHandler callbackHandler ) throws CredentialVaultException;

  }

  /**
   * Requests for a credential.
   * 
   * @param t
   *          the credential class
   * @param scope
   *          the credential scope
   * @param request
   *          the credential request
   * @return the credential
   * @throws CredentialVaultException
   *           if a credential is not supported by the callback handler in a way
   *           that a credential could not be resolved, or any other error
   *           occured
   */
  <T extends Credential> T requestCredential( Class<T> t, Scope scope, CredentialRequest<T> request )
          throws CredentialVaultException;

}
