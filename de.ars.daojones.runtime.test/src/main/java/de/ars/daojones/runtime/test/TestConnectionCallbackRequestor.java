package de.ars.daojones.runtime.test;

import de.ars.daojones.runtime.configuration.connections.Connection;
import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.spi.database.CredentialVault;
import de.ars.daojones.runtime.spi.database.CredentialVaultException;

/**
 * Instances of this type are responsible to request callbacks for the
 * connection.
 *
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2016
 * @since 2.0
 */
public interface TestConnectionCallbackRequestor {

  /**
   * Requests the callbacks.
   *
   * @param model
   *          the connection model
   * @param vault
   *          the credential vault
   * @throws CredentialVaultException
   *           if resolving the credentials occured an error
   * @throws ConfigurationException
   */
  void callback( final Connection model, CredentialVault vault )
          throws CredentialVaultException, ConfigurationException;

}
