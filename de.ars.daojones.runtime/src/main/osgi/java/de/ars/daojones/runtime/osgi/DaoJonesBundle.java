package de.ars.daojones.runtime.osgi;

import de.ars.daojones.internal.runtime.osgi.Activator;
import de.ars.daojones.runtime.context.DaoJonesContext;

/**
 * This is an interface to access to DaoJones environment managed by the bundle.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2014
 * @since 2.0
 */
public final class DaoJonesBundle {

  private DaoJonesBundle() {
    super();
  }

  /**
   * Returns the DaoJones context. If the DaoJones runtime plugin is stopped,
   * this method returns <tt>null</tt>.
   * 
   * @return the DaoJones context
   */
  public static DaoJonesContext getDaoJonesContext() {
    final Activator activator = Activator.getDefault();
    return null == activator ? null : activator.getDaoJonesContext();
  }

}
