package de.ars.daojones.internal.runtime.test.junit;

import java.util.Stack;

import de.ars.daojones.runtime.configuration.provider.ConfigurationException;
import de.ars.daojones.runtime.test.TestConnectionFactory;

public final class TestContext {

  public static interface TestSetup {

    void configure( TestConnectionFactory testConnectionFactory ) throws ConfigurationException;

  }

  // Initialization per Test Class (isolated)
  private static final ThreadLocal<TestSetup> testSetupThreadLocal = new ThreadLocal<TestSetup>();
  // Initialization per Test Suite (shared) - use Stack because of nested suites
  private static final Stack<TestSetup> testSetupGlobal = new Stack<TestSetup>();

  private TestContext() {
    super();
  }

  public static TestSetup getTestSetup() {
    TestSetup result = TestContext.testSetupThreadLocal.get();
    if ( null == result && !TestContext.testSetupGlobal.isEmpty() ) {
      result = TestContext.testSetupGlobal.peek();
    }
    return result;
  }

  public static void setTestSetup( final TestSetup testSetup, final boolean threaded ) {
    if ( threaded ) {
      TestContext.testSetupThreadLocal.set( testSetup );
    } else {
      TestContext.testSetupGlobal.push( testSetup );
    }
  }

  public static void removeTestSetup( final boolean threaded ) {
    if ( threaded ) {
      TestContext.testSetupThreadLocal.remove();
    } else {
      if ( !TestContext.testSetupGlobal.isEmpty() ) {
        TestContext.testSetupGlobal.pop();
      }
    }
  }

}