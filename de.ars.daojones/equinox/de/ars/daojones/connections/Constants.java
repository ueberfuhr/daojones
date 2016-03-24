package de.ars.daojones.connections;

import org.eclipse.core.runtime.QualifiedName;

import de.ars.daojones.internal.Activator;
import de.ars.daojones.internal.connections.ConnectionsFileDescriber;

/**
 * An interface holding constants concerning connections and connection files.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public interface Constants {

  /**
   * The name of the persistent property that a connection configuration file
   * may have.
   */
  public static final QualifiedName FILE_IDENTIFIER_KEY = new QualifiedName(
      Activator.PLUGIN_ID, "connectionfile" );
  /**
   * The value of the persistent property that a connection configuration file
   * may have.
   */
  public static final String FILE_IDENTIFIER_VALUE = "true";
  
  /**
   * The id of the XML connection configuration content type.
   */
  public static final String CONNECTIONSFILE_CONTENTTYPE = ConnectionsFileDescriber.CONTENT_TYPE;

}
