package de.ars.daojones.internal.runtime.security;

import de.ars.daojones.internal.runtime.utilities.ConcurrentLazyHashMap;
import de.ars.daojones.runtime.spi.cache.CallbackHandlerException;

public class CredentialScope<K> extends ConcurrentLazyHashMap<K, CredentialStore, CallbackHandlerException> {

}
