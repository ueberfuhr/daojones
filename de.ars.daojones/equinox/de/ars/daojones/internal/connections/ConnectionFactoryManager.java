package de.ars.daojones.internal.connections;

import static de.ars.daojones.internal.Activator.log;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;

import de.ars.daojones.connections.ConnectionFactoryMetaData;

/**
 * A class managing the registered Connection Factories.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class ConnectionFactoryManager {

  /* *********************************************************
   *   S I N G L E T O N   -   I M P L E M E N T A T I O N   *
   ********************************************************* */

  private static ConnectionFactoryManager theInstance;

  private ConnectionFactoryManager() {
    super();
  }

  /**
   * Creates the singleton instance of a {@link ConnectionFactoryManager}.
   * 
   * @return the singleton instance of a {@link ConnectionFactoryManager}
   */
  public static ConnectionFactoryManager getInstance() {
    if ( null == theInstance )
      theInstance = new ConnectionFactoryManager();
    return theInstance;
  }

  /* *********************************************************
   *    I N S T A N C E   -   I M P L E M E N T A T I O N    *
   ********************************************************* */

  private Map<String, ConnectionFactoryMetaData> factories = new HashMap<String, ConnectionFactoryMetaData>();

  /**
   * Registers a {@link ConnectionFactoryMetaData}.
   * 
   * @param metadata
   *          the {@link ConnectionFactoryMetaData}
   */
  public void register( ConnectionFactoryMetaData metadata ) {
    this.factories.put( metadata.getId(), metadata );
    log( IStatus.INFO, "Registered connection factory class for \""
        + metadata.getName() + "\"." );
  }

  /**
   * Returns a {@link Map} containing {@link ConnectionFactoryMetaData} objects
   * by their id.
   * 
   * @return the {@link Map} containing {@link ConnectionFactoryMetaData}
   *         objects by their id
   */
  public Map<String, ConnectionFactoryMetaData> getFactories() {
    return new HashMap<String, ConnectionFactoryMetaData>( this.factories );
  }

  /**
   * Returns a {@link ConnectionFactoryMetaData} by id.
   * 
   * @param id
   *          the id
   * @return the {@link ConnectionFactoryMetaData}
   */
  public ConnectionFactoryMetaData getFactory( String id ) {
    return this.factories.get( id );
  }

}
