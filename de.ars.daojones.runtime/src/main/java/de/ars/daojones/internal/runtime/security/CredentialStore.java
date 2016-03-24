package de.ars.daojones.internal.runtime.security;

import de.ars.daojones.internal.runtime.utilities.ConcurrentLazyHashMap;
import de.ars.daojones.runtime.spi.database.CredentialVaultException;
import de.ars.daojones.runtime.spi.security.Credential;

class CredentialStore extends ConcurrentLazyHashMap<String, Credential, CredentialVaultException> {

}
