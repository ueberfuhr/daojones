package de.ars.daojones.runtime.docs.integration;

import javax.annotation.ManagedBean;

import de.ars.daojones.integration.cdi.DaoJonesApplication;
import de.ars.daojones.runtime.configuration.context.DaoJonesContextConfiguration;
import de.ars.daojones.runtime.connections.Connection;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.context.Application;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.test.junit.Inject;

@ManagedBean
public class SampleBean {

  @Inject
  private DaoJonesContext djContext;

  @Inject
  private DaoJonesContextConfiguration djConfig;

  @Inject
  @DaoJonesApplication("myApp")
  private Application djApplication;

  @Inject
  @DaoJonesApplication("myApp")
  private ConnectionProvider djConnectionprovider;

  @Inject
  @DaoJonesApplication("myApp")
  private Connection<Memo> connection;

}
