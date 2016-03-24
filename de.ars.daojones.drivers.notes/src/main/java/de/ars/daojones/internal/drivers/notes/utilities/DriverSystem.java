package de.ars.daojones.internal.drivers.notes.utilities;

import java.util.List;
import java.util.logging.Level;

public final class DriverSystem {

  public static final String PROPERTY_DEBUG = "debug";

  private static final Messages bundle = Messages.create( "utilities.DriverSystem" );
  private static final String LOCAL_DEBUGGING_ARGUMENT = "-agentlib:jdwp";
  private static final String REMOTE_DEBUGGING_ARGUMENT = "runjdwp:";
  private static final boolean DEBUGGING;

  static {
    boolean debugging = Boolean.valueOf( System.getProperty( DriverSystem.PROPERTY_DEBUG ) );
    if ( !debugging ) {
      final List<String> arguments = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments();
      for ( final String argument : arguments ) {
        if ( argument.toLowerCase().startsWith( DriverSystem.LOCAL_DEBUGGING_ARGUMENT.toLowerCase() )
                || argument.toLowerCase().startsWith( DriverSystem.REMOTE_DEBUGGING_ARGUMENT.toLowerCase() ) ) {
          debugging = true;
          break;
        }
      }
    }
    DEBUGGING = debugging;
    if ( debugging ) {
      DriverSystem.bundle.log( Level.INFO, "debugging.enabled" );
    }
  }

  private DriverSystem() {
    super();
  }

  /**
   * Returns <tt>true</tt>, if the driver debugging mode is enabled. To enable
   * the debugging mode, specify the system property
   * <tt>{@value DriverSystem#PROPERTY_DEBUG}=true</tt>. If the JVM is running
   * in debug mode, the driver debugging mode is enabled per default.
   * 
   * @return <tt>true</tt>, if the driver debugging mode is enabled, otherwise
   *         <tt>false</tt>
   */
  public static boolean isDebugging() {
    return DriverSystem.DEBUGGING;
  }
}
