package de.ars.daojones.drivers.notes.security;

import lotus.domino.Session;
import de.ars.daojones.runtime.spi.security.ActiveCredential;

/**
 * A credential that can be used to create a Notes session.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2015
 * @since 2.0
 */
public abstract class SessionFactoryCredential implements ActiveCredential<AuthorityCredential, Session> {

  private static final long serialVersionUID = 1L;

}
