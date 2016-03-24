package de.ars.daojones.runtime.docs.test_support;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.ars.daojones.runtime.connections.ConnectionProvider;
import de.ars.daojones.runtime.test.junit.Config;
import de.ars.daojones.runtime.test.junit.ConfigAnnotations;
import de.ars.daojones.runtime.test.junit.ConfigType;
import de.ars.daojones.runtime.test.junit.Configs;
import de.ars.daojones.runtime.test.junit.DaoJones;
import de.ars.daojones.runtime.test.junit.Inject;

@RunWith(DaoJones.class)
/*
 * Configure test-default application with custom connections.
 * This is a test-class-wide configuration,
 * i.e. valid for all test methods within this class.
 */
@Config("ConfiguredTestConnections.xml")
/*
 * The XML file is searched within the classpath relative to the test class.
 * To search the file at an absolute position within the classpath,
 * start the path with a slash "/"
 */
public class ConfiguredTest {

  @Test
  /*
   * Multiple configurations bundled with @Configs.
   * These configurations are only used for this test method. 
   */
  @Configs({
      // Configure connections for the custom application "mytest"
      @Config(application = "mytest", value = "/mytest-connections.xml"),
      // Configure beans for the custom application "mytest"
      @Config(application = "mytest", value = "/mytest-beans.xml", type = ConfigType.BEANS) })
  /*
   * Scan classpath for annotations for application "mytest".
   * This is necessary because of the customized bean model configuration upon.
   */
  @ConfigAnnotations(application = "mytest")
  // To get any element of "mytest", you have to use @Inject for parameters too.
  public void testSth1(@Inject(application = "mytest") final ConnectionProvider cp) {
    // your test goes here...
  }

}
