package de.ars.daojones.runtime.docs.test_support;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.ars.daojones.runtime.configuration.context.DaoJonesContextConfiguration;
import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.context.DaoJonesContext;
import de.ars.daojones.runtime.test.junit.DaoJones;
import de.ars.daojones.runtime.test.junit.Inject;

@RunWith(DaoJones.class)
public class Simple2Test {

  // Field Injection
  @Inject
  private ConnectionProvider cp;

  // Constructor Injection
  public Simple2Test(final DaoJonesContext ctx) {
    super();
    // access the context
  }

  // Parameter Injection
  @Test
  public void testSth(final DaoJonesContextConfiguration config) {
    // test your code
  }

}
