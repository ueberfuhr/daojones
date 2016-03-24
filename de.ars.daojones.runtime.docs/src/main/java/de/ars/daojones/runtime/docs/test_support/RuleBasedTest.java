package de.ars.daojones.runtime.docs.test_support;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.test.junit.Config;
import de.ars.daojones.runtime.test.junit.DaoJones;

@SuppressWarnings("unused")
public class RuleBasedTest {

  // The runner class provides the rule.
  @Rule
  public DaoJones.Rule dj = DaoJones.asRule(this);

  @Before
  public void init() {
    // Access to the environment using the rule object.
    final ConnectionProvider cp = dj.getConnectionProvider();
  }

  @Test
  public void testSth() {
    // your test goes here - accessing the rule is possible too
  }

}
