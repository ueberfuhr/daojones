package de.ars.daojones.runtime.docs.integration;

import javax.annotation.ManagedBean;

import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.test.junit.Inject;

@ManagedBean
public class SampleWebBean {

  @Inject
  private ConnectionProvider djConnectionprovider;

  @Inject
  private Connection<Memo> connection;

}
